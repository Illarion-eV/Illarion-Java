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
package org.illarion.engine.backend.slick;

import org.illarion.engine.graphic.Font;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.SlickException;

import javax.annotation.Nonnull;

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
    SlickFont(@Nonnull final String fntFile, @Nonnull final String imgFile) throws SlickEngineException {
        try {
            internalFont = new AngelCodeFont(fntFile, imgFile);
        } catch (@Nonnull final SlickException e) {
            throw new SlickEngineException(e);
        }
    }

    @Override
    public void dispose() {
        // nothing to do
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
        return internalFont.getGlyph(current).getKerning(next);
    }
}
