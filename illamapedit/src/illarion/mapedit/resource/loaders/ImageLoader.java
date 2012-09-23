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

import illarion.mapedit.crash.exceptions.ResourceException;
import illarion.mapedit.resource.Resource;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tim
 */
public class ImageLoader implements Resource {

    private static final ImageLoader INSTANCE = new ImageLoader();
    private static final Map<String, Image> IMAGES = new HashMap<String, Image>();


    private static final String[] files = {
            "/sound.png",
            "/messagebox_critical.png",
            "/singleSelect.png",
            "/viewmag.png",
            "/viewmag1.png",
            "/viewmag-.png",
            "/viewmag+.png",
            "/mapedit64.png",
            "/fileopen.png",
            "/filenew.png",
            "/filesave.png",
            "/editcopy.png",
            "/editpaste.png",
            "/editcut.png",
            "/file_tiles.png",
            "/file_items.png",
            "/messagebox_warning.png",
            "/viewGrid.png",
            "/sound.png",
    };

    private ImageLoader() {

    }

    @Override
    public void load() throws IOException {
        final Class<?> clazz = ImageLoader.class;
        for (final String f : files) {
            String key = f.substring(1, f.length() - 4);
            InputStream is = clazz.getResourceAsStream(f);
            if (is == null) {
                throw new IOException(f + " does not exist!");
            }
            IMAGES.put(key, ImageIO.read(is));
        }
    }

    @Override
    public String getDescription() {
        return "Images";
    }

    public static ImageLoader getInstance() {
        return INSTANCE;
    }

    public static Image getImage(final String key) {
        if (!IMAGES.containsKey(key))
            throw new ResourceException(key + " does not exist!");
        return IMAGES.get(key);
    }

    public static ResizableIcon getResizableIcon(final String key) {
        Image image = getImage(key);

        final int height = image.getHeight(null);
        final int width = image.getWidth(null);
        final ResizableIcon resizeIcon =
                ImageWrapperResizableIcon.getIcon(image, new Dimension(width,
                        height));
        return resizeIcon;
    }

    public static ImageIcon getImageIcon(final String key) {
        return new ImageIcon(getImage(key));
    }
}
