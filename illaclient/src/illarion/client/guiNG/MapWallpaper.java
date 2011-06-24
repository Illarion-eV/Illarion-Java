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

import illarion.client.guiNG.elements.DesktopWallpaper;
import illarion.client.world.Game;

/**
 * This is the wallpaper that is displayed upon the desktop and shows the map
 * along with all tiles, items and characters and the weather effects.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class MapWallpaper implements DesktopWallpaper {
    /**
     * The serialization UID of this map wallpaper.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Draw the wallpaper of the map and the weather on the display.
     */
    @Override
    public void draw(final int delta, final int width, final int height) {
        // render the map
        Game.getDisplay().render(delta, width, height);

        // render the weather
        Game.getWeather().render(delta, width, height);
    }
}
