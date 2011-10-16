package org.illarion.nifty.renderer.render;

import org.newdawn.slick.Color;

import illarion.graphics.RenderableFont;
import illarion.graphics.common.FontLoader;
import de.lessvoid.nifty.spi.render.RenderFont;

/**
 * Nifty Font implementation for the Illarion Renderable Fonts.
 * 
 * @author Martin Karing
 */
public class RenderFontIllarion implements RenderFont {
    /**
     * The font that is used.
     */
    private RenderableFont font;

    /**
     * Initialize the font.
     * 
     * @param name font filename
     */
    public RenderFontIllarion(final String name) {
        font = FontLoader.getInstance().getFont(name);
    }

    /**
     * get font height.
     * 
     * @return height
     */
    public int getHeight() {
        return font.getHeight();
    }

    /**
     * get font width of the given string.
     * 
     * @param text text
     * @return width of the given text for the current font
     */
    public int getWidth(final String text) {
        return font.getWidth(text);
    }

    /**
     * Return the width of the given character including kerning information.
     * 
     * @param currentCharacter current character
     * @param nextCharacter next character
     * @param size font size
     * @return width of the character or null when no information for the
     *         character is available
     */
    public Integer getCharacterAdvance(final char currentCharacter,
        final char nextCharacter, final float size) {
        return font.getCharacterAdvance(currentCharacter, nextCharacter, size);
    }

    /**
     * Clean this font reference up.
     */
    public void dispose() {
        font = null;
    }
    
    public void drawText(final int x, final int y, final String line, final float size, final Color color) {
        font.renderString(null, x, y, color, size);
    }
}
