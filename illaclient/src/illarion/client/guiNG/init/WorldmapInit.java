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
 * This class takes care for setting up the display of the worldmap correctly.
 * 
 * @author Natal Venetz
 * @since 1.22
 * @version 1.22
 */
public final class WorldmapInit implements WidgetInit {
    /**
     * The instance used used for all requested instances of this class.
     */
    private static WorldmapInit instance = null;

    /**
     * The serialization UID this initialization script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The private constructor to avoid instances created uncontrolled.
     */
    private WorldmapInit() {
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
    public static WorldmapInit getInstance() {
        if (instance == null) {
            instance = new WorldmapInit();
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
        final ImageZoomable worldmap = (ImageZoomable) widget;
        final Sprite minimapSprite = Game.getMap().getMinimap().getWorldmap();
        minimapSprite.setRotation(90);
        minimapSprite.setAlign(Sprite.HAlign.center, Sprite.VAlign.middle);
        worldmap.setDrawingOffset(worldmap.getWidth() / 2,
            worldmap.getHeight() / 2);

        worldmap.setImage(minimapSprite);
    }

}
