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
import illarion.client.net.server.events.DialogCraftingUpdateAbortedReceivedEvent;
import illarion.client.net.server.events.DialogCraftingUpdateCompletedReceivedEvent;
import illarion.client.net.server.events.DialogCraftingUpdateStartReceivedEvent;
import illarion.common.net.NetCommReader;
import javolution.text.TextBuilder;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Crafting dialog update ({@link CommandList#MSG_DIALOG_CRAFTING_UPDATE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_DIALOG_CRAFTING_UPDATE)
public final class DialogCraftingUpdateMsg
        extends AbstractReply {
    /**
     * This is the value of {@link #type} in case the update means that the crafting operation was started.
     */
    private static final int START = 0;

    /**
     * This is the value of {@link #type} in case the update means that the crafting operation is completed.
     */
    private static final int COMPLETE = 1;

    /**
     * This is the value of {@link #type} in case the update means that the crafting operation was aported.
     */
    private static final int ABORTED = 2;

    /**
     * The update type.
     */
    private int type;

    /**
     * The time in 1s/10 required to complete the task.
     */
    private int requiredTime;

    /**
     * The ID of this dialog
     */
    private int requestId;

    /**
     * The amount of remaining items that still need to be produced.
     */
    private int remaining;

    /**
     * Decode the text request the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws java.io.IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader)
            throws IOException {
        type = reader.readUByte();
        if (type == START) {
            remaining = reader.readUByte();
            requiredTime = reader.readUShort();
        }
        requestId = reader.readInt();
    }

    /**
     * Execute the text request message and send the decoded data to the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        switch (type) {
            case START:
                EventBus.publish(new DialogCraftingUpdateStartReceivedEvent(requestId, remaining, requiredTime));
                break;
            case COMPLETE:
                EventBus.publish(new DialogCraftingUpdateCompletedReceivedEvent(requestId));
                break;
            case ABORTED:
                EventBus.publish(new DialogCraftingUpdateAbortedReceivedEvent(requestId));
                break;
        }

        return true;
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
        final TextBuilder builder = TextBuilder.newInstance();
        try {
            switch (type) {
                case START:
                    builder.append("START");
                    builder.append(", required time: ");
                    builder.append((float) requiredTime / 10.f);
                    builder.append("s");
                    break;
                case COMPLETE:
                    builder.append("COMPLETED");
                    break;
                case ABORTED:
                    builder.append("ABORTED");
                    break;
                default:
                    builder.append("UNKNOWN");
            }
            builder.append(", id: ").append(requestId);
            return toString(builder.toString());
        } finally {
            TextBuilder.recycle(builder);
        }
    }
}
