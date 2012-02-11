/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.input;

import org.bushe.swing.event.EventBus;
import org.newdawn.slick.util.InputAdapter;

import illarion.client.world.World;

/**
 * This class is used to receive and forward all user input.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InputReceiver
        extends InputAdapter {
    /**
     * The topic that is in general used to publish input events.
     */
    public static final String EB_TOPIC = "InputEvent";

    private final KeyMapper keyMapper = new KeyMapper();

    /**
     * @see org.newdawn.slick.InputListener#keyPressed(int, char)
     */
    public void keyPressed(final int key, final char c) {
        keyMapper.handleKeyInput(key);
    }

    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
        EventBus.publish(EB_TOPIC, new DragOnMapEvent(oldx, oldy, newx, newy));
    }
    
    public void mouseReleased(int button, int x, int y) {
        World.getPlayer().getMovementHandler().stopWalkTowards();
    }
}
