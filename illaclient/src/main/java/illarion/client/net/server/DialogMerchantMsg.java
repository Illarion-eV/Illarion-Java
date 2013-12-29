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
import illarion.client.net.server.events.DialogMerchantReceivedEvent;
import illarion.client.world.items.MerchantItem;
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import javolution.text.TextBuilder;
import org.bushe.swing.event.EventBus;

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
public final class DialogMerchantMsg extends AbstractGuiMsg {
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
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
        title = reader.readString();
        items = new ArrayList<>();

        final int entriesSell = reader.readUByte();
        for (int i = 0; i < entriesSell; i++) {
            final ItemId itemId = new ItemId(reader);
            final String name = reader.readString();
            final long itemValue = reader.readUInt();
            final ItemCount bundleSize = ItemCount.getInstance(reader);

            items.add(new MerchantItem(i, MerchantItem.MerchantItemType.SellingItem, itemId, name, itemValue,
                                       bundleSize));
        }

        final int entriesBuyPrimary = reader.readUByte();
        for (int i = 0; i < entriesBuyPrimary; i++) {
            final ItemId itemId = new ItemId(reader);
            final String name = reader.readString();
            final long itemValue = reader.readUInt();

            items.add(new MerchantItem(i, MerchantItem.MerchantItemType.BuyingPrimaryItem, itemId, name, itemValue));
        }

        final int entriesBuySecondary = reader.readUByte();
        for (int i = 0; i < entriesBuySecondary; i++) {
            final ItemId itemId = new ItemId(reader);
            final String name = reader.readString();
            final long itemValue = reader.readUInt();

            items.add(new MerchantItem(i, MerchantItem.MerchantItemType.BuyingSecondaryItem, itemId, name, itemValue));
        }

        dialogId = reader.readInt();
    }

    @Override
    public boolean executeUpdate() {
        if (items == null) {
            throw new IllegalStateException("Can't execute update before it was decoded.");
        }

        final MerchantItem[] itemArray = new MerchantItem[items.size()];
        EventBus.publish(new DialogMerchantReceivedEvent(dialogId, title, items.toArray(itemArray)));

        return true;
    }

    @Nonnull
    @Override
    public String toString() {
        final TextBuilder builder = new TextBuilder();
        builder.append("title: \"").append(title).append("\", ");
        builder.append("items: \"").append(items.size()).append("\", ");
        builder.append("dialog ID: ").append(dialogId);

        return toString(builder.toString());
    }
}
