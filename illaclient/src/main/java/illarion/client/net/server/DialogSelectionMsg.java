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
import illarion.client.net.server.events.DialogSelectionReceivedEvent;
import illarion.client.world.items.SelectionItem;
import illarion.common.net.NetCommReader;
import javolution.text.TextBuilder;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * This server message is used to make the client show a selection dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_DIALOG_SELECTION)
public final class DialogSelectionMsg extends AbstractGuiMsg {
    /**
     * The title of the dialog window.
     */
    private String title;

    /**
     * The message that is displayed inside this selection dialog.
     */
    private String message;

    /**
     * The ID of the dialog that needs to be returned in order to inform the server that the window was closed.
     */
    private int dialogId;

    /**
     * The items read from the dialog.
     */
    private SelectionItem[] items;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        title = reader.readString();
        message = reader.readString();
        int itemCount = reader.readByte();
        items = new SelectionItem[itemCount];
        for (int i = 0; i < itemCount; i++) {
            int id = reader.readUShort();
            String name = reader.readString();
            items[i] = new SelectionItem(i, id, name);
        }
        dialogId = reader.readInt();
    }

    @Override
    public void executeUpdate() {
        EventBus.publish(new DialogSelectionReceivedEvent(dialogId, title, message, items));
    }

    @Nonnull
    @Override
    public String toString() {
        TextBuilder builder = new TextBuilder();
        builder.append("title: \"").append(title).append("\", ");
        builder.append("items: \"").append(items.length).append("\", ");
        builder.append("dialog ID: ").append(dialogId);
        return toString(builder.toString());
    }
}
