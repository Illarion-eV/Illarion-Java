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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import illarion.client.guiNG.elements.Mask;
import illarion.client.guiNG.elements.Widget;

/**
 * This class takes care for setting up the mask that is used to display the
 * minimap properly on the screen.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class MinimapMaskInit implements WidgetInit {
    /**
     * The instance used used for all requested instances of this class.
     */
    private static MinimapMaskInit instance = null;

    /**
     * The serialization UID this initialization script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The private constructor to avoid instances created uncontrolled.
     */
    private MinimapMaskInit() {
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
    public static MinimapMaskInit getInstance() {
        if (instance == null) {
            instance = new MinimapMaskInit();
        }
        return instance;
    }

    /**
     * Prepare the widget for the active work.
     * 
     * @param widget the widget that is prepared
     */
    @Override
    @SuppressWarnings("nls")
    public void initWidget(final Widget widget) {
        if (!(widget instanceof Mask)) {
            throw new IllegalArgumentException(
                "Init Class requires a Mask widget");
        }
        final Mask minimapMask = (Mask) widget;
        final FloatBuffer buffer =
            ByteBuffer.allocateDirect((Float.SIZE / 8) * 16)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        buffer.put(2).put(80);
        buffer.put(25).put(137);
        buffer.put(23).put(25);
        buffer.put(80).put(158);
        buffer.put(80).put(2);
        buffer.put(135).put(137);
        buffer.put(138).put(25);
        buffer.put(158).put(80);
        buffer.flip();

        minimapMask.setCoordBuffer(buffer);
        minimapMask.setDrawOnMask();
    }

}
