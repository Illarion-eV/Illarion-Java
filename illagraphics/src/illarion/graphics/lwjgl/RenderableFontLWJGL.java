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
package illarion.graphics.lwjgl;

import java.awt.font.GlyphVector;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.GlyphPage;
import org.newdawn.slick.font.effects.ColorEffect;

import illarion.graphics.FontData;
import illarion.graphics.RenderableFont;
import illarion.graphics.SpriteColor;
import illarion.graphics.lwjgl.font.FontLWJGL;
import illarion.graphics.lwjgl.font.TextureFontLWJGL;

/**
 * This class defines all informations for a OpenGL image based font.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class RenderableFontLWJGL implements RenderableFont {
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
    public RenderableFontLWJGL(final java.awt.Font font) {
        internalFont = new UnicodeFont(font);
        internalColor = new Color(0);
        javaFont = font;
        DriverSettingsLWJGL.getInstance().reset();
        
        internalFont.addAsciiGlyphs();
        internalFont.getEffects().add(new ColorEffect());
        try {
            internalFont.loadGlyphs();
        } catch (SlickException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public int getWidth(String text) {
        return internalFont.getWidth(text);
    }

    @Override
    public int getHeight() {
        return internalFont.getLineHeight();
    }

    @Override
    public Integer getCharacterAdvance(char currentCharacter,
        char nextCharacter, float size) {
        GlyphVector vector =
            javaFont.createGlyphVector(GlyphPage.renderContext,
                new char[] { currentCharacter, nextCharacter });
        return (int) ((vector.getGlyphPosition(1).getX() - vector
            .getGlyphPosition(0).getX()) * size);
    }
    
    private final Color internalColor;

    @Override
    public void renderString(String text, int posX, int posY,
        SpriteColor color, float size) {
        internalColor.r = color.getRedf();
        internalColor.g = color.getGreenf();
        internalColor.b = color.getBluef();
        internalColor.a = color.getAlphaf();
        
        internalFont.drawString(posX, posY, text, internalColor);
        DriverSettingsLWJGL.getInstance().reset();
    }
}
