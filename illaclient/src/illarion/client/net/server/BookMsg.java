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
import illarion.client.net.server.events.ShowBookEvent;
import illarion.common.net.NetCommReader;
import org.bushe.swing.event.EventBus;

import java.io.IOException;

/**
 * Servermessage: Book ({@link CommandList#MSG_BOOK}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_BOOK)
public final class BookMsg extends AbstractReply {
    /**
     * The book id that was sent.
     */
    private int bookId;

    @Override
    public void decode(final NetCommReader reader) throws IOException {
        bookId = reader.readUShort();
    }

    @Override
    public boolean executeUpdate() {
        EventBus.publish(new ShowBookEvent(bookId));
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("book " + Integer.toString(bookId));
    }
}
