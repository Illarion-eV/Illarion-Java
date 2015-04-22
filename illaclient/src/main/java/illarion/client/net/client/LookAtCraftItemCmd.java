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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;

import javax.annotation.Nonnull;

/**
 * This command is used to request a look at on a item inside the crafting menu.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class LookAtCraftItemCmd extends AbstractCommand {
    /**
     * The ID of the crafting dialog.
     */
    private final int dialogId;

    /**
     * The index of the item to look at.
     */
    private final short itemIndex;

    /**
     * Default constructor for the looking at a crafting item.
     *
     * @param dialogId the ID of the dialog to close
     * @param itemIndex the index of the item to look at
     */
    public LookAtCraftItemCmd(int dialogId, int itemIndex) {
        super(CommandList.CMD_CRAFT_ITEM);

        this.dialogId = dialogId;
        this.itemIndex = (short) itemIndex;
    }

    @Override
    public void encode(@Nonnull NetCommWriter writer) {
        writer.writeInt(dialogId);
        writer.writeByte((byte) 2);
        writer.writeUByte(itemIndex);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString("Dialog ID: " + dialogId + " Look at index: " + itemIndex);
    }
}
