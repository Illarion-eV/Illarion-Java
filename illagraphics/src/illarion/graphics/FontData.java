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
package illarion.graphics;

/**
 * This interface is used to font implementations in order to access data of
 * fonts.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface FontData {
    /**
     * A glyph defines the appearance and the placement of a single character of
     * the font. This data is needed to properly render a font line.
     * 
     * @author Martin Karing
     * @since 1.22
     */
    public interface GlyphData {
        /**
         * Returns the pen advance. This is how many points the pen has to move
         * forward after this glyph was placed.
         * 
         * @return the amount of pixel the pen has to move right after the glyph
         *         was drawn
         */
        int getAdvance();

        /**
         * Get the height of the glyph.
         * 
         * @return the height of the glyph
         */
        int getHeight();

        /**
         * Get the unique ID of the glyph.
         * 
         * @return the ID of this glyph
         */
        int getId();

        /**
         * If we have just laid out a glyph, g, and we want to lay out this
         * glyph next to it, this function will return the required kerning to
         * do so.
         * 
         * @param g the glyph that was layed out before
         * @return the kerning that is needed for this glyph
         */
        int getKerningAfter(GlyphData g);

        /**
         * Get the texture of this glyph.
         * 
         * @return the texture of the glyph
         */
        Texture getTexture();

        /**
         * Get the name of the texture that fits this Glyph.
         * 
         * @return the name of the texture
         */
        String getTextureName();

        /**
         * Get the width of the glyph.
         * 
         * @return the width of the glyph
         */
        int getWidth();

        /**
         * Get the x coordinate of the origin of the glyph. Some glyphes have
         * offsets not equal 0 and those have to played relative to the line and
         * the pen location.
         * 
         * @return the x coordinate of the glyph offset
         */
        int getX();

        /**
         * Get the y coordinate of the origin of the glyph. Some glyphes have
         * offsets not equal 0 and those have to played relative to the line and
         * the pen location.
         * 
         * @return the y coordinate of the glyph offset
         */
        int getY();
    }

    /**
     * Gets the ascent.
     * 
     * @return the ascent value
     */
    int getAscent();

    /**
     * Gets the descent.
     * 
     * @return the descent value
     */
    int getDescent();

    /**
     * Get the name of the font.
     * 
     * @return the name of the font
     */
    String getFontName();

    /**
     * Gets a glyph.
     * 
     * @param i the character value of this glyph
     * @return the glyph fitting to this character
     */
    GlyphData getGlyph(int i);

    /**
     * Get the amount of glyphes stored in this list.
     * 
     * @return the amount of glyphes
     */
    int getGlyphCount();

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
    void getGlyphes(String text, int start, int end, GlyphData[] dest, int[] x);

    /**
     * Gets the leading.
     * 
     * @return the leading value
     */
    int getLeading();

    /**
     * Gets the size.
     * 
     * @return the size of the font
     */
    int getSize();

    /**
     * Get if this is a bold font.
     * 
     * @return <code>true</code> in case this is a bold font
     */
    boolean isBold();

    /**
     * Get if this is a italic font.
     * 
     * @return <code>true</code> if this is a italic font
     */
    boolean isItalic();

    /**
     * Get if this is a plain font. So its not bold or italic.
     * 
     * @return <code>true</code> if this font is not bold and not italic
     */
    boolean isPlain();
}
