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

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.illarion.engine.graphic.Font;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The font implementation of the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxFont implements Font {
    /**
     * The internal bitmap font that is used.
     */
    @Nonnull
    private final BitmapFont bitmapFont;

    @Nullable
    private final GdxFont outlineFont;

    /**
     * Create a new instance of the libGDX font implementation.
     *
     * @param bitmapFont the bitmap font that is wrapped by this instance
     * @param outlineFont the outline font in case there is any
     */
    GdxFont(@Nonnull BitmapFont bitmapFont, @Nullable GdxFont outlineFont) {
        this.bitmapFont = bitmapFont;
        this.outlineFont = outlineFont;
    }

    @Override
    public void dispose() {
        bitmapFont.dispose();
    }

    @Override
    public int getLineHeight() {
        return Math.round(bitmapFont.getLineHeight());
    }

    @Override
    public int getWidth(@Nonnull CharSequence text) {
        return Math.round(bitmapFont.getBounds(text).width);
    }

    @Override
    public int getAdvance(char current, char next) {
        @Nullable BitmapFont.Glyph currentGlyph = bitmapFont.getData().getGlyph(current);
        if (currentGlyph == null) {
            return 0;
        }
        return currentGlyph.getKerning(next) + currentGlyph.xadvance;
    }

    /**
     * Get the internal bitmap font.
     *
     * @return the internal font
     */
    @Nonnull
    BitmapFont getBitmapFont() {
        return bitmapFont;
    }

    @Nullable
    BitmapFont getOutlineBitmapFont() {
        if (outlineFont != null) {
            return outlineFont.getBitmapFont();
        }
        return null;
    }
}
