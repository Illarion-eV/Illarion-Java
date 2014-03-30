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

import org.illarion.engine.graphic.Font;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.SlickException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the implementation of the font interface for Slick2D.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickFont implements Font {
    /**
     * The font that is used internal to display the font.
     */
    @Nonnull
    private final AngelCodeFont internalFont;

    /**
     * The constructor of this font.
     *
     * @param fntFile the angelcode font file
     * @param imgFile the image file
     * @throws SlickEngineException in case loading the font fails
     */
    SlickFont(@Nonnull final String fntFile, @Nonnull final SlickTexture imgFile) throws SlickEngineException {
        try {
            internalFont = new AngelCodeFont(fntFile, imgFile.getBackingImage());
        } catch (@Nonnull final SlickException | RuntimeException e) {
            throw new SlickEngineException(e);
        }
    }

    @Override
    public void dispose() {
        // nothing to do
    }

    /**
     * Get the internal font instance.
     *
     * @return the internal font
     */
    @Nonnull
    public AngelCodeFont getInternalFont() {
        return internalFont;
    }

    @Override
    public int getLineHeight() {
        return internalFont.getLineHeight();
    }

    @Override
    public int getWidth(@Nonnull final CharSequence text) {
        return internalFont.getWidth(text);
    }

    @Override
    public int getAdvance(final char current, final char next) {
        @Nullable final AngelCodeFont.Glyph currentGlypth = internalFont.getGlyph(current);
        @Nullable final AngelCodeFont.Glyph nextGlypth = internalFont.getGlyph(current);
        if (currentGlypth == null) {
            return 0;
        }
        final int advance;
        if (nextGlypth == null) {
            advance = 0;
        } else {
            advance = nextGlypth.xadvance;
        }
        return currentGlypth.getKerning(next) + advance;
    }
}
