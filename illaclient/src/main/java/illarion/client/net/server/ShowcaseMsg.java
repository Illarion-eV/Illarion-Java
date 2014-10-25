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
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

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
    /**
     * The event instance containing all decoded data that is send during the execution using the event bus.
     */
    @Nullable
    private OpenContainerEvent event;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        int containerId = reader.readUByte();
        String title = reader.readString();
        String description = reader.readString();
        int containerSize = reader.readUShort();
        int itemAmount = reader.readUShort();

        event = new OpenContainerEvent(containerId, title, description, containerSize);

        for (int i = 0; i < itemAmount; i++) {
            int itemPos = reader.readUShort();
            ItemId itemId = new ItemId(reader);
            ItemCount itemCount = ItemCount.getInstance(reader);

            event.addItem(new OpenContainerEvent.Item(itemPos, itemId, itemCount));
        }
    }

    @Override
    public void executeUpdate() {
        if (event == null) {
            throw new IllegalStateException("Executing the update before the decoding happened.");
        }
        World.getPlayer().onOpenContainerEvent(event);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("ItemContainer: " + ((event != null) ? event.getContainerId() : "NULL"));
    }
}
