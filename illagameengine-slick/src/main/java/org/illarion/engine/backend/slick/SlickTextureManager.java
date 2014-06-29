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
package org.illarion.engine.backend.slick;

import org.illarion.engine.backend.shared.AbstractTextureManager;
import org.illarion.engine.graphic.Texture;
import org.newdawn.slick.Image;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.LoadableImageData;
import org.newdawn.slick.opengl.PNGImageData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * The texture manager that takes care for loading and providing the texture data for the Slick 2D backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickTextureManager extends AbstractTextureManager<ImageData> {
    @Nullable
    @Override
    protected ImageData loadTextureData(@Nonnull String textureName) {
        LoadableImageData imageData = new PNGImageData();

        @Nullable InputStream in = null;
        try {
            in = new BufferedInputStream(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream(textureName));
            imageData.loadImage(in);
        } catch (@Nonnull IOException e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (@Nonnull IOException ignored) {
                }
            }
        }
        return imageData;
    }

    @Nullable
    @Override
    protected Texture loadTexture(@Nonnull String resource, @Nonnull ImageData preLoadData) {
        return new SlickTexture(new Image(preLoadData));
    }
}
