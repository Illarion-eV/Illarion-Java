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
package illarion.graphics.jogl;

import java.awt.Font;

import illarion.graphics.FontData;
import illarion.graphics.RenderableFont;
import illarion.graphics.SpriteColor;
import illarion.graphics.jogl.font.FontJOGL;
import illarion.graphics.jogl.font.JavaFontJOGL;
import illarion.graphics.jogl.font.TextureFontJOGL;

/**
 * This class defines all informations for a OpenGL image based font.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class RenderableFontJOGL implements RenderableFont {

    /**
     * The texture render that takes care for drawing the glyph textures.
     */
    private final FontJOGL interalFont;

    /**
     * Constructor for connecting with a font definitions file.
     * 
     * @param font the font that is rendered
     */
    public RenderableFontJOGL(final FontData font) {
        interalFont = new TextureFontJOGL(font);
    }
    
    /**
     * Constructor for connecting with a font definitions file.
     * 
     * @param font the font that is rendered
     */
    public RenderableFontJOGL(final Font font) {
        interalFont = new JavaFontJOGL(font);
    }

    @Override
    public int getWidth(String text) {
        return interalFont.getWidth(text);
    }

    @Override
    public int getHeight() {
        return interalFont.getHeight();
    }

    @Override
    public Integer getCharacterAdvance(char currentCharacter,
        char nextCharacter, float size) {
        return interalFont.getCharacterAdvance(currentCharacter, nextCharacter, size);
    }

    @Override
    public void renderString(String text, int posX, int posY,
        SpriteColor color, float size) {
        interalFont.renderString(text, posX, posY, (SpriteColorJOGL) color, size);
    }
}
