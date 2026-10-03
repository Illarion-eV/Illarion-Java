/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import org.gradle.api.file.FileTree
import org.gradle.api.Project
import org.gradle.api.java.archives.Manifest

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ConvertPluginConvention {
    private Project project

    def String atlasNameExtension
    def File privateKey
    def File resourceDirectory
    def FileTree resources
    final def File outputDirectory

    List metaInf

    Manifest manifest

    ConvertPluginConvention(Project project) {
        this.project = project
        manifest = manifest()
        metaInf = []
        resourceDirectory = new File(project.projectDir, "src/main/resources");
        resources = project.fileTree(dir: resourceDirectory)
        outputDirectory = project.layout.buildDirectory.dir("converted-resources").get().asFile
    }

    public def setResourceDirectory(File dir) {
        resourceDirectory = dir;
        resources = project.fileTree(dir: resourceDirectory)
    }

    def privateKey(File file) {
        privateKey = file
    }

    /**
     * Creates a new instance of a {@link Manifest}.
     */
    public Manifest manifest() {
        return manifest(null);
    }

    /**
     * Creates and configures a new instance of a {@link Manifest}. The given closure configures
     * the new manifest instance before it is returned.
     *
     * @param closure The closure to use to configure the manifest.
     */
    public Manifest manifest(Closure closure) {
        Manifest result = project.extensions.getByType(org.gradle.api.plugins.JavaPluginExtension).manifest()

        if (closure != null) {
            project.configure(result, closure)
        }

        return result
    }
}
