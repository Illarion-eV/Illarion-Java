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

import java.awt.font.GlyphVector;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.GlyphPage;
import org.newdawn.slick.font.effects.ColorEffect;

/**
 * This class defines all informations for a OpenGL image based font.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class RenderableFont {
    /**
     * The font that is used internal.
     */
    private final UnicodeFont internalFont;
    
    private final java.awt.Font javaFont;

    /**
     * Constructor for connecting with a font definitions file.
     * 
     * @param font the font that is rendered
     */
    @SuppressWarnings("unchecked")
    public RenderableFont(final java.awt.Font font) {
        internalFont = new UnicodeFont(font);
        javaFont = font;
        
        internalFont.addAsciiGlyphs();
        internalFont.addGlyphs("•");
        internalFont.getEffects().add(new ColorEffect());
        try {
            internalFont.loadGlyphs();
        } catch (SlickException e) {
            // failed loading glyphes
        }
    }
    
    /**
     * Get width in pixel of given text.
     * 
     * @param text the text to measure
     * @return the pixel width of the given text
     */
    public int getWidth(final String text) {
        return internalFont.getWidth(text);
    }

    /**
     * The height of the font in pixel.
     * 
     * @return font height in pixel
     */
    public int getHeight() {
        return internalFont.getLineHeight();
    }

    /**
     * The height of the font in pixel in case the set text is displayed.
     * 
     * @param text the text to display
     * @return font height in pixel
     */
    public int getHeight(final String text) {
        return internalFont.getHeight(text);
    }
    
    /**
     * Return the advance of the given character including kerning information.
     * 
     * @param currentCharacter current character
     * @param nextCharacter next character
     * @param size font size
     * @return width of the character or null when no information for the
     *         character is available
     */
    public Integer getCharacterAdvance(char currentCharacter,
        char nextCharacter, float size) {
        GlyphVector vector =
            javaFont.createGlyphVector(GlyphPage.renderContext,
                new char[] { currentCharacter, nextCharacter });
        return (int) ((vector.getGlyphPosition(1).getX() - vector
            .getGlyphPosition(0).getX()) * size);
    }

    /**
     * Render a character sequence using this font.
     * 
     * @param text the text to render
     * @param posX the x origin of the text
     * @param posY the y origin of the text
     * @param color the color of the text
     * @param size the size of the text
     */
    public void renderString(String text, int posX, int posY,
        Color color, float size) {
        
        internalFont.drawString(posX, posY, text, color);
    }
}
