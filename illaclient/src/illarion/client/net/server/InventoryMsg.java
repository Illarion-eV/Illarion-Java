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
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.server.events.InventoryUpdateEvent;
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.bushe.swing.event.EventBus;

import java.io.IOException;

/**
 * Servermessage: Update of a inventory item (
 * {@link CommandList#MSG_INVENTORY}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class InventoryMsg extends AbstractReply {
    /**
     * New count of the item on the position.
     */
    private ItemCount count;

    /**
     * New ID of the item.
     */
    private ItemId itemId;

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
     *               to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *                     decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        location = reader.readUByte();
        itemId = new ItemId(reader);
        count = new ItemCount(reader);
    }

    /**
     * Execute the inventory item message and send the decoded data to the rest
     * of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        EventBus.publish(new InventoryUpdateEvent(itemId, location, count));
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
