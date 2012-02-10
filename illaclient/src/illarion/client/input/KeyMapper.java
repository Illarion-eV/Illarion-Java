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

import gnu.trove.map.hash.TIntObjectHashMap;

import org.bushe.swing.event.EventBus;
import org.newdawn.slick.Input;

/**
 * This class is used to generate events based on keys that got pressed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class KeyMapper {
    private final TIntObjectHashMap<String> inputMap;

    public KeyMapper() {
        inputMap = new TIntObjectHashMap<String>();
        inputMap.put(Input.KEY_I, "ToggleInventory");
        inputMap.put(Input.KEY_ENTER, "SelectChat");

        // walking commands
        inputMap.put(Input.KEY_W, "WalkNorthEast");
        inputMap.put(Input.KEY_A, "WalkNorthWest");
        inputMap.put(Input.KEY_S, "WalkSouthWest");
        inputMap.put(Input.KEY_D, "WalkSouthEast");

        inputMap.put(Input.KEY_UP, "WalkNorthEast");
        inputMap.put(Input.KEY_LEFT, "WalkNorthWest");
        inputMap.put(Input.KEY_DOWN, "WalkSouthWest");
        inputMap.put(Input.KEY_RIGHT, "WalkSouthEast");

        inputMap.put(Input.KEY_NUMPAD1, "WalkWest");
        inputMap.put(Input.KEY_NUMPAD2, "WalkSouthWest");
        inputMap.put(Input.KEY_NUMPAD3, "WalkSouth");
        inputMap.put(Input.KEY_NUMPAD4, "WalkNorthWest");
        inputMap.put(Input.KEY_NUMPAD6, "WalkSouthEast");
        inputMap.put(Input.KEY_NUMPAD7, "WalkNorth");
        inputMap.put(Input.KEY_NUMPAD8, "WalkNorthEast");
        inputMap.put(Input.KEY_NUMPAD9, "WalkEast");
    }

    public void handleKeyInput(final int key) {
        if (inputMap.contains(key)) {
            EventBus.publish(InputReceiver.EB_TOPIC, inputMap.get(key));
        }
    }
}
