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
import javolution.text.TextBuilder;
import org.bushe.swing.event.EventBus;

import java.io.IOException;

/**
 * This server message is used to make the client showing a message dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogMessageMsg
        extends AbstractReply {
    /**
     * The title of the dialog window.
     */
    private String title;

    /**
     * The content of the dialog.
     */
    private String content;

    /**
     * The ID of the dialog that needs to be returned in order to inform the server that the window was closed.
     */
    private int dialogId;

    /**
     * Default constructor for the message dialog message.
     */
    public DialogMessageMsg() {
        super(CommandList.MSG_DIALOG_MSG);
    }

    @Override
    public AbstractReply clone() {
        return new DialogMessageMsg();
    }

    @Override
    public void decode(final NetCommReader reader)
            throws IOException {
        title = reader.readString();
        content = reader.readString();
        dialogId = reader.readInt();
    }

    @Override
    public boolean executeUpdate() {
        EventBus.publish(new DialogMessageReceivedEvent(dialogId, title, content));

        return true;
    }

    @Override
    public void reset() {
        title = null;
    }

    @Override
    public String toString() {
        final TextBuilder builder = TextBuilder.newInstance();
        builder.append("title: \"").append(title).append("\", ");
        builder.append("message: \"").append(content).append("\", ");
        builder.append("dialog ID: ").append(dialogId);

        final String result = builder.toString();
        TextBuilder.recycle(builder);
        return toString(result);
    }
}
