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
import illarion.client.net.server.events.BroadcastInformReceivedEvent;
import illarion.client.net.server.events.ScriptInformReceivedEvent;
import illarion.client.net.server.events.ServerInformReceivedEvent;
import illarion.client.net.server.events.TextToInformReceivedEvent;
import illarion.common.net.NetCommReader;
import javolution.text.TextBuilder;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;

import java.io.IOException;

/**
 * The inform message that is used to receive inform messages from the server.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
@ReplyMessage(replyId = CommandList.MSG_INFORM)
public final class InformMsg extends AbstractReply {
    /**
     * The logger that is used for the log output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(InformMsg.class);

    /**
     * The type of the inform.
     */
    private int informType;

    /**
     * The text of the inform.
     */
    private String informText;

    @Override
    public void decode(final NetCommReader reader) throws IOException {
        informType = reader.readUByte();
        informText = reader.readString();
    }

    @Override
    public boolean executeUpdate() {
        switch (informType) {
            case 0:
                EventBus.publish(new ServerInformReceivedEvent(informText));
                break;
            case 1:
                EventBus.publish(new BroadcastInformReceivedEvent(informText));
                break;
            case 2:
                EventBus.publish(new TextToInformReceivedEvent(informText));
                break;
            case 100:
            case 101:
            case 102:
                EventBus.publish(new ScriptInformReceivedEvent(informType - 100, informText));
                break;

            default:
                TextBuilder builder = null;
                try {
                    builder = TextBuilder.newInstance();
                    builder.append("Received inform with unknown type: ").append(informType);
                    builder.append("(").append(informText).append(")");
                    LOGGER.warn(builder.toString());
                } finally {
                    if (builder != null) {
                        TextBuilder.recycle(builder);
                    }
                }
        }
        return true;
    }

    @Override
    public String toString() {
        final TextBuilder builder = TextBuilder.newInstance();
        try {
            builder.append("Type: ").append(informType);
            builder.append(" Text: ").append(informText);
            return toString(builder.toString());
        } finally {
            TextBuilder.recycle(builder);
        }
    }
}
