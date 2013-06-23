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

import illarion.client.net.client.CloseShowcaseCmd;
import illarion.client.net.client.PickUpAllItemsCmd;
import illarion.client.world.World;
import illarion.client.world.items.InventorySlot;
import org.bushe.swing.event.EventBus;
import org.illarion.engine.input.Key;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

/**
 * This class is used to generate events based on keys that got pressed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class KeyMapper {
    @Nonnull
    private final Map<Key, String> inputMap;

    public KeyMapper() {
        inputMap = new EnumMap<Key, String>(Key.class);

        inputMap.put(Key.Escape, "CloseGame");

        inputMap.put(Key.C, "ToggleCharacterWindow");
        inputMap.put(Key.Enter, "SelectChat");

        // walking commands
        inputMap.put(Key.CursorUp, "WalkNorthEast");
        inputMap.put(Key.CursorLeft, "WalkNorthWest");
        inputMap.put(Key.CursorDown, "WalkSouthWest");
        inputMap.put(Key.CursorRight, "WalkSouthEast");

        inputMap.put(Key.NumPad1, "WalkWest");
        inputMap.put(Key.NumPad2, "WalkSouthWest");
        inputMap.put(Key.NumPad3, "WalkSouth");
        inputMap.put(Key.NumPad4, "WalkNorthWest");
        inputMap.put(Key.NumPad6, "WalkSouthEast");
        inputMap.put(Key.NumPad7, "WalkNorth");
        inputMap.put(Key.NumPad8, "WalkNorthEast");
        inputMap.put(Key.NumPad9, "WalkEast");

        inputMap.put(Key.PageUp, "WalkEast");
        inputMap.put(Key.PageDown, "WalkSouth");
        inputMap.put(Key.End, "WalkWest");
        inputMap.put(Key.Home, "WalkNorth");
    }

    public void handleKeyInput(@Nonnull final Key key) {
        switch (key) {
            case B:
                if (World.getPlayer().hasContainer(0)) {
                    World.getPlayer().removeContainer(0);
                    World.getNet().sendCommand(new CloseShowcaseCmd(0));
                } else {
                    final InventorySlot slot = World.getPlayer().getInventory().getItem(0);
                    if (slot.containsItem()) {
                        slot.getInteractive().openContainer();
                    }
                }
                break;
            case I:
                World.getGameGui().getInventoryGui().toggleInventory();
            case Q:
            case J:
                World.getGameGui().getQuestGui().toggleQuestLog();
                break;
            case P:
                World.getNet().sendCommand(new PickUpAllItemsCmd());
                break;
            default:
                if (inputMap.containsKey(key)) {
                    EventBus.publish(InputReceiver.EB_TOPIC, inputMap.get(key));
                }
        }
    }
}
