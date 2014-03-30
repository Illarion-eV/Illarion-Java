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
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.java.archives.Manifest
import org.gradle.api.java.archives.internal.DefaultManifest
import org.gradle.util.ConfigureUtil

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ConvertPluginConvention {
    private ProjectInternal project

    def String atlasNameExtension
    def File privateKey
    def File resourceDirectory
    def FileTree resources
    final def File outputDirectory

    List metaInf

    DefaultManifest manifest

    ConvertPluginConvention(ProjectInternal project) {
        this.project = project
        manifest = manifest()
        metaInf = []
        resourceDirectory = new File(project.projectDir, "src/main/resources");
        resources = project.fileTree(dir: resourceDirectory)
        outputDirectory = new File(project.buildDir, "resources")
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
        return ConfigureUtil.configure(closure, new DefaultManifest(project.fileResolver));
    }
}
