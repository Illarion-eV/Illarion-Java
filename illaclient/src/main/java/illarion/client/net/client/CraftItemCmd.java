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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * This command is used to craft a item from a crafting dialog.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
@Immutable
public final class CraftItemCmd extends AbstractCommand {
    /**
     * The ID of the dialog to interact with.
     */
    private final int dialogId;

    /**
     * The index of the item in the crafting list that is referred to.
     */
    private final int craftingIndex;

    /**
     * The amount of items to be crafted.
     */
    private final int amount;

    /**
     * Default constructor for the trade item command.
     *
     * @param dialogId      the dialog ID of the dialog to craft a item from
     * @param craftingIndex the index of the item to craft
     * @param amount        the amount of items to create as a batch
     */
    public CraftItemCmd(final int dialogId, final int craftingIndex, final int amount) {
        super(CommandList.CMD_CRAFT_ITEM);

        this.dialogId = dialogId;
        this.craftingIndex = craftingIndex;
        this.amount = amount;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeInt(dialogId);
        writer.writeByte((byte) 1);
        writer.writeUByte((short) craftingIndex);
        writer.writeUByte((short) amount);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString("dialog ID: " + dialogId);
    }
}
