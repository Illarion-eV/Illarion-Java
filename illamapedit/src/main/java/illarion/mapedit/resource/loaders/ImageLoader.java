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
package illarion.mapedit.resource.loaders;

import illarion.mapedit.resource.Resource;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Tim
 */
public class ImageLoader implements Resource {
    private static final Logger LOGGER = Logger.getLogger(ImageLoader.class);
    private static final ImageLoader INSTANCE = new ImageLoader();
    private static final Map<String, Image> IMAGES = new FastMap<String, Image>();


    private static final String[] FILES = {
            "sound.png",
            "messagebox_critical.png",
            "singleSelect.png",
            "viewmag.png",
            "viewmag1.png",
            "viewmag-.png",
            "viewmag+.png",
            "mapedit64.png",
            "fileopen.png",
            "filenew.png",
            "filesave.png",
            "editcopy.png",
            "editpaste.png",
            "editcut.png",
            "file_tiles.png",
            "file_items.png",
            "messagebox_warning.png",
            "viewGrid.png",
            "reload.png",
            "undo.png",
            "redo.png",
            "close.png",
            "help.png",
            "player_play.png",
    };

    private ImageLoader() {

    }

    @Override
    public void load() throws IOException {
        final Class<?> clazz = ImageLoader.class;
        for (final String file : FILES) {
            final String filePath = '/' + file;
            final String key = filePath.substring(1, filePath.length() - 4);
            final InputStream is = clazz.getResourceAsStream(filePath);
            if (is == null) {
                throw new IOException(filePath + " does not exist!");
            }
            IMAGES.put(key, ImageIO.read(is));
        }
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Images";
    }

    @Nonnull
    public static ImageLoader getInstance() {
        return INSTANCE;
    }

    public static Image getImage(final String key) {
        if (!IMAGES.containsKey(key)) {
            LOGGER.warn("Image [" + key + "] does not exist!");
            throw new RuntimeException("Image [" + key + "] does not exist!");
//            return null;
        }
        return IMAGES.get(key);
    }

    public static ResizableIcon getResizableIcon(final String key) {
        final Image image = getImage(key);

        final int height = image.getHeight(null);
        final int width = image.getWidth(null);
        final ResizableIcon resizeIcon =
                ImageWrapperResizableIcon.getIcon(image, new Dimension(width,
                        height));
        return resizeIcon;
    }

    @Nonnull
    public static ImageIcon getImageIcon(final String key) {
        return new ImageIcon(getImage(key));
    }
}
