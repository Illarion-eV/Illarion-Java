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

import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;

/**
 * Servermessage: Look at description of item in the inventory (
 * {@link illarion.client.net.CommandList#MSG_LOOKAT_INV}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class LookatInvMsg extends AbstractReply {
    /**
     * Inventory slot that message is related to.
     */
    private short slot;

    /**
     * Text that is shown as look at on the item.
     */
    private String text;

    /**
     * Default constructor for the inventory item look at text message.
     */
    public LookatInvMsg() {
        super(CommandList.MSG_LOOKAT_INV);
    }

    /**
     * Create a new instance of the inventory item look at text message as
     * recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public LookatInvMsg clone() {
        return new LookatInvMsg();
    }

    /**
     * Decode the inventory item look at text data the receiver got and prepare
     * it for the execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        slot = reader.readUByte();
        text = reader.readString();
    }

    /**
     * Execute the inventory item look at text message and send the decoded data
     * to the rest of the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        // Gui.getInstance().getContainers()
        // .lookAtResult(Containers.INVENTORY, slot, text);
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
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Slot: " + slot + " Message: " + text);
    }
}
