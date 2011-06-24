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

import illarion.graphics.RenderableFont;
import illarion.graphics.SpriteColor;
import illarion.graphics.generic.AbstractTextLine;

/**
 * This class represents a single graphical text line that can be rendered using
 * JOGL. It has no align but it is possible that it shows a text cursor and
 * marked text.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class TextLineJOGL extends AbstractTextLine {
    /**
     * Set the color that is used to render the line. Note that this does not
     * copy the instance of the color. It just stores the reference. So if the
     * color of the instance is changed later this color will change as well.
     * 
     * @param newColor the color that shall be used for the render actions from
     *            now on
     */
    @SuppressWarnings("nls")
    @Override
    public void setColor(final SpriteColor newColor) {
        if (newColor != null) {
            if (newColor instanceof SpriteColorJOGL) {
                super.setColor(newColor);
            } else {
                throw new IllegalArgumentException(
                    "Sprite color uses a invalid implementation.");
            }
        }
    }

    /**
     * Set the font that is used to render this text line.
     * 
     * @param newFont the new font that is used
     */
    @Override
    @SuppressWarnings("nls")
    public void setFont(final RenderableFont newFont) {
        if (newFont instanceof RenderableFontJOGL) {
            super.setFont(newFont);
        } else {
            throw new IllegalArgumentException(
                "Illegal implementation of RenderableFont");
        }
    }
}
