/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar

/**
 * Resource converter using Gradle's public extension and Java component APIs.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ConvertPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.pluginManager.apply('java-library')
        def converter = project.extensions.create('converter', ConvertPluginConvention, project)
        def legacyDependencies = project.configurations.maybeCreate('compile')
        legacyDependencies.canBeConsumed = false
        legacyDependencies.canBeResolved = false

        project.configurations.named('api') {
            extendsFrom legacyDependencies
        }

        def conversion = project.tasks.register('convertResources', ResourceConverter) {
            description = 'Convert the resources for the Illarion applications.'
            group = 'build'

            atlasName.set(project.provider {
                converter.atlasNameExtension ?: project.name
            })

            privateKey.set(project.layout.file(project.provider {
                converter.privateKey
            }))

            resources.from(project.provider {
                converter.resources
            })

            resourceDirectory.set(project.layout.dir(project.provider {
                converter.resourceDirectory
            }))

            outputDirectory.set(project.layout.dir(project.provider {
                converter.outputDirectory
            }))
        }

        project.tasks.named('jar', Jar) {
            from(conversion.flatMap {
                it.outputDirectory
            })

            manifest.from(converter.manifest)

            metaInf.from(project.provider {
                converter.metaInf
            })
        }

        // Do not package the unconverted inputs alongside the converted output.
        project.tasks.named('processResources') {
            enabled = false
        }

        project.tasks.register('buildConvert') {
            description = 'Assemble converted resources.'
            group = 'build'
            dependsOn project.tasks.named('assemble')
        }
    }
}
