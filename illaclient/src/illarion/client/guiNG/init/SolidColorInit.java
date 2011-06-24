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
package illarion.client.guiNG.init;

import illarion.client.guiNG.elements.SolidColor;
import illarion.client.guiNG.elements.Widget;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * This class takes care for loading up a solid color correctly.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class SolidColorInit implements WidgetInit {
    /**
     * The serialization UID this initialization script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The alpha share of the color that is supposed to be loaded to the solid
     * color.
     */
    private int alpha;

    /**
     * The blue share of the color that is supposed to be loaded to the solid
     * color.
     */
    private int blue;

    /**
     * The green share of the color that is supposed to be loaded to the solid
     * color.
     */
    private int green;

    /**
     * The red share of the color that is supposed to be loaded to the solid
     * color.
     */
    private int red;

    /**
     * The private constructor to avoid instances created uncontrolled.
     */
    private SolidColorInit() {
        // private constructor to avoid instances created uncontrolled.
    }

    /**
     * Get a new instance of this initialization script. This either creates a
     * new instance of this class or returns always the same, depending on what
     * is needed for this script.
     * 
     * @return the instance of this initialization script that is to be used
     *         from now on
     */
    public static SolidColorInit getInstance() {
        return new SolidColorInit();
    }

    /**
     * Prepare the widget for the active work.
     * 
     * @param widget the widget that is prepared
     */
    @Override
    @SuppressWarnings("nls")
    public void initWidget(final Widget widget) {
        if (!(widget instanceof SolidColor)) {
            throw new IllegalArgumentException(
                "Init Class requires a SolidColor widget");
        }
        final SolidColor colorWidget = (SolidColor) widget;
        final SpriteColor color = Graphics.getInstance().getSpriteColor();
        color.set(red, green, blue);
        color.setAlpha(alpha);
        colorWidget.setColor(color);
    }

    /**
     * Set the color that is supposed to be loaded into the solid color widget.
     * 
     * @param newRed red share of the color
     * @param newGreen green share of the color
     * @param newBlue blue share of the color
     * @param newAlpha alpha share of the color
     * @return the instance of the initialization script that is currently
     *         handled
     */
    public SolidColorInit setColor(final int newRed, final int newGreen,
        final int newBlue, final int newAlpha) {
        red = newRed;
        green = newGreen;
        blue = newBlue;
        alpha = newAlpha;

        return this;
    }

}
