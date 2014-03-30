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
package org.illarion.engine.nifty;

import de.lessvoid.nifty.spi.render.RenderFont;
import org.illarion.engine.graphic.Font;

import javax.annotation.Nonnull;

/**
 * This is the render font implementation for Nifty that uses the font interface to prepare the font.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class IgeRenderFont implements RenderFont {
    /**
     * The font that is backing this render font.
     */
    @Nonnull
    private final Font font;

    /**
     * Create a new render font that can be used by the Nifty-GUI.
     *
     * @param font the backing font
     */
    IgeRenderFont(@Nonnull final Font font) {
        this.font = font;
    }

    /**
     * Get the backing font.
     *
     * @return the backing font
     */
    @Nonnull
    public Font getFont() {
        return font;
    }

    @Override
    public int getWidth(@Nonnull final String text) {
        return font.getWidth(text);
    }

    @Override
    public int getWidth(@Nonnull final String text, final float size) {
        return (int) Math.ceil(font.getWidth(text) * size);
    }

    @Override
    public int getHeight() {
        return font.getLineHeight();
    }

    @Override
    public int getCharacterAdvance(final char currentCharacter, final char nextCharacter, final float size) {
        return (int) Math.ceil(font.getAdvance(currentCharacter, nextCharacter) * size);
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
