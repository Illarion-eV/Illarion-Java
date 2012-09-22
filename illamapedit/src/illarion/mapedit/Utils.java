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
package illarion.mapedit;

/**
 * @author Tim
 */

import illarion.common.util.Location;
import org.apache.log4j.Logger;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * A small set of static utility functions that help at some points.
 *
 * @author Martin Karing, Tim
 * @since 1.00
 */
public final class Utils {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Utils.class);

    private Utils() {
    }

    public static ResizableIcon getResizableIconFromResource(
            final String resource) {
        final Image image;
        try {
            image = ImageIO.read(Utils.class.getClassLoader()
                    .getResource(resource));
        } catch (final IOException e) {
            LOGGER.error("Failed to read image: \"" + resource + '"');
            return null;
        }
        final int height = image.getHeight(null);
        final int width = image.getWidth(null);
        final ResizableIcon resizeIcon =
                ImageWrapperResizableIcon.getIcon(image, new Dimension(width,
                        height));
        return resizeIcon;
    }

    public static Icon getIconFromResource(final String resource) {
        final URL imgURL = Utils.class.getClassLoader()
                .getResource(resource);
        if (imgURL != null) {
            return new ImageIcon(imgURL, resource);
        } else {
            LOGGER.warn("Can't load resource " + resource);
            return null;
        }

    }

    public static int getMapXFormDisp(final int x, final int y, final int transX, final int transY, final float zoom) {
        float xr = (x - transX) / zoom;
        float yr = (y - transY) / zoom;
        Location mapPos = new Location();
        mapPos.setDC((int) xr, (int) yr);
        return mapPos.getScX();
    }

    public static int getMapYFormDisp(final int x, final int y, final int transX, final int transY, final float zoom) {
        float xr = (x - transX) / zoom;
        float yr = (y - transY) / zoom;
        Location mapPos = new Location();
        mapPos.setDC((int) xr, (int) yr);
        return mapPos.getScY() - 1;
    }
}
