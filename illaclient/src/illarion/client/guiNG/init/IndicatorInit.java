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

import illarion.client.guiNG.GUI;
import illarion.client.guiNG.IndicatorMask;
import illarion.client.guiNG.elements.Widget;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * This initialization script takes care for preparing a indicator in the client
 * to be used. It stores what type of indicator is used and knows everything
 * else as resulting values.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class IndicatorInit implements WidgetInit {
    /**
     * The type constant for a food indicator.
     */
    public static final int TYPE_FOOD = 1;

    /**
     * The type constant for a health indicator.
     */
    public static final int TYPE_HEALTH = 0;

    /**
     * The type constant for a mana indicator.
     */
    public static final int TYPE_MANA = 2;

    /**
     * The serialization ID of the initialization script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The indicator type.
     */
    private int type = TYPE_HEALTH;

    /**
     * The private constructor to avoid instances created uncontrolled.
     */
    private IndicatorInit() {
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
    public static IndicatorInit getInstance() {
        return new IndicatorInit();
    }

    /**
     * Prepare the widget for the active work.
     * 
     * @param widget the widget that is prepared
     */
    @Override
    @SuppressWarnings("nls")
    public void initWidget(final Widget widget) {
        if (!(widget instanceof IndicatorMask)) {
            throw new IllegalArgumentException(
                "Init Class requires a IndicatorMask widget");
        }
        final IndicatorMask indicator = (IndicatorMask) widget;
        final SpriteColor color = Graphics.getInstance().getSpriteColor();
        indicator.setDefaultColor(color);
        color.setAlpha(0.9f);

        if (type == TYPE_HEALTH) {
            color.set(201.f / 255.f, 1.f / 255.f, 1.f / 255.f);
            GUI.getInstance().getIndicators().registerHealth(indicator);
        } else if (type == TYPE_FOOD) {
            color.set(224.f / 255.f, 173.f / 255.f, 5.f / 255.f);
            GUI.getInstance().getIndicators().registerFood(indicator);
        } else if (type == TYPE_MANA) {
            color.set(53.f / 255.f, 22.f / 255.f, 214.f / 255.f);
            GUI.getInstance().getIndicators().registerMana(indicator);
        }
    }

    /**
     * Set the new type of the widget.
     * 
     * @param newType the constant value of the new type of the widget
     * @return the current instance of the indicator initialization script
     * @see #TYPE_HEALTH
     * @see #TYPE_FOOD
     * @see #TYPE_MANA
     */
    public IndicatorInit setType(final int newType) {
        type = newType;
        return this;
    }
}
