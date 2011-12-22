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

import illarion.client.world.PlayerMovement;
import illarion.client.world.World;
import illarion.common.util.Location;

import org.bushe.swing.event.EventBus;
import org.newdawn.slick.Input;
import org.newdawn.slick.util.InputAdapter;

/**
 * This class is used to receive and forward all user input.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InputReceiver extends InputAdapter {
    /**
     * The topic that is in general used to publish input events.
     */
    public static final String EB_TOPIC = "InputEvent";
    
    private final KeyMapper keyMapper = new KeyMapper();
    /**
     * @see org.newdawn.slick.InputListener#keyPressed(int, char)
     */
    public void keyPressed(final int key, final char c) {
        switch (key) {
            case Input.KEY_DOWN:
                World.getPlayer().getMovementHandler().requestMove(Location.DIR_SOUTH, PlayerMovement.MOVE_MODE_WALK);
                break;
            case Input.KEY_UP:
                World.getPlayer().getMovementHandler().requestMove(Location.DIR_NORTH, PlayerMovement.MOVE_MODE_WALK);
                break;
            case Input.KEY_LEFT:
                World.getPlayer().getMovementHandler().requestMove(Location.DIR_WEST, PlayerMovement.MOVE_MODE_WALK);
                break;
            case Input.KEY_RIGHT:
                World.getPlayer().getMovementHandler().requestMove(Location.DIR_EAST, PlayerMovement.MOVE_MODE_WALK);
                break;
            default: keyMapper.handleKeyInput(key); break;
        }
    }
    
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
        EventBus.publish(EB_TOPIC, new DragOnMapEvent(oldx, oldy, newx, newy));
    }
}
