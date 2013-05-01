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
public final class ShowcaseSingleMsg extends AbstractReply {
    private int containerId;
    private int containerSlot;
    @Nullable
    private ItemId slotItem;
    @Nullable
    private ItemCount slotItemCount;

    /**
     * Decode the container data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws java.io.IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
        containerId = reader.readUByte();
        containerSlot = reader.readUShort();
        slotItem = new ItemId(reader);
        slotItemCount = ItemCount.getInstance(reader);
    }

    /**
     * Execute the container message and send the decoded data to the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        final ItemContainer container = World.getPlayer().getContainer(containerId);
        if (container != null) {
            container.setItem(containerSlot, slotItem, slotItemCount);
            World.getGameGui().getContainerGui().showContainer(container);
        }
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
        return toString("ItemContainer: " + containerSlot + " Slot: " + containerSlot + ' ' + slotItem + ' ' +
                slotItemCount);
    }
}
