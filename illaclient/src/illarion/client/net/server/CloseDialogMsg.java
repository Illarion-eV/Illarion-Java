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
import illarion.client.world.events.CloseDialogEvent;
import illarion.common.net.NetCommReader;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Close a dialog ({@link CommandList#MSG_CLOSE_SHOWCASE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_CLOSE_DIALOG)
public final class CloseDialogMsg extends AbstractReply {
    /**
     * The ID of the dialog that is supposed to be closed.
     */
    private int dialogId;

    /**
     * Decode the close dialog data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs  to be decoded
     * @throws IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
        dialogId = reader.readInt();
    }

    /**
     * Execute the close dialog message and send the decoded data to the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        EventBus.publish(new CloseDialogEvent(dialogId, CloseDialogEvent.DialogType.Any));
        return true;
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Dialog ID: " + dialogId);
    }
}
