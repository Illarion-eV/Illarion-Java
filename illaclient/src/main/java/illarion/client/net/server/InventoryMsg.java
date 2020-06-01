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
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Update of a inventory item
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_INVENTORY)
public final class InventoryMsg implements ServerReply {
    /**
     * New count of the item on the position.
     */
    @Nullable
    private ItemCount count;

    /**
     * New ID of the item.
     */
    @Nullable
    private ItemId itemId;

    /**
     * Position in the inventory.
     */
    private short location;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        location = reader.readUByte();
        itemId = new ItemId(reader);
        count = ItemCount.getInstance(reader.readUShort());
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        World.getPlayer().getInventory().setItem(location, itemId, count);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(InventoryMsg.class, "Slot: " + location, itemId, count);
    }
}
