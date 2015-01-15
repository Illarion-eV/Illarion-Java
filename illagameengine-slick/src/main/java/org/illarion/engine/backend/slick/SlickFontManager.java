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
package org.illarion.engine.backend.slick;

import org.illarion.engine.backend.shared.AbstractFontManager;
import org.illarion.engine.graphic.Font;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * This is the font manager implementation for Slick.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickFontManager extends AbstractFontManager {
    /**
     * The texture manager used to fetch the images.
     */
    @Nonnull
    private final SlickTextureManager texManager;

    /**
     * Create a new instance of the font manager.
     *
     * @param textureManager the texture manager used to fetch the required images
     */
    SlickFontManager(@Nonnull SlickTextureManager textureManager) {
        texManager = textureManager;
    }

    @Nonnull
    @Override
    protected Font buildFont(
            @Nonnull String fntRef,
            @Nonnull String imageRoot,
            @Nullable Font outlineFont) throws IOException {
        try {
            String imageName = getImageName(fntRef);
            SlickTexture texture = (SlickTexture) texManager.getTexture(imageRoot, imageName);
            if (texture == null) {
                throw new IOException("Failed to load required image: " + imageRoot + imageName);
            }
            return new SlickFont(fntRef, texture);
        } catch (@Nonnull SlickEngineException e) {
            throw new IOException(e);
        }
    }
}
