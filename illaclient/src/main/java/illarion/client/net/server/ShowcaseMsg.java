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
import java.util.Arrays;

/**
 * Server message: Content of a container
 *
 * @author Blay09
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_SHOWCASE)
public final class ShowcaseMsg implements ServerReply {
    /**
     * The ID of the container.
     */
    private int containerId;

    /**
     * The title of the container.
     */
    @Nullable
    private String title;

    /**
     * The description of the container.
     */
    @Nullable
    private String description;

    /**
     * The amount of slots in the container.
     */
    private int slotCount;

    /**
     * The positions of the filled slots.
     */
    @Nullable
    private int[] slots;

    /**
     * The item IDs in the filled slots.
     */
    @Nullable
    private ItemId[] itemIds;

    /**
     * The amount of items in the filled slots.
     */
    @Nullable
    private ItemCount[] itemCounts;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        containerId = reader.readUByte();
        title = reader.readString();
        description = reader.readString();
        slotCount = reader.readUShort();

        int itemAmount = reader.readUShort();

        slots = new int[itemAmount];
        itemIds = new ItemId[itemAmount];
        itemCounts = new ItemCount[itemAmount];

        for (int i = 0; i < itemAmount; i++) {
            int itemPos = reader.readUShort();
            ItemId itemId = new ItemId(reader);
            ItemCount itemCount = ItemCount.getInstance(reader.readUShort());

            slots[i] = itemPos;
            itemIds[i] = itemId;
            itemCounts[i] = itemCount;
        }
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        //noinspection OverlyComplexBooleanExpression
        if ((title == null) || (description == null) || (slots == null) || (itemIds == null) || (itemCounts == null)) {
            throw new NotDecodedException();
        }
        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        ItemContainer container = World.getPlayer().getOrCreateContainer(containerId, title, description, slotCount);
        boolean[] updatedSlot = new boolean[slotCount];
        Arrays.fill(updatedSlot, false);

        for (int i = 0; i < slots.length; i++) {
            updatedSlot[slots[i]] = true;
            container.setItem(slots[i], itemIds[i], itemCounts[i]);
        }

        for (int i = 0; i < slotCount; i++) {
            if (!updatedSlot[i]) {
                container.setItem(i, null, null);
            }
        }

        World.getGameGui().getContainerGui().showContainer(container);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(ShowcaseMsg.class, "ID: " + containerId, title, "Slots: " + slotCount);
    }
}
