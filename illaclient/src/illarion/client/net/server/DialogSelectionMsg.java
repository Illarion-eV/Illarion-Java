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
import illarion.client.net.server.events.DialogMessageReceivedEvent;
import illarion.client.net.server.events.DialogSelectionReceivedEvent;
import illarion.client.world.items.SelectionItem;
import javolution.text.TextBuilder;
import org.bushe.swing.event.EventBus;

import java.io.IOException;
import java.util.List;

/**
 * This server message is used to make the client show a selection dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogSelectionMsg
        extends AbstractReply {
    /**
     * The title of the dialog window.
     */
    private String title;

    /**
     * The ID of the dialog that needs to be returned in order to inform the server that the window was closed.
     */
    private int dialogId;

    /**
     * The items read from the dialog.
     */
    private SelectionItem[] items;

    /**
     * Default constructor for the message dialog message.
     */
    public DialogSelectionMsg() {
        super(CommandList.MSG_DIALOG_SELECTION);
    }

    @Override
    public AbstractReply clone() {
        return new DialogSelectionMsg();
    }

    @Override
    public void decode(final NetCommReader reader)
            throws IOException {
        title = reader.readString();
        final int itemCount = reader.readByte();
        items = new SelectionItem[itemCount];
        for (int i = 0; i < itemCount; i++) {
            final int id = reader.readUShort();
            final String name = reader.readString();
            items[i] = new SelectionItem(id, name);
        }
        dialogId = reader.readInt();
    }

    @Override
    public boolean executeUpdate() {
        EventBus.publish(new DialogSelectionReceivedEvent(dialogId, title, items));

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
