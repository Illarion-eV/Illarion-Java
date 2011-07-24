/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui.swing;

import java.awt.Dimension;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.pushingpixels.flamingo.api.common.icon.IcoWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

/**
 * This utility class contains a set of functions that are used at building up
 * the swing GUI.
 * 
 * @author Martin Karing
 * @since 1.01
 * @version 1.01
 */
public final class Utils {

    /**
     * The dimension that is used by default for all for all newly load icons.
     */
    private static final Dimension DEFAULT_DIM = new Dimension(64, 64);

    /**
     * The extension that is checked to identify a resource as icon.
     */
    @SuppressWarnings("nls")
    private static final String ICON_ENDING = ".ico";
    
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Utils.class);

    /**
     * Get a resizable icon from a resource. Depending on the type of icon its
     * load properly.
     * 
     * @param resource the resource that is load
     * @return the load icon
     */
    public static Icon getIconFromResource(final String resource) {

        final URL resourceUrl =
            Utils.class.getClassLoader().getResource(resource);
        if (resourceUrl == null) {
            LOGGER.error("Failed to find: " + resource);
            return null;
        }
        if (resource.endsWith(ICON_ENDING)) {
            return IcoWrapperResizableIcon.getIcon(resourceUrl, DEFAULT_DIM);
        }
        return new ImageIcon(resourceUrl);
    }

    /**
     * Get a resizable icon from a resource. Depending on the type of icon its
     * load properly.
     * 
     * @param resource the resource that is load
     * @return the load icon
     */
    public static ResizableIcon getResizableIconFromResource(
        final String resource) {

        final URL resourceUrl =
            Utils.class.getClassLoader().getResource(resource);
        if (resource.endsWith(ICON_ENDING)) {
            return IcoWrapperResizableIcon.getIcon(resourceUrl, DEFAULT_DIM);
        }
        return ImageWrapperResizableIcon.getIcon(resourceUrl, DEFAULT_DIM);
    }
}
