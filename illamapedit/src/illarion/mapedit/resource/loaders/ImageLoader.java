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

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tim
 */
public class ImageLoader implements Resource {

    private static final ImageLoader INSTANCE = new ImageLoader();
    private static final Map<String, Image> IMAGES = new HashMap<String, Image>();


    private static final String[] files = {
            "/sound.png"
    };

    private ImageLoader() {

    }

    @Override
    public void load() throws IOException {
        final Class<?> clazz = ImageLoader.class;
        for (final String f : files) {
            IMAGES.put(f, ImageIO.read(clazz.getResourceAsStream(f)));
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
        return IMAGES.get(key);
    }
}
