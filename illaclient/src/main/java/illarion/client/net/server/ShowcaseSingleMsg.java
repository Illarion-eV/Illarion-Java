/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import illarion.client.world.World;
import illarion.client.world.items.ItemContainer;
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Content of a single slot of a container.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_SHOWCASE_SINGLE)
public final class ShowcaseSingleMsg implements ServerReply {
    /**
     * The ID of the container.
     */
    private int containerId;

    /**
     * The slot in the container that is updated.
     */
    private int containerSlot;

    /**
     * The id of the new item in the slot.
     */
    @Nullable
    private ItemId slotItem;

    /**
     * The new amount of items in the slot.
     */
    @Nullable
    private ItemCount slotItemCount;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        containerId = reader.readUByte();
        containerSlot = reader.readUShort();
        slotItem = new ItemId(reader);
        slotItemCount = ItemCount.getInstance(reader.readUShort());
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((slotItem == null) || (slotItemCount == null)) {
            throw new NotDecodedException();
        }

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        ItemContainer container = World.getPlayer().getContainer(containerId);
        if (container != null) {
            container.setItem(containerSlot, slotItem, slotItemCount);
            World.getGameGui().getContainerGui().showContainer(container);
            return ServerReplyResult.Success;
        }
        return ServerReplyResult.Failed;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(ShowcaseSingleMsg.class, "ID: " + containerId, "Slot: " + containerSlot);
    }
}
