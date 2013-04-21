/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
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

    /**
     * Create a new instance of the libGDX font implementation.
     *
     * @param bitmapFont the bitmap font that is wrapped by this instance
     */
    GdxFont(@Nonnull final BitmapFont bitmapFont) {
        this.bitmapFont = bitmapFont;
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
    public int getWidth(@Nonnull final CharSequence text) {
        return Math.round(bitmapFont.getBounds(text).width);
    }

    @Override
    public int getAdvance(final char current, final char next) {
        @Nullable final BitmapFont.Glyph currentGlyph = bitmapFont.getData().getGlyph(current);
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
}
