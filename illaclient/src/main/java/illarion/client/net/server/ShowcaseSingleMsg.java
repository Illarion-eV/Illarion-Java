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
import illarion.client.world.World;
import illarion.client.world.items.ItemContainer;
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Servermessage: Content of a single slot of a container. ({@link CommandList#MSG_SHOWCASE_SINGLE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_SHOWCASE_SINGLE)
public final class ShowcaseSingleMsg extends AbstractGuiMsg {
    private int containerId;
    private int containerSlot;
    @Nullable
    private ItemId slotItem;
    @Nullable
    private ItemCount slotItemCount;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        containerId = reader.readUByte();
        containerSlot = reader.readUShort();
        slotItem = new ItemId(reader);
        slotItemCount = ItemCount.getInstance(reader);
    }

    @Override
    public void executeUpdate() {
        ItemContainer container = World.getPlayer().getContainer(containerId);
        if (container != null) {
            container.setItem(containerSlot, slotItem, slotItemCount);
            World.getGameGui().getContainerGui().showContainer(container);
        }
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("ItemContainer: " + containerSlot + " Slot: " + containerSlot + ' ' + slotItem + ' ' +
                                slotItemCount);
    }
}
