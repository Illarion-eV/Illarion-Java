package illarion.build

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.java.JavaLibrary
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.internal.reflect.Instantiator
import org.gradle.language.base.plugins.LanguageBasePlugin

import javax.inject.Inject
import java.util.concurrent.Callable

/**
 * This is the plugin that applies the resource converter of Illarion to a gradle build project.
 *
 * @author Martin Karing &ltnitram@illarion.org&gt
 */
class ConvertPlugin implements Plugin<Project> {
    public static final String COMPILE_CONFIGURATION_NAME = "compile"

    public static final String BUILD_TASK_NAME = "build"

    private final Instantiator instantiator

    @Inject
    public ConvertPlugin(Instantiator instantiator) {
        this.instantiator = instantiator
    }

    @Override
    void apply(final Project project) {
        project.plugins.apply(BasePlugin)
        project.plugins.apply(LanguageBasePlugin)

        ConvertPluginConvention convention = new ConvertPluginConvention(project as ProjectInternal)
        project.convention.plugins.converter = convention

        project.configurations.create(COMPILE_CONFIGURATION_NAME)

        configureConfigurations(project)
        configureJar(project, convention)
        configureBuild(project)
        configureConvert(project, convention)
    }

    static void configureBuild(Project project) {
        DefaultTask buildTask = project.tasks.create(BUILD_TASK_NAME, DefaultTask.class)
        buildTask.description = "Assembles and tests this project."
        buildTask.group = BasePlugin.BUILD_GROUP
        buildTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME)
    }

    static void configureConvert(Project project, ConvertPluginConvention convention) {
        def task = project.tasks.create("convertResources", ResourceConverter)
        task.description = 'Convert the resources for the Illarion applications.'
        task.group = BasePlugin.BUILD_GROUP

        task.conventionMapping.atlasName = { convention.atlasNameExtension }
        task.conventionMapping.privateKey = { convention.privateKey }
        task.conventionMapping.resources = { convention.resources }
        task.conventionMapping.resourceDirectory = { convention.resourceDirectory }
        task.conventionMapping.outputDirectory = { convention.outputDirectory }
    }

    static void configureJar(Project project, ConvertPluginConvention convention) {
        def jar = project.tasks.create("jar", Jar)
        jar.manifest.from(convention.manifest)
        jar.description = "Assembles a jar archive containing the main classes."
        jar.group = BasePlugin.BUILD_GROUP
        jar.dependsOn("convertResources")
        jar.from(convention.outputDirectory)
        jar.metaInf.from(new Callable() {
            public Object call() throws Exception {
                return convention.metaInf
            }
        })

        def jarArtifact = new ArchivePublishArtifact(jar)
        def compileConfiguration = project.configurations.getByName(COMPILE_CONFIGURATION_NAME)

        compileConfiguration.artifacts.add(jarArtifact)
        project.extensions.getByType(DefaultArtifactPublicationSet).addCandidate(jarArtifact)
        project.components.add(new JavaLibrary(jarArtifact, compileConfiguration.allDependencies));
    }

    static void configureConfigurations(Project project) {
        def configurations = project.configurations
        def compileConfiguration = configurations.getByName(COMPILE_CONFIGURATION_NAME)

        configurations.getByName(Dependency.DEFAULT_CONFIGURATION).extendsFrom(compileConfiguration)
    }
}
