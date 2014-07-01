/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.net.server.events.OpenContainerEvent;
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Servermessage: Content of a container ({@link CommandList#MSG_SHOWCASE}).
 *
 * @author Blay09
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_SHOWCASE)
public final class ShowcaseMsg extends AbstractGuiMsg {
    @Nullable
    private OpenContainerEvent event;
    @Nullable
    private String name;
    @Nullable
    private String description;

    /**
     * Decode the container data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        int containerId = reader.readUByte();
        name = reader.readString();
        description = reader.readString();
        int containerSize = reader.readUShort();
        int itemAmount = reader.readUShort();

        event = new OpenContainerEvent(containerId, containerSize);

        for (int i = 0; i < itemAmount; i++) {
            int itemPos = reader.readUShort();
            ItemId itemId = new ItemId(reader);
            ItemCount itemCount = ItemCount.getInstance(reader);

            event.addItem(itemPos, new OpenContainerEvent.Item(itemId, itemCount));
        }
    }

    /**
     * Execute the container message and send the decoded data to the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        EventBus.publish(event);
        return true;
    }

    /**
     * Get the data of this container message as string.
     *
     * @return the string that contains the values that were decoded for this message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("ItemContainer: " + ((event != null) ? event.getContainerId() : "NULL"));
    }
}
