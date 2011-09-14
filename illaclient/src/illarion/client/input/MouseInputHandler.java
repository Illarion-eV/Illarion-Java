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
package illarion.client.input;

import illarion.client.world.Game;
import illarion.input.MouseEvent;
import illarion.input.receiver.MouseEventReceiverComplex;

/**
 * This handler is used to process the mouse events that are not handled by the
 * GUI and are meant for the direct interaction with the game world.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class MouseInputHandler implements MouseEventReceiverComplex {
    private boolean walkToMouse = false;
    /**
     * This is the main function that receives the mouse events that need to be
     * handled.
     * 
     * @return <code>true</code> in case the mouse event got handled
     */
    @Override
    public boolean handleMouseEvent(final MouseEvent event) {
        switch (event.getEvent()) {
            case MouseEvent.EVENT_DRAG_START:
                walkToMouse = true;
            case MouseEvent.EVENT_LOCATION:
                if (walkToMouse) {
                    Game.getPlayer().getMovementHandler().walkTowards(event.getPosX(), event.getPosY());
                }
                break;
            case MouseEvent.EVENT_DRAG_END:
                Game.getPlayer().getMovementHandler().stopWalkTowards();
                walkToMouse = false;
                break;
            default:
                break;
        }
        
        return true;
    }

}
