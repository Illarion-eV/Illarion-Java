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

import java.nio.FloatBuffer;

import illarion.client.ClientWindow;

import illarion.graphics.Drawer;
import illarion.graphics.Graphics;
import illarion.graphics.MaskUtil;
import illarion.graphics.SpriteColor;

/**
 * This mask widget applies a mask to all its children. This mask is not
 * rectangular by all needs.
 * <p>
 * The float buffer that contains the coordinates for the mask is transient and
 * will not be stored with the rest of the GUI.
 * </p>
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class Mask extends Widget {
    /**
     * The instance of drawer that is used to draw the shape.
     */
    private static final Drawer drawer = Graphics.getInstance().getDrawer();

    /**
     * The instance of sprite color that is used to draw the mask.
     */
    private static final SpriteColor maskColor = Graphics.getInstance()
        .getSpriteColor();

    /**
     * The masking utility that is used to apply the mask properly.
     */
    private static final MaskUtil maskUtil = Graphics.getInstance().getMask();

    /**
     * The serialization UID for this mask widget.
     */
    private static final long serialVersionUID = 1L;

    static {
        maskColor.set(0.5f);
        maskColor.setAlpha(SpriteColor.COLOR_MAX);
    }

    /**
     * The coordinate buffer that contains the coordinates to draw the mask.
     */
    private transient FloatBuffer coordBuffer;

    /**
     * This variable is <code>true</code> in case the image shall be drawn on
     * the mask.
     */
    private boolean drawOnMask = true;

    /**
     * Draw the mask on the screen and limit the area for all children by this.
     * 
     * @param delta the time since the last render operation
     */
    @Override
    public void draw(final int delta) {
        if (!isVisible()) {
            return;
        }
        ClientWindow.getInstance().getRenderDisplay()
            .setAreaLimit(getAbsX(), getAbsY(), getWidth(), getHeight());

        final boolean applyMask =
            ((coordBuffer != null) && (coordBuffer.remaining() > 2));

        if (applyMask) {
            maskUtil.defineMask();

            drawer.drawTriangles(coordBuffer, maskColor);

            maskUtil.finishDefineMask();
            if (drawOnMask) {
                maskUtil.drawOnMask();
            } else {
                maskUtil.drawOffMask();
            }
        }
        super.draw(delta);

        if (applyMask) {
            maskUtil.resetMask();
        }

        ClientWindow.getInstance().getRenderDisplay().unsetAreaLimit();
    }

    /**
     * Set the coordinate buffer that contains the coordinates used to draw the
     * shape of the mask.
     * <p>
     * Do not forget to flip the buffer before drawing the first time.
     * </p>
     * 
     * @param newCoordBuffer the float buffer with the coordinates
     */
    public final void setCoordBuffer(final FloatBuffer newCoordBuffer) {
        coordBuffer = newCoordBuffer;
    }

    /**
     * Draw the children of this widget outside the mask.
     */
    public final void setDrawOffMask() {
        drawOnMask = false;
    }

    /**
     * Draw the children of this widget ON the mask.
     */
    public final void setDrawOnMask() {
        drawOnMask = true;
    }
}
