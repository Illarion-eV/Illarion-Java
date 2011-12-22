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

import org.bushe.swing.event.EventBus;
import org.newdawn.slick.Input;

import gnu.trove.map.hash.TIntObjectHashMap;

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
    }
    
    public void handleKeyInput(final int key) {
        if (inputMap.contains(key)) {
            EventBus.publish(InputReceiver.EB_TOPIC, inputMap.get(key));
        }
    }
}
