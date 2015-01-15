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
import illarion.client.net.server.events.DialogCraftingReceivedEvent;
import illarion.client.world.items.CraftingIngredientItem;
import illarion.client.world.items.CraftingItem;
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import javolution.text.TextBuilder;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Crafting Request ({@link CommandList#MSG_DIALOG_INPUT}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_DIALOG_CRAFTING)
public final class DialogCraftingMsg extends AbstractGuiMsg {
    /**
     * The title that is supposed to be displayed in the dialog.
     */
    private String title;

    /**
     * The group names
     */
    private String[] groups;

    /**
     * The crafting item.
     */
    private CraftingItem[] craftItems;

    /**
     * The ID of this request.
     */
    private int requestId;

    /**
     * Decode the text request the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        title = reader.readString();

        groups = new String[reader.readUByte()];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = reader.readString();
        }

        craftItems = new CraftingItem[reader.readUByte()];
        for (int i = 0; i < craftItems.length; i++) {
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

            craftItems[i] = new CraftingItem(itemIndex, group, itemId, name, buildTime, craftStackSize, ingredients);
        }

        requestId = reader.readInt();
    }

    /**
     * Execute the text request message and send the decoded data to the rest of the client.
     */
    @Override
    public void executeUpdate() {
        EventBus.publish(new DialogCraftingReceivedEvent(requestId, title, groups, craftItems));
    }

    /**
     * Get the data of this text request message as string.
     *
     * @return the string that contains the values that were decoded for this message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        TextBuilder builder = new TextBuilder();
        builder.append("title: ").append(title);
        builder.append(" id: ").append(requestId);
        return toString(builder.toString());
    }
}
