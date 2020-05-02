/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
package illarion.client.gui;

import illarion.client.resources.data.AbstractEntityTemplate;
import illarion.client.resources.data.ItemTemplate;
import org.illarion.engine.nifty.IgeTextureRenderImage;

import javax.annotation.Nonnull;

/**
 * This implementation of a slick render image is used to show a image that is usually used in the game graphics,
 * inside the elements of the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class TextureRenderImage extends IgeTextureRenderImage {
    /**
     * Create this render image that refers to a specified entity.
     *
     * @param entity the entity the image refers to
     */
    public TextureRenderImage(@Nonnull ItemTemplate entity) {
        super(entity.getGuiTexture());
    }
    public TextureRenderImage(@Nonnull AbstractEntityTemplate entity) {
        super(entity.getSprite().getFrame(0));
    }
}
