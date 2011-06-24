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
package illarion.graphics.generic;

import illarion.graphics.FontData;
import illarion.graphics.RenderableFont;
import illarion.graphics.SpriteColor;

/**
 * Generic renderable font implementation that implements the parts of the
 * renderable font that is shared by all library specific implementations.
 * 
 * @author Martin Karing
 * @since 2.00
 * @version 2.00
 */
public abstract class AbstractRenderableFont implements RenderableFont {
    /**
     * Informations about the font.
     */
    private final FontData fontData;

    /**
     * Constructor for connecting with a font definitions file.
     * 
     * @param font the font data that is supplied to this renderable font
     */
    public AbstractRenderableFont(final FontData font) {
        fontData = font;
    }

    /**
     * Get the underlying FontData object that provides the data for this font.
     * 
     * @return the font data object
     */
    @Override
    public final FontData getSourceFont() {
        return fontData;
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
    public abstract void renderGlyph(final FontData.GlyphData glyph,
        final int penX, final int penY, final SpriteColor color);
}
