package org.illarion.nifty.renderer.render;

import illarion.graphics.Graphics;
import illarion.graphics.RenderableFont;
import illarion.graphics.SpriteColor;
import illarion.graphics.common.FontLoader;
import de.lessvoid.nifty.spi.render.RenderFont;
import de.lessvoid.nifty.tools.Color;

/**
 * This render font implements the required functions for rendering texts with
 * Nifty.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
final class IllarionRenderFont implements RenderFont {
    
    private final RenderableFont internalFont;
    private final SpriteColor internalColor;
    
    public static IllarionRenderFont getFont(final String fontName) {
        RenderableFont font = FontLoader.getInstance().getFont(fontName);
        return new IllarionRenderFont(font);
    }
    
    private IllarionRenderFont(final RenderableFont font) {
        internalFont = font;
        internalColor = Graphics.getInstance().getSpriteColor();
    }

    /* (non-Javadoc)
     * @see de.lessvoid.nifty.spi.render.RenderFont#getWidth(java.lang.String)
     */
    @Override
    public int getWidth(String text) {
        return internalFont.getWidth(text);
    }

    /* (non-Javadoc)
     * @see de.lessvoid.nifty.spi.render.RenderFont#getHeight()
     */
    @Override
    public int getHeight() {
        return internalFont.getHeight();
    }

    /* (non-Javadoc)
     * @see de.lessvoid.nifty.spi.render.RenderFont#getCharacterAdvance(char, char, float)
     */
    @Override
    public Integer getCharacterAdvance(char currentCharacter,
        char nextCharacter, float size) {
        
        return internalFont.getCharacterAdvance(currentCharacter, nextCharacter, size);
    }

    /* (non-Javadoc)
     * @see de.lessvoid.nifty.spi.render.RenderFont#dispose()
     */
    @Override
    public void dispose() {
        // nothing to do
    }
    
    void renderString(String text, int posX, int posY, Color color, float size) {
        internalColor.set(color.getRed(), color.getGreen(), color.getBlue());
        internalColor.setAlpha(color.getAlpha());
        internalFont.renderString(text, posX, posY, internalColor, size);
    }

}
