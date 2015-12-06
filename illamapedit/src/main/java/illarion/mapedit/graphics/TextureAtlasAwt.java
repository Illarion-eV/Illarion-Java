/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.mapedit.graphics;

import illarion.common.graphics.TextureAtlas;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the texture atlas implementation that uses AWT images.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class TextureAtlasAwt implements TextureAtlas<BufferedImage> {
    /**
     * The map to assign the texture names with the images of this atlas.
     */
    @Nonnull
    private final Map<String, BufferedImage> textures;

    /**
     * Create a new instance of the texture atlas.
     *
     * @param image the buffered image that contains the entire texture
     * @param textureDef the XML document that contains the locations of the sub-images on the large image
     */
    public TextureAtlasAwt(@Nonnull BufferedImage image, @Nonnull Document textureDef) {
        textures = new HashMap<>();

        NodeList list = textureDef.getElementsByTagName("sprite");
        for (int i = 0; i < list.getLength(); i++) {
            Element element = (Element) list.item(i);

            String name = element.getAttribute("name");
            int x = Integer.parseInt(element.getAttribute("x"));
            int y = Integer.parseInt(element.getAttribute("y"));
            int width = Integer.parseInt(element.getAttribute("width"));
            int height = Integer.parseInt(element.getAttribute("height"));

            textures.put(name, image.getSubimage(x, y, width, height));
        }
    }

    @Override
    public BufferedImage getTexture(String texture) {
        return textures.get(texture);
    }
}
