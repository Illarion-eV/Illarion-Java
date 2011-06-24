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
package illarion.client.net.server;

import java.io.IOException;

import illarion.client.guiNG.GUI;
import illarion.client.guiNG.Inventory;
import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;

/**
 * Servermessage: Update of a inventory item (
 * {@link illarion.client.net.CommandList#MSG_INVENTORY}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class InventoryMsg extends AbstractReply {
    /**
     * New count of the item on the position.
     */
    private short count;

    /**
     * New ID of the item.
     */
    private int itemId;

    /**
     * Position in the inventory.
     */
    private short location;

    /**
     * Default constructor for the inventory item message.
     */
    public InventoryMsg() {
        super(CommandList.MSG_INVENTORY);
    }

    /**
     * Create a new instance of the inventory item message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public InventoryMsg clone() {
        return new InventoryMsg();
    }

    /**
     * Decode the inventory item data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        location = reader.readUByte();
        itemId = reader.readUShort();
        count = reader.readUByte();
    }

    /**
     * Execute the inventory item message and send the decoded data to the rest
     * of the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        final Inventory inv = GUI.getInstance().getInventory();
        if (inv == null) {
            return false;
        }
        inv.setItemId(location, itemId, count);
        return true;
    }

    /**
     * Get the data of this inventory item message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("pos: ");
        builder.append(location);
        builder.append(" itemid: ");
        builder.append(itemId);
        builder.append(" count: ");
        builder.append(count);
        return toString(builder.toString());
    }
}
