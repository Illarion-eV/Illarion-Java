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

import illarion.client.graphics.AnimationUtility;
import illarion.client.guiNG.elements.AbstractImage;
import illarion.client.guiNG.elements.Button;
import illarion.client.guiNG.elements.Widget;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

import illarion.input.InputManager;
import illarion.input.MouseManager;

/**
 * This is the button used for the locks in the client. It has a small addition
 * that allows the button to fade in and out in case the mouse points at its
 * parent.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class LockButton extends Button {
    /**
     * The mouse manager that is used.
     */
    private static final MouseManager MOUSE = InputManager.getInstance()
        .getMouseManager();

    /**
     * The serialization UID of this widget.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The color thats alpha is changed in order to get the fade in and out
     * effects properly working.
     */
    private transient SpriteColor color;

    /**
     * The target alpha value that shall be reached.
     */
    private int targetAlpha;

    /**
     * Constructor of the lock button. This prepares the requires entries for
     * this button.
     */
    public LockButton() {
        color = Graphics.getInstance().getSpriteColor();
        color.set(SpriteColor.COLOR_MAX);
    }

    /**
     * Draw the widget. In this case also the color is updated for the fade in
     * and out effects.
     * 
     * @param delta the time in milliseconds since the last render
     */
    @Override
    public void draw(final int delta) {
        if (getParent().isInside(MOUSE.getMousePosX(), MOUSE.getMousePosY())) {
            targetAlpha = SpriteColor.COLOR_MAX;
        } else {
            targetAlpha = SpriteColor.COLOR_MIN;
        }

        if (color.getAlphai() != targetAlpha) {
            color.setAlpha(AnimationUtility.translateAlpha(color.getAlphai(),
                targetAlpha, 15, delta));
        }
        super.draw(delta);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initWidget() {
        final SpriteColor usedColor = Graphics.getInstance().getSpriteColor();
        usedColor.set(SpriteColor.COLOR_MAX);

        setColor(usedColor);

        final int count = getChildrenCount();
        for (int i = 0; i < count; ++i) {
            final Widget child = getChildByIndex(i);
            if (child instanceof AbstractImage) {
                ((AbstractImage) child).setColor(usedColor);
            }
        }
        super.initWidget();
    }

    /**
     * Addition to the default insert child. This causes in addition that the
     * proper color value to set to this image in case the added child is
     * inherit from a {@link illarion.client.guiNG.elements.AbstractImage}.
     */
    @Override
    public void insertChild(final Widget widget, final int index) {
        if (widget instanceof AbstractImage) {
            ((AbstractImage) widget).setColor(color);
        }
        super.insertChild(widget, index);
    }

    /**
     * Set the color that is used to render all following images under this
     * widget.
     * 
     * @param newColor the new color instance that is used.
     */
    public void setColor(final SpriteColor newColor) {
        color = newColor;
    }
}
