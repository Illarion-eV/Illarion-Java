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
import illarion.client.net.NetCommReader;
import illarion.client.net.server.events.LookAtInventoryEvent;
import org.bushe.swing.event.EventBus;

import java.io.IOException;

/**
 * Servermessage: Look at description of item in the inventory (
 * {@link illarion.client.net.CommandList#MSG_LOOKAT_INV}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class LookAtInvMsg extends AbstractReply {
    /**
     * Inventory slot that message is related to.
     */
    private short slot;

    /**
     * Text that is shown as look at on the item.
     */
    private String text;

    /**
     * The value of the item in copper coins.
     */
    private long value;

    /**
     * Default constructor for the inventory item look at text message.
     */
    public LookAtInvMsg() {
        super(CommandList.MSG_LOOKAT_INV);
    }

    /**
     * Create a new instance of the inventory item look at text message as recycle object.
     *
     * @return a new instance of this message object
     */
    @Override
    public LookAtInvMsg clone() {
        return new LookAtInvMsg();
    }

    /**
     * Decode the inventory item look at text data the receiver got and prepare
     * it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs
     *               to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *                     decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        slot = reader.readUByte();
        text = reader.readString();
        value = reader.readUInt();
    }

    /**
     * Execute the inventory item look at text message and send the decoded data to the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        EventBus.publish(new LookAtInventoryEvent(slot, text, value));

        return true;
    }

    /**
     * Clean the command up before recycling it.
     */
    @Override
    public void reset() {
        text = null;
    }

    /**
     * Get the data of this inventory item look at text message as string.
     *
     * @return the string that contains the values that were decoded for this  message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Slot: ").append(slot);
        builder.append(" Message: ").append(text);
        builder.append(" Item value: ").append(value);
        return toString(builder);
    }
}
