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
import illarion.client.world.items.MerchantItem;
import illarion.client.world.items.MerchantItem.MerchantItemType;
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This server message is used to make the client showing a merchant dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_DIALOG_MERCHANT)
public final class DialogMerchantMsg implements ServerReply {
    /**
     * The title of the dialog window.
     */
    private String title;

    /**
     * The ID of the dialog that needs to be returned in order to inform the server that the window was closed.
     */
    private int dialogId;

    /**
     * The items that were received from the server.
     */
    private List<MerchantItem> items;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        title = reader.readString();
        items = new ArrayList<>();

        int entriesSell = reader.readUByte();
        for (int i = 0; i < entriesSell; i++) {
            ItemId itemId = new ItemId(reader);
            String name = reader.readString();
            long itemValue = reader.readUInt();
            ItemCount bundleSize = ItemCount.getInstance(reader);

            items.add(new MerchantItem(i, MerchantItemType.SellingItem, itemId, name, itemValue,
                                       bundleSize));
        }

        int entriesBuyPrimary = reader.readUByte();
        for (int i = 0; i < entriesBuyPrimary; i++) {
            ItemId itemId = new ItemId(reader);
            String name = reader.readString();
            long itemValue = reader.readUInt();

            items.add(new MerchantItem(i, MerchantItemType.BuyingPrimaryItem, itemId, name, itemValue));
        }

        int entriesBuySecondary = reader.readUByte();
        for (int i = 0; i < entriesBuySecondary; i++) {
            ItemId itemId = new ItemId(reader);
            String name = reader.readString();
            long itemValue = reader.readUInt();

            items.add(new MerchantItem(i, MerchantItemType.BuyingSecondaryItem, itemId, name, itemValue));
        }

        dialogId = reader.readInt();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((title == null) || (items == null)) {
            throw new NotDecodedException();
        }
        World.getPlayer().openMerchantDialog(dialogId, title, items);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(DialogMerchantMsg.class, "ID: " + dialogId, title,
                "Items: " + ((items == null) ? "0" : items.size()));
    }
}
