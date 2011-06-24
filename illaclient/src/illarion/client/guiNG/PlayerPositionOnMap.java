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

import illarion.client.guiNG.elements.SolidColor;
import illarion.client.world.Game;

/**
 * This widget simply draws a static color on the player position.
 * 
 * @author Natal Venetz
 * @since 1.22
 * @version 1.22
 */
public class PlayerPositionOnMap extends SolidColor {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * change the position of the solid color.
     * 
     * @param delta the time since the render function was called last time
     */
    @Override
    public void draw(final int delta) {
        if (!isVisible()) {
            return;
        }
        super.setRelY(Game.getPlayer().getLocation().getScX());
        super.setRelX(Game.getPlayer().getLocation().getScY());

        super.draw(delta);
    }
}
