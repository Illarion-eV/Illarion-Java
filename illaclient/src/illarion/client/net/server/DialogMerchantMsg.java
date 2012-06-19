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
import illarion.client.net.NetCommReader;
import illarion.client.net.server.events.DialogMerchantReceivedEvent;
import illarion.client.world.items.MerchantItem;
import javolution.text.TextBuilder;
import org.bushe.swing.event.EventBus;

import java.io.IOException;

/**
 * This server message is used to make the client showing a merchant dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogMerchantMsg extends AbstractReply {
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
    private MerchantItem[] items;

    /**
     * Default constructor for the merchant dialog message.
     */
    public DialogMerchantMsg() {
        super(CommandList.MSG_DIALOG_MERCHANT);
    }

    @Override
    public AbstractReply clone() {
        return new DialogMerchantMsg();
    }

    @Override
    public void decode(final NetCommReader reader)
            throws IOException {
        title = reader.readString();

        final int entries = reader.readUByte();
        items = new MerchantItem[entries];
        for (int i = 0; i < entries; i++) {
            final int itemId = reader.readUShort();
            final String name = reader.readString();
            final long itemValue = reader.readUInt();

            items[i] = new MerchantItem(i, itemId, name, itemValue);
        }

        dialogId = reader.readInt();
    }

    @Override
    public boolean executeUpdate() {
        if (items == null) {
            throw new IllegalStateException("Can't execute update before it was decoded.");
        }
        EventBus.publish(new DialogMerchantReceivedEvent(dialogId, title, items));

        return true;
    }

    @Override
    public void reset() {
        title = null;
        items = null;
    }

    @Override
    public String toString() {
        final TextBuilder builder = TextBuilder.newInstance();
        builder.append("title: \"").append(title).append("\", ");
        builder.append("items: \"").append(items.length).append("\", ");
        builder.append("dialog ID: ").append(dialogId);

        final String result = builder.toString();
        TextBuilder.recycle(builder);
        return toString(result);
    }
}
