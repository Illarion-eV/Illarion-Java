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

import illarion.graphics.FontData;
import illarion.graphics.SpriteColor;
import illarion.graphics.generic.AbstractRenderableFont;
import illarion.graphics.jogl.render.AbstractTextureRender;

/**
 * This class defines all informations for a OpenGL image based font.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class RenderableFontJOGL extends AbstractRenderableFont {

    /**
     * The texture render that takes care for drawing the glyph textures.
     */
    private final AbstractTextureRender texRender;

    /**
     * Constructor for connecting with a font definitions file.
     * 
     * @param font the font that is rendered
     */
    public RenderableFontJOGL(final FontData font) {
        super(font);
        texRender = AbstractTextureRender.getInstance();
    }

    /**
     * Draw a glyph to the screen. Only the offset of the glyph is considered
     * due this rendering action. The influence of the kerning is not considered
     * by the function and must by included in the location of the pen before
     * rendering the glyph.
     * 
     * @param glyph the glyph that is supposed to be rendered
     * @param penX the x coordinate of the pen
     * @param penY the y coordinate of the pen
     * @param color the sprite color that is used to draw the texture
     */
    @Override
    public void renderGlyph(final FontData.GlyphData glyph, final int penX,
        final int penY, final SpriteColor color) {
        final TextureJOGL texture = (TextureJOGL) glyph.getTexture();
        if (texture != null) {
            texRender.drawTexture(penX + glyph.getX(), penY + glyph.getY(),
                0.f, texture.getImageWidth(), texture.getImageHeight(),
                texture, color, false, 0.f);
        }
    }
}
