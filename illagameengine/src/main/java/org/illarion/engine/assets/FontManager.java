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
package org.illarion.engine.assets;

import org.illarion.engine.graphic.Font;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * This class manages loading and storing the fonts that are rendered in the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface FontManager {
    /**
     * This is the function to create a new font.
     *
     * @param identifier the identifier of the font, this is used to retrieve the font later
     * @param fntRef the Angelcode font definition
     * @param imageRoot the root directory where the image for the angelcode font is located
     * @return the newly created font
     * @throws IOException in case loading the font fails
     * @throws IllegalArgumentException in case the identifier is already used for another font
     */
    @Nonnull
    Font createFont(
            @Nonnull Object identifier,
            @Nonnull String fntRef,
            @Nonnull String imageRoot) throws IOException;

    /**
     * This is the function to create a new font.
     *
     * @param identifier  the identifier of the font, this is used to retrieve the font later
     * @param fntRef      the Angelcode font definition
     * @param imageRoot   the root directory where the image for the angelcode font is located
     * @param outlineFont a second font that is rendered on the same spot as the original one
     * @return the newly created font
     * @throws IOException              in case loading the font fails
     * @throws IllegalArgumentException in case the identifier is already used for another font
     */
    @Nonnull
    Font createFont(
            @Nonnull Object identifier,
            @Nonnull String fntRef,
            @Nonnull String imageRoot,
            @Nonnull Font outlineFont) throws IOException;

    /**
     * Fetch a font that was created before.
     *
     * @param identifier the identifier of the font
     * @return the font assigned to the identifier or {@code null} in case there is no font assigned to this identifier
     */
    @Nullable
    Font getFont(@Nonnull Object identifier);

    /**
     * Set the font that is supposed to be used by default in case a font is requested that was not defined. Once the
     * default font is set the {@link #getFont(Object)} method won't return {@code null} anymore. Instead it returns
     * the default font.
     *
     * @param identifier the font to use by default or {@code null} to delete the default font assignment
     * @throws IllegalArgumentException in case the identifier is not assigned to any font
     */
    void setDefaultFont(@Nullable Object identifier);
}
