/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.common;

import java.io.Serializable;

import javolution.text.TextBuilder;

import illarion.common.util.Rectangle;

import illarion.graphics.FontData;
import illarion.graphics.Texture;

/**
 * This font class stores all definitions needed for a font. The
 * {@link illarion.graphics.RenderableFont} implementations can use the data
 * stored in this font class to get all data needed to render a font properly.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class Font implements FontData, Serializable {
    /**
     * A glyph defines the appearance and the placement of a single character of
     * the font. This data is needed to properly render a font line.
     * 
     * @author Martin Karing
     * @since 1.22
     */
    public static final class Glyph implements FontData.GlyphData,
        Serializable {
        /**
         * The serialization UID of this font.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The amount of pixels the cursor advances right after the glyph was
         * drawn.
         */
        private final int advance;

        /**
         * The ID of this glyph.
         */
        private final int id;

        /**
         * The kerning data of the glyphs.
         */
        private final int[] kerningIndex;

        /**
         * The kerning data of the glyphs.
         */
        private final int[] kerningValues;

        /**
         * The texture name of the graphic for this glyph.
         */
        private final String name;

        /**
         * The texture showing that glyph.
         */
        private transient Texture texture;

        /**
         * The x coordinate of the origin offset of the glyph.
         */
        private final int x;

        /**
         * The y coordinate of the origin offset of the glyph.
         */
        private final int y;

        /**
         * Create a new Glyph will all data required by the other classes.
         * 
         * @param glyphId the ID of this glyph
         * @param newOffsetX the x coordinate of the offset of this glyph
         * @param newOffsetY the y coordinate of the offset of this glyph
         * @param newAdvance the amount of pixels the cursor advances right
         *            after the glyph was written
         * @param newKerningValues the actual kerning values
         * @param newTextureName the name of the texture of this glyph
         */
        public Glyph(final int glyphId, final int newOffsetX,
            final int newOffsetY, final int newAdvance,
            final int[] newKerningValues, final String newTextureName) {
            id = glyphId;
            advance = newAdvance;
            x = newOffsetX;
            y = newOffsetY;
            name = newTextureName;

            final int[] index = new int[newKerningValues.length];
            final int[] values = new int[newKerningValues.length];
            int entries = 0;

            for (int i = 0; i < newKerningValues.length; i++) {
                if (newKerningValues[i] != 0) {
                    index[entries] = i;
                    values[entries] = newKerningValues[i];
                    entries++;
                }
            }

            if (entries == 0) {
                kerningIndex = null;
                kerningValues = null;
            } else {
                kerningIndex = new int[entries];
                kerningValues = new int[entries];

                System.arraycopy(index, 0, kerningIndex, 0, entries);
                System.arraycopy(values, 0, kerningValues, 0, entries);
            }
        }

        /**
         * Returns the pen advance. This is how many points the pen has to move
         * forward after this glyph was placed.
         * 
         * @return the amount of pixel the pen has to move right after the glyph
         *         was drawn
         */
        @Override
        public int getAdvance() {
            return advance;
        }

        /**
         * Get the height of this glyph.
         * 
         * @return the height of the glyph
         */
        @Override
        public int getHeight() {
            if (texture == null) {
                return 0;
            }
            return texture.getImageHeight();
        }

        /**
         * Get the ID of this glyph.
         * 
         * @return the ID of this glyph
         */
        @Override
        public int getId() {
            return id;
        }

        /**
         * If we have just laid out a glyph, g, and we want to lay out this
         * glyph next to it, this function will return the required kerning to
         * do so.
         * 
         * @param g the glyph that was layed out before
         * @return the kerning that is needed for this glyph
         */
        @Override
        public int getKerningAfter(final FontData.GlyphData g) {
            if (g == null) {
                return 0;
            }

            if (kerningIndex == null) {
                return 0;
            }

            for (int i = 0; i < kerningIndex.length; i++) {
                if (kerningIndex[i] == g.getId()) {
                    return kerningValues[i];
                }
            }
            return 0;
        }

        /**
         * Get the texture of this glyph.
         * 
         * @return the texture of this glyph
         */
        @Override
        public Texture getTexture() {
            return texture;
        }

        /**
         * Get the name of the texture that fits this Glyph.
         * 
         * @return the name of the texture
         */
        @Override
        public String getTextureName() {
            return name;
        }

        /**
         * Get the width of this glyph.
         * 
         * @return the width of the glyph
         */
        @Override
        public int getWidth() {
            if (texture == null) {
                return 0;
            }
            return texture.getImageWidth();
        }

        /**
         * Get the x coordinate of the origin of the glyph. Some glyphes have
         * offsets not equal 0 and those have to played relative to the line and
         * the pen location.
         * 
         * @return the x coordinate of the glyph offset
         */
        @Override
        public int getX() {
            return x;
        }

        /**
         * Get the y coordinate of the origin of the glyph. Some glyphes have
         * offsets not equal 0 and those have to played relative to the line and
         * the pen location.
         * 
         * @return the y coordinate of the glyph offset
         */
        @Override
        public int getY() {
            return y;
        }

        /**
         * Load the texture from the texture loader in order to prepare the
         * rendering on the font properly.
         * 
         * @param path the path to the font textures
         */
        protected void prepareTexture(final String path, final String fontName) {
            if (name == null) {
                return;
            }

            final TextBuilder builder = TextBuilder.newInstance();
            builder.append(fontName);
            builder.append('/');
            builder.append(name);
            texture =
                TextureLoader.getInstance().getTexture(path,
                    builder.toString(), false, false);
            TextBuilder.recycle(builder);
        }
    }

    /**
     * The new line character for the detection where line wrap.
     */
    public static final char NEWLINE = 0x0A;

    /**
     * The serialization UID of this font.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The ascent of the font.
     */
    private final int ascent;

    /**
     * In case this is a bold font the value is set <code>true</code>.
     */
    private final boolean bold;

    /**
     * The descent of the font.
     */
    private final int descent;

    /**
     * The glyphes known to this font.
     */
    private final Glyph[] glyphes;

    /**
     * In case this is a italic font the value is set <code>true</code>.
     */
    private final boolean italic;

    /**
     * The leading of the font
     */
    private final int leading;

    /**
     * The name of the font.
     */
    private final String name;

    /**
     * The size of the font.
     */
    private final int size;

    /**
     * Create a new instance of a font. This is usually only used by the font
     * builder.
     * 
     * @param fontName the name of the font, this is important to tell the
     *            glyphes the texture they have to load
     * @param boldFont <code>true</code> in case this font is a bold one
     * @param italicFont <code>true</code> in case this font is a italic one
     * @param glyph the array of glyphes that are loaded into this font
     * @param fontSize the size of this font
     * @param fontAscent the ascent of this font
     * @param fontDescent the descent of this font
     * @param fontLeading the leading of this font
     * @param map the character mapping of this font
     */
    @SuppressWarnings("nls")
    public Font(final String fontName, final boolean boldFont,
        final boolean italicFont, final Glyph[] glyph, final int fontSize,
        final int fontAscent, final int fontDescent, final int fontLeading,
        final int[] map) {

        if ((fontName == null) || fontName.isEmpty()) {
            throw new IllegalArgumentException(
                "Font name may not be null or empty.");
        }

        if (glyph == null) {
            throw new IllegalArgumentException(
                "Glyph list (glyph) may not be null.");
        }

        if (map == null) {
            throw new IllegalArgumentException(
                "Glyph mappings (map) may not be null.");
        }

        name = fontName;
        bold = boldFont;
        italic = italicFont;
        size = fontSize;
        ascent = fontAscent;
        descent = fontDescent;
        leading = fontLeading;

        glyphes = new Glyph[map.length];
        for (int i = 0; i < map.length; i++) {
            int mapping = map[i];
            if (mapping >= glyph.length) {
                mapping = 0;
            }
            glyphes[i] = glyph[mapping];
        }
    }

    /**
     * Gets the ascent.
     * 
     * @return the ascent value
     */
    @Override
    public int getAscent() {
        return ascent;
    }

    /**
     * Gets the descent.
     * 
     * @return the descent value
     */
    @Override
    public int getDescent() {
        return descent;
    }

    /**
     * Get the name of the font.
     * 
     * @return the name of the font
     */
    @Override
    public String getFontName() {
        return name;
    }

    /**
     * Gets a glyph.
     * 
     * @param i the character value of this glyph
     * @return the glyph fitting to this character
     */
    @Override
    public FontData.GlyphData getGlyph(final int i) {
        return getGlyphImpl(i);
    }

    /**
     * Get the amount of glyphes stored in this list.
     * 
     * @return the amount of glyphes
     */
    @Override
    public int getGlyphCount() {
        return glyphes.length;
    }

    /**
     * This function simply puts the references to the glyph instances of this
     * font into a array. Make sure the array is large enough to store all the
     * glyphes.
     * 
     * @param text the text that shall be rendered with the glyphes
     * @param start the index of the first character of the text that shall be
     *            rendered, the glyph for this character is placed at index 0 of
     *            the glyph array
     * @param end the index of the first character of the text that is not
     *            anymore rendered
     * @param dest the array of glyphes the references are stored in
     * @param x the array of x coordinates where to render the glyphes, the y
     *            coordinates are as a matter of fact always the same because
     *            all letters are drawn in one line
     */
    @SuppressWarnings("nls")
    @Override
    public void getGlyphes(final String text, final int start, final int end,
        final GlyphData[] dest, final int[] x) {
        if (dest == null) {
            throw new IllegalArgumentException(
                "Destination for font glyphes may not be null.");
        }
        if (x == null) {
            throw new IllegalArgumentException(
                "Destination for glyph locations may not be null.");
        }
        if (dest.length < (end - start)) {
            throw new IllegalArgumentException(
                "Destination array for font glyphes is too small.");
        }
        if (x.length < (end - start)) {
            throw new IllegalArgumentException(
                "Destination array for glyph locations is too small.");
        }
        if ((text == null) || text.isEmpty()) {
            throw new IllegalArgumentException("Text may not be null or empty");
        }

        int penX = 0;
        Glyph next = null;
        Glyph last = null;
        char currentChar;
        boolean foundNonSpace = false;
        for (int i = start; i < end; i++) {
            currentChar = text.charAt(i);
            next = getGlyphImpl(currentChar);
            dest[i - start] = next;
            x[i - start] = penX;

            if ((currentChar != ' ') || foundNonSpace) {
                penX += next.getAdvance() - next.getKerningAfter(last);
                foundNonSpace = true;
            }
            last = next;
        }
    }

    /**
     * Gets the leading.
     * 
     * @return the leading value
     */
    @Override
    public int getLeading() {
        return leading;
    }

    /**
     * Gets the size.
     * 
     * @return the size of the font
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Get the bounding box of a string that is rendered with this font.
     * 
     * @param text the string that is used to calculate the bounding box
     * @param start the start index within the string that is used to calculate
     * @param end the end index in the string that is used to calculate
     * @return a rectangle describing the bounding box of this font
     */
    @SuppressWarnings("nls")
    public java.awt.Rectangle getStringBounds(final String text,
        final int start, final int end) {

        if ((text == null) || text.isEmpty()) {
            throw new IllegalArgumentException("Text may not be null or empty");
        }

        final Rectangle resultRect = Rectangle.getInstance();
        resultRect.set(0, 0, 0, 0);

        Glyph next = null;
        Glyph last = null;
        int penX = 0;
        int kerning = 0;
        char currentChar;
        final Rectangle tempRect = Rectangle.getInstance();
        for (int i = start; i < end; i++) {
            currentChar = text.charAt(i);
            if ((i == start) && (currentChar == ' ')
                && (currentChar == NEWLINE)) {
                continue;
            }
            next = getGlyphImpl(currentChar);
            kerning = next.getKerningAfter(last);
            tempRect
                .set((next.getX() + penX) - kerning, next.getY(),
                    Math.max(next.getWidth(), next.getAdvance()),
                    next.getHeight());
            resultRect.add(tempRect);
            last = next;
            penX += next.getAdvance() - kerning;
        }
        tempRect.recycle();

        final java.awt.Rectangle returnValue = resultRect.toNative();
        resultRect.recycle();

        return returnValue;
    }

    /**
     * Check how many characters of a string fit into a given length in pixel.
     * Its possible that the string gets longer then the specified length in
     * case there is no space found.
     * 
     * @param text the text that shall be measured
     * @param start the index of the first character of the relevant string
     * @param end the index of the first character that is not relevant anymore
     *            for the measurement
     * @param length the length in pixel that is the limit
     * @return the index of the character that is the last one fitting into the
     *         given length
     */
    @SuppressWarnings("nls")
    public int getStringWrap(final String text, final int start,
        final int end, final int length) {

        if ((text == null) || text.isEmpty()) {
            throw new IllegalArgumentException("Text may not be null or empty");
        }

        final Rectangle currRect = Rectangle.getInstance();
        final Rectangle tempRect = Rectangle.getInstance();
        currRect.set(0, 0, 0, 0);

        int lastFittingIndex = -1;

        Glyph next = null;
        Glyph last = null;
        int penX = 0;
        int kerning = 0;
        boolean foundSpace = false;
        char currentChar;
        boolean foundNonSpace = false;
        for (int i = start; i < end; i++) {
            currentChar = text.charAt(i);
            if (!foundNonSpace
                && ((currentChar == ' ') || (currentChar == NEWLINE))) {
                if (currentChar == NEWLINE) {
                    foundNonSpace = true;
                }
                continue;
            }
            foundNonSpace = true;

            if (currentChar == NEWLINE) {
                lastFittingIndex = i - 1;
                break;
            }
            next = getGlyphImpl(currentChar);
            kerning = next.getKerningAfter(last);
            tempRect
                .set((next.getX() + penX) - kerning, next.getY(),
                    Math.max(next.getWidth(), next.getAdvance()),
                    next.getHeight());
            currRect.add(tempRect);
            last = next;
            penX += next.getAdvance() - kerning;
            if ((i + 1) == end) {
                lastFittingIndex = i;
                break;
            } else if ((text.charAt(i + 1) == ' ')
                && (!foundSpace || (currRect.getWidth() <= length))) {
                lastFittingIndex = i;
                foundSpace = true;
            } else if (foundSpace && (currRect.getWidth() > length)) {
                break;
            } else if (text.charAt(i + 1) == NEWLINE) {
                lastFittingIndex = i;
                break;
            }
        }
        currRect.recycle();
        tempRect.recycle();
        return lastFittingIndex;
    }

    /**
     * Get if this is a bold font.
     * 
     * @return <code>true</code> in case this is a bold font
     */
    @Override
    public boolean isBold() {
        return bold;
    }

    /**
     * Get if this is a italic font.
     * 
     * @return <code>true</code> if this is a italic font
     */
    @Override
    public boolean isItalic() {
        return italic;
    }

    /**
     * Get if this is a plain font. So its not bold or italic.
     * 
     * @return <code>true</code> if this font is not bold and not italic
     */
    @Override
    public boolean isPlain() {
        return !(bold || italic);
    }

    /**
     * Prepare the textures of glyphes to ensure that everything loads properly.
     * 
     * @param fontRoot the root path to the fonts
     */
    @SuppressWarnings("nls")
    public void prepareTextures(final String fontRoot) {
        if (fontRoot == null) {
            throw new IllegalArgumentException(
                "Root directory of the fonts my not be null.");
        }
        String path = fontRoot;
        if (!fontRoot.endsWith("/")) {
            path += "/";
        }

        for (final Glyph glyphe : glyphes) {
            glyphe.prepareTexture(path, name);
        }
    }

    /**
     * Internal function to get a glyph. This is used to get glyphs without any
     * casting needed.
     * 
     * @param i the character value of this glyph
     * @return the glyph fitting to this character
     */
    private Glyph getGlyphImpl(final int i) {
        if ((i < 0) || (i >= glyphes.length)) {
            return glyphes[0];
        }
        return glyphes[i];
    }
}
