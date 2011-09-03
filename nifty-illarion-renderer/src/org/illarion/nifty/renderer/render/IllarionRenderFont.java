/*
 * This file is part of the Illarion Nifty-GUI binding.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Nifty-GUI binding is free software: you can redistribute i
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * The Illarion Nifty-GUI binding is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Nifty-GUI binding. If not, see <http://www.gnu.org/licenses/>.
 */
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

    public static IllarionRenderFont getFont(final String fontName) {
        final RenderableFont font = FontLoader.getInstance().getFont(fontName);
        return new IllarionRenderFont(font);
    }

    private final SpriteColor internalColor;

    private final RenderableFont internalFont;

    private IllarionRenderFont(final RenderableFont font) {
        internalFont = font;
        internalColor = Graphics.getInstance().getSpriteColor();
    }

    /*
     * (non-Javadoc)
     * @see de.lessvoid.nifty.spi.render.RenderFont#dispose()
     */
    @Override
    public void dispose() {
        // nothing to do
    }

    /*
     * (non-Javadoc)
     * @see de.lessvoid.nifty.spi.render.RenderFont#getCharacterAdvance(char,
     * char, float)
     */
    @Override
    public Integer getCharacterAdvance(final char currentCharacter,
        final char nextCharacter, final float size) {

        return internalFont.getCharacterAdvance(currentCharacter,
            nextCharacter, size);
    }

    /*
     * (non-Javadoc)
     * @see de.lessvoid.nifty.spi.render.RenderFont#getHeight()
     */
    @Override
    public int getHeight() {
        return internalFont.getHeight();
    }

    /*
     * (non-Javadoc)
     * @see de.lessvoid.nifty.spi.render.RenderFont#getWidth(java.lang.String)
     */
    @Override
    public int getWidth(final String text) {
        return internalFont.getWidth(text);
    }

    void renderString(final String text, final int posX, final int posY,
        final Color color, final float size) {
        internalColor.set(color.getRed(), color.getGreen(), color.getBlue());
        internalColor.setAlpha(color.getAlpha());
        internalFont.renderString(text, posX, posY, internalColor, size);
    }

}
