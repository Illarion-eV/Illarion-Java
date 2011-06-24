/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.guiNG.elements;

import illarion.graphics.Drawer;
import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * This widget simply draws a static color on the entire area of the widget.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class SolidColor extends Widget {
    /**
     * The drawer that is used to draw the colored area.
     */
    private static final Drawer DRAWER = Graphics.getInstance().getDrawer();

    /**
     * The serialization ID of this color widget.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The color that is used to fill the area.
     */
    private transient SpriteColor color;

    /**
     * Draw the solid color.
     * 
     * @param delta the time since the render function was called last time
     */
    @Override
    public void draw(final int delta) {
        if (!isVisible()) {
            return;
        }
        if (color != null) {
            DRAWER.drawRectangle(getRelX(), getRelY(), getRelX() + getWidth(),
                getRelY() + getHeight(), color);
        }
        super.draw(delta);
    }

    /**
     * Set the color that is used when rendering this solid color.
     * 
     * @param newColor the color that is used to render this solid color
     */
    public void setColor(final SpriteColor newColor) {
        color = newColor;
    }
}
