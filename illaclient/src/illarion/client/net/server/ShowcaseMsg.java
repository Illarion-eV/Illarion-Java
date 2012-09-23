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
import illarion.client.net.server.events.OpenContainerEvent;
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.bushe.swing.event.EventBus;

import java.io.IOException;

/**
 * Servermessage: Content of a container ({@link CommandList#MSG_SHOWCASE}).
 *
 * @author Blay09
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class ShowcaseMsg extends AbstractReply {
    private OpenContainerEvent event;

    /**
     * Default constructor for the container message.
     */
    public ShowcaseMsg() {
        super(CommandList.MSG_SHOWCASE);
    }

    /**
     * Create a new instance of the container message as recycle object.
     *
     * @return a new instance of this message object
     */
    @Override
    public ShowcaseMsg clone() {
        return new ShowcaseMsg();
    }

    /**
     * Decode the container data the receiver got and prepare it for the
     * execution.
     *
     * @param reader the receiver that got the data from the server that needs
     *               to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *                     decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        final int containerId = reader.readUByte();
        final int containerSize = reader.readUShort();
        final int itemAmount = reader.readUShort();

        event = new OpenContainerEvent(containerId, containerSize);

        for (int i = 0; i < itemAmount; i++) {
            final int itemPos = reader.readUShort();
            final ItemId itemId = new ItemId(reader);
            final ItemCount itemCount = ItemCount.getInstance(reader);

            event.addItem(itemPos, new OpenContainerEvent.Item(itemId, itemCount));
        }
    }

    /**
     * Execute the container message and send the decoded data to the rest of
     * the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        EventBus.publish(event);
        event = null;
        return true;
    }

    /**
     * Get the data of this container message as string.
     *
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("ItemContainer: " + event.getContainerId());
    }
}
