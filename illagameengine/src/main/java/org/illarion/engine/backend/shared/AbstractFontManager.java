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
            @Nonnull final Object identifier,
            @Nonnull final String ttfRef,
            final float size,
            final int style,
            @Nonnull final String fntRef,
            @Nonnull final String imageRoot) throws IOException {
        final Font font = buildFont(ttfRef, size, style, fntRef, imageRoot);
        loadedFonts.put(identifier, font);
        return font;
    }

    /**
     * Build a font.
     *
     * @param ttfRef the reference to the ttf font file
     * @param size the requested size of the font
     * @param style the requested style of the font
     * @param fntRef the reference to the angelcode font file
     * @param imageRoot the root directory of the image file
     * @return the created font
     * @throws IOException in case loading the font fails
     */
    @Nonnull
    protected abstract Font buildFont(
            @Nonnull String ttfRef, float size, int style, @Nonnull String fntRef, @Nonnull String imageRoot)
            throws IOException;

    @Nullable
    @Override
    public final Font getFont(@Nonnull final Object identifier) {
        @Nullable final Font requestedFont = loadedFonts.get(identifier);
        if ((requestedFont == null) && (defaultFontIdentifier != null)) {
            return loadedFonts.get(defaultFontIdentifier);
        }
        return requestedFont;
    }

    @Override
    public final void setDefaultFont(@Nullable final Object identifier) {
        defaultFontIdentifier = identifier;
    }

    /**
     * Get the name of the image file fitting the specified font file.
     *
     * @param fntRef the reference to the font file
     * @return the name of the image
     */
    @Nonnull
    protected static String getImageName(@Nonnull final String fntRef) {
        return fntRef.substring(fntRef.lastIndexOf('/') + 1).replace(".fnt", "_0.png");
    }
}
