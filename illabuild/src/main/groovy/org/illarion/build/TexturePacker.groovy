/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Build Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction

import javax.imageio.ImageIO

/**
 * This is the texture packer that creates the texture images.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class TexturePacker extends DefaultTask {
    /**
     * These are the files that are packed into the texture atlas files.
     */
    FileCollection sourceFiles

    /**
     * This is the target directory where the new texture files are created.
     */
    File targetDirectory

    @TaskAction
    def pack() {
        def imageData = new ArrayList<ImageInformation>()
        sourceFiles.each { file ->
            def img = ImageIO.read(file)
            if (img != null) {
                def info = new ImageInformation(source: file, hasAlpha: img.getColorModel().hasAlpha(),
                        hasColor: img.getColorModel().getNumColorComponents() > 1,
                        height: img.getHeight(), width: img.getWidth())
                imageData.add(info)
                logger.trace("Got ${file.toString()} as image.")
            } else {
                logger.trace("The file ${file.toString()} can't be decoded as image.")
            }
        }
        logger.debug("Located ${imageData.size()} images!")

        imageData.sort { info1, info2 ->
            if (info1.hasColor != info2.hasColor) {
                return (info1.hasColor ? 1 : -1)
            }
            if (info1.hasAlpha != info2.hasAlpha) {
                return (info1.hasAlpha ? 1 : -1)
            }
            if (info1.height != info2.height) {
                return info1.height <=> info2.height
            }
            return info1.width <=> info2.width
        }
        logger.debug("Sorting of images is done.")
    }
}
