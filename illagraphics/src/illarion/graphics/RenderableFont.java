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
 * This interface specifies the font implementations that are used to display
 * the text in the client.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface RenderableFont {
    /**
     * Get width in pixel of given text.
     * 
     * @param text the text to measure
     * @return the pixel width of the given text
     */
    int getWidth(String text);

    /**
     * The height of the font in pixel.
     * 
     * @return font height in pixel
     */
    int getHeight();

    /**
     * Return the advance of the given character including kerning information.
     * 
     * @param currentCharacter current character
     * @param nextCharacter next character
     * @param size font size
     * @return width of the character or null when no information for the
     *         character is available
     */
    Integer getCharacterAdvance(char currentCharacter, char nextCharacter,
        float size);

    /**
     * Render a character sequence using this font.
     * 
     * @param text the text to render
     * @param posX the x origin of the text
     * @param posY the y origin of the text
     * @param color the color of the text
     * @param size the size of the text
     */
    void renderString(String text, int posX, int posY, SpriteColor color, float size);
}
