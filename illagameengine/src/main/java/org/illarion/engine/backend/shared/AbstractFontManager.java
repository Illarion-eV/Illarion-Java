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
package org.illarion.engine.backend.shared;

import org.illarion.engine.assets.FontManager;
import org.illarion.engine.graphic.Font;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the font manager implementation that is shared by the different backends.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractFontManager implements FontManager {
    /**
     * The map that stores the fonts that are already load.
     */
    @Nonnull
    private final Map<Object, Font> loadedFonts;

    /**
     * The identifier of the font returned by default.
     */
    @Nullable
    private Object defaultFontIdentifier;

    /**
     * Create a new abstract font manager and setup the internal structures.
     */
    protected AbstractFontManager() {
        loadedFonts = new HashMap<>();
    }

    @Nonnull
    @Override
    public final Font createFont(
            @Nonnull Object identifier,
            @Nonnull String fntRef,
            @Nonnull String imageRoot) throws IOException {
        Font font = buildFont(fntRef, imageRoot, null);
        loadedFonts.put(identifier, font);
        return font;
    }

    @Nonnull
    @Override
    public final Font createFont(
            @Nonnull Object identifier,
            @Nonnull String fntRef,
            @Nonnull String imageRoot,
            @Nonnull Font outlineFont) throws IOException {
        Font font = buildFont(fntRef, imageRoot, outlineFont);
        loadedFonts.put(identifier, font);
        return font;
    }

    /**
     * Build a font.
     *
     * @param fntRef the reference to the angelcode font file
     * @param imageRoot the root directory of the image file
     * @param outlineFont the font that is rendered as outline
     * @return the created font
     * @throws IOException in case loading the font fails
     */
    @Nonnull
    protected abstract Font buildFont(@Nonnull String fntRef, @Nonnull String imageRoot, @Nullable Font outlineFont)
            throws IOException;

    @Nullable
    @Override
    public final Font getFont(@Nonnull Object identifier) {
        @Nullable Font requestedFont = loadedFonts.get(identifier);
        if ((requestedFont == null) && (defaultFontIdentifier != null)) {
            return loadedFonts.get(defaultFontIdentifier);
        }
        return requestedFont;
    }

    @Override
    public final void setDefaultFont(@Nullable Object identifier) {
        defaultFontIdentifier = identifier;
    }

    /**
     * Get the name of the image file fitting the specified font file.
     *
     * @param fntRef the reference to the font file
     * @return the name of the image
     */
    @Nonnull
    protected static String getImageName(@Nonnull String fntRef) {
        return fntRef.substring(fntRef.lastIndexOf('/') + 1).replace(".fnt", "_0.png");
    }
}
