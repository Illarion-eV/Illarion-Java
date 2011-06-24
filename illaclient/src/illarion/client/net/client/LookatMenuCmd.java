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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;

/*
 * TODO: Check if its really needed to send the slot as well as the item ID in
 * this menu.
 */
/**
 * Client Command: Look at the slot within a menu (
 * {@link illarion.client.net.CommandList#CMD_LOOKAT_MENU}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class LookatMenuCmd extends AbstractCommand {
    /**
     * The ID of the item we are looking at.
     */
    private int itemId;

    /**
     * The slot within the menu we are looking at.
     */
    private byte slot;

    /**
     * Default constructor for the look at menu command.
     */
    public LookatMenuCmd() {
        super(CommandList.CMD_LOOKAT_MENU);
    }

    /**
     * Create a duplicate of this look at menu command.
     * 
     * @return new instance of this command
     */
    @Override
    public LookatMenuCmd clone() {
        return new LookatMenuCmd();
    }

    /**
     * Encode the data of this look at menu command and put the values into the
     * buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(slot);
        writer.writeUShort(itemId);
    }

    /**
     * Set the target menu slot and the ID of the item on this slot we want to
     * look at.
     * 
     * @param lookAtSlot the slot we want to look at
     * @param lookAtItemId the ID of the item that is on the slot we are looking
     *            at
     */
    public void setItem(final int lookAtSlot, final int lookAtItemId) {
        slot = (byte) lookAtSlot;
        itemId = lookAtItemId;
    }

    /**
     * Get the data of this look at menu command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Slot: " + slot + " Item on the slot: " + itemId);
    }
}
