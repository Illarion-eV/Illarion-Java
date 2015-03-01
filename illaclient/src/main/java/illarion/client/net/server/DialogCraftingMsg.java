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
import illarion.client.world.items.CraftingIngredientItem;
import illarion.client.world.items.CraftingItem;
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;

/**
 * Server message: Open a crafting dialog
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_DIALOG_CRAFTING)
public final class DialogCraftingMsg implements ServerReply {
    /**
     * The title that is supposed to be displayed in the dialog.
     */
    private String title;

    /**
     * The group names
     */
    @Nullable
    private String[] groups;

    /**
     * The crafting item.
     */
    @Nullable
    private CraftingItem[] craftItems;

    /**
     * The ID of this request.
     */
    private int requestId;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        title = reader.readString();

        groups = new String[reader.readUByte()];
        int groupCount = groups.length;
        for (int i = 0; i < groupCount; i++) {
            assert groups != null;
            groups[i] = reader.readString();
        }

        craftItems = new CraftingItem[reader.readUByte()];
        int itemsCount = craftItems.length;
        for (int i = 0; i < itemsCount; i++) {
            int itemIndex = reader.readUByte();
            int group = reader.readUByte();
            ItemId itemId = new ItemId(reader);
            String name = reader.readString();
            int buildTime = reader.readUShort();
            ItemCount craftStackSize = ItemCount.getInstance(reader.readUByte());

            CraftingIngredientItem[] ingredients = new CraftingIngredientItem[reader.readUByte()];
            for (int k = 0; k < ingredients.length; k++) {
                ItemId ingredientId = new ItemId(reader);
                ItemCount ingredientCount = ItemCount.getInstance(reader.readUByte());

                ingredients[k] = new CraftingIngredientItem(ingredientId, ingredientCount);
            }

            assert craftItems != null;
            craftItems[i] = new CraftingItem(itemIndex, group, itemId, name, buildTime, craftStackSize, ingredients);
        }

        requestId = reader.readInt();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((groups == null) || (craftItems == null) || (title == null)) {
            throw new NotDecodedException();
        }

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        World.getGameGui().getDialogCraftingGui().showCraftingDialog(requestId, title, Arrays.asList(groups),
                Arrays.asList(craftItems));
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(DialogCraftingMsg.class, "ID: " + requestId, title);
    }
}
