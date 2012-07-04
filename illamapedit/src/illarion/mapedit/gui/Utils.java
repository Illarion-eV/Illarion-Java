/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui;

/**
 * @author Tim
 */

import org.apache.log4j.Logger;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * A small set of static utility functions that help at some points.
 *
 * @author Martin Karing
 * @since 1.00
 */
public final class Utils {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Utils.class);

    public static ResizableIcon getResizableIconFromResource(
            final String resource) {
        Image image;
        try {
            image = ImageIO.read(Utils.class.getClassLoader()
                    .getResource(resource));
        } catch (final IOException e) {
            LOGGER.error("Failed to read image: \"" + resource + "\"");
            return null;
        }
        final int height = image.getHeight(null);
        final int width = image.getWidth(null);
        final ResizableIcon resizeIcon =
                ImageWrapperResizableIcon.getIcon(image, new Dimension(width,
                        height));
        return resizeIcon;
    }

    public static void selectAndOpenMapDir() {
        JFileChooser ch = new JFileChooser();
        ch.setDialogType(JFileChooser.OPEN_DIALOG);
        ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (ch.showOpenDialog(MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File dir = ch.getSelectedFile();
        selectAndOpenMap(dir);
    }

    private static void selectAndOpenMap(final File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        String[] files = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".tiles.txt");
            }
        });
        for (String f : files) {
            System.out.println(f);
        }
    }
}
