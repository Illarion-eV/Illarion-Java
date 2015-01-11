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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.illarion.engine.backend.shared.AbstractFontManager;
import org.illarion.engine.graphic.Font;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * This is the font manager implementation of the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxFontManager extends AbstractFontManager {
    /**
     * The file system handler of libGDX.
     */
    @Nonnull
    private final Files files;

    /**
     * The texture manager used to receive the textures that contain the glyphs.
     */
    @Nonnull
    private final GdxTextureManager textureManager;

    /**
     * Create a new font manager instance.
     *
     * @param files the file system handler of libGDX that should be used to load the font data
     * @param textureManager the texture manager that supplies the texture data
     */
    GdxFontManager(@Nonnull Files files, @Nonnull GdxTextureManager textureManager) {
        this.files = files;
        this.textureManager = textureManager;
    }

    @Nonnull
    @Override
    protected Font buildFont(@Nonnull String fntRef, @Nonnull String imageRoot, @Nullable Font outlineFont)
            throws IOException {
        String imageName = getImageName(fntRef);
        GdxTexture imageTexture = (GdxTexture) textureManager.getTexture(imageRoot, imageName);
        if (imageTexture == null) {
            throw new IOException("Failed to load required image: " + imageRoot + imageName);
        }
        GdxFont gdxOutlineFont = (outlineFont instanceof GdxFont) ? (GdxFont) outlineFont : null;

        return new GdxFont(new BitmapFont(files.internal(fntRef), imageTexture.getTextureRegion(), true), gdxOutlineFont);
    }
}
