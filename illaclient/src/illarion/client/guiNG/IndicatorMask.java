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
package illarion.client.guiNG;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import illarion.client.graphics.AnimationUtility;
import illarion.client.guiNG.elements.Image;
import illarion.client.guiNG.elements.Mask;

import illarion.graphics.SpriteColor;

/**
 * This is a special type of the mask that is used to draw the indicators. The
 * special function of this mask it modifies the shape of the mask dynamic in
 * order to provide a smooth animation of the indicator bars. Also it allows
 * effecting the state of the bar by the {@link illarion.client.guiNG.Indicator}
 * interface.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public class IndicatorMask extends Mask implements Indicator {
    /**
     * The float buffer that is used for the coordinates of the mask triangle.
     */
    private static final FloatBuffer BUFFER = ByteBuffer
        .allocateDirect((Float.SIZE / 8) * 8).order(ByteOrder.nativeOrder())
        .asFloatBuffer();

    /**
     * The serialization UID of this indicator mask.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The current value displayed by the indicator mask.
     */
    private int currentValue;

    /**
     * The default color of the indicator mask.
     */
    private transient SpriteColor defaultColor;

    /**
     * The maximal value of this indicator mask.
     */
    private int maxValue;

    /**
     * The value that is supposed to be displayed by the indicator.
     */
    private int targetValue;

    /**
     * Constructor to set the mask correctly.
     */
    public IndicatorMask() {
        setCoordBuffer(BUFFER);
    }

    /**
     * Draw this indicator. This takes care for the needed animations in case
     * those are needed.
     * 
     * @param delta the time in milliseconds since the last render
     */
    @Override
    public void draw(final int delta) {
        if (currentValue != targetValue) {
            currentValue =
                AnimationUtility.approach(currentValue, targetValue, 0,
                    maxValue, delta);
        }

        // mask removes everything. Spare the render and fall out again.
        if (currentValue == 0) {
            return;
        }

        if (currentValue == maxValue) {
            BUFFER.rewind().flip();
        } else {
            final float currValMod = (float) currentValue / (float) maxValue;
            BUFFER.clear();
            BUFFER.put(0).put(0);
            BUFFER.put(getWidth()).put(0);
            BUFFER.put(0).put(getHeight() * currValMod);
            BUFFER.put(getWidth()).put(getHeight() * currValMod);
            BUFFER.flip();
        }

        super.draw(delta);
    }

    /**
     * Walk over all the children of this widget and trigger the initialization.
     */
    @Override
    public void initWidget() {
        setCoordBuffer(BUFFER);
        super.initWidget();
    }

    /**
     * Reset the color of this indicator to its default one.
     */
    @Override
    public void resetColor() {
        ((Image) super.getChildByIndex(0)).setColor(defaultColor);
    }

    /**
     * Set the color of the indicator to a new one.
     * 
     * @param color the color that shall be used to render this indicator from
     *            now on
     */
    @Override
    public void setColor(final SpriteColor color) {
        ((Image) super.getChildByIndex(0)).setColor(color);
    }

    /**
     * Set the new default color of this indicator mask.
     * 
     * @param newDefaultColor the new default color of this indicator
     */
    public void setDefaultColor(final SpriteColor newDefaultColor) {
        defaultColor = newDefaultColor;
        resetColor();
    }

    /**
     * Set the new maximum value of this indicator mask.
     * 
     * @param newMaxValue the new maximum value of this mask
     */
    public void setMaximumValue(final int newMaxValue) {
        maxValue = newMaxValue;
    }

    /**
     * Set the value that is shown by this indicator. A animation is applied
     * automatically.
     * 
     * @param newValue the new value of this indicator
     */
    @Override
    public void setValue(final int newValue) {
        if ((newValue > 0) && !getParent().isVisible()) {
            getParent().setVisible(true);
        }
        targetValue = newValue;
    }
}
