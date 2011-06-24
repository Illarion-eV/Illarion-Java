/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.server;

import java.io.IOException;

import illarion.client.guiNG.GUI;
import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.util.BookFactory;

/**
 * Servermessage: Book ( {@link illarion.client.net.CommandList#MSG_BOOK}).
 * 
 * @author Blay09
 * @since 1.22
 * @version 1.22
 */
public final class BookMsg extends AbstractReply {

    /**
     * The book id that was sent.
     */
    private int bookid;

    /**
     * Default constructor for the book message.
     */
    public BookMsg() {
        super(CommandList.MSG_BOOK);
    }

    /**
     * Create a new instance of the book message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public BookMsg clone() {
        return new BookMsg();
    }

    /**
     * Decode the book data the receiver got and prepare it for the execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        bookid = reader.readUShort();
    }

    /**
     * Execute the book message and send the decoded data to the rest of the
     * client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        if (BookFactory.getInstance().loadBook(bookid)) {
            GUI.getInstance().getBookWindow().setVisible(true);
        }
        return true;
    }

    /**
     * Clean the command up before recycling it.
     */
    @Override
    public void reset() {
        bookid = 0;
    }

    /**
     * Get the data of this book message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("book ");
        builder.append(bookid);
        return toString(builder.toString());
    }
}
