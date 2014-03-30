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
package org.illarion.engine.graphic;

import org.illarion.engine.Disposable;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Font extends Disposable {
    /**
     * Get the line height of the font.
     *
     * @return the line height of the font
     */
    int getLineHeight();

    /**
     * Get the width of the specified text.
     *
     * @param text the text
     * @return the width of the line needed to place the text in
     */
    int getWidth(@Nonnull CharSequence text);

    /**
     * Get the advance value between two characters.
     *
     * @param current the current character
     * @param next the next character
     * @return the amount of pixels the cursor needs to be moved before drawing the next glyph
     */
    int getAdvance(char current, char next);
}
