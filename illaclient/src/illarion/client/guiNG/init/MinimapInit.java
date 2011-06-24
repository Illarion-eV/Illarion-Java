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

import illarion.client.guiNG.elements.ImageZoomable;
import illarion.client.guiNG.elements.Widget;
import illarion.client.world.Game;

import illarion.graphics.Sprite;

/**
 * This class takes care for setting up the display of the minimap correctly.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class MinimapInit implements WidgetInit {
    /**
     * The instance used used for all requested instances of this class.
     */
    private static MinimapInit instance = null;

    /**
     * The serialization UID this initialization script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The private constructor to avoid instances created uncontrolled.
     */
    private MinimapInit() {
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
    public static MinimapInit getInstance() {
        if (instance == null) {
            instance = new MinimapInit();
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
        if (!(widget instanceof ImageZoomable)) {
            throw new IllegalArgumentException(
                "Initclass requires ImageZoomable widget");
        }
        final ImageZoomable minimap = (ImageZoomable) widget;
        final Sprite minimapSprite = Game.getMap().getMinimap().getMinimap();
        minimapSprite.setRotation(45);
        minimapSprite.setAlign(Sprite.HAlign.center, Sprite.VAlign.middle);
        minimap.setDrawingOffset(minimap.getWidth() / 2,
            minimap.getHeight() / 2);

        // minimapSprite.setRotation(45);
        minimap.setImage(minimapSprite);
    }

}
