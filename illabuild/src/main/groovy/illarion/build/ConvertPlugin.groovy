package illarion.build

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * This is the plugin that applies the resource converter of Illarion to a gradle build project.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ConvertPlugin implements Plugin<Project> {
    @Override
    void apply(final Project project) {
        if (!project.plugins.hasPlugin('java')) {
            project.apply plugin: 'java'
        }

        project.extensions.create("converter", ConverterExtension)

        project.tasks.replace("processResources", ResourceConverter)
    }
}
