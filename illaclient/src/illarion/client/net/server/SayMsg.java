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

import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.util.ChatHandler;

import illarion.common.util.Location;

/**
 * Servermessage: Talking ( {@link illarion.client.net.CommandList#MSG_SAY}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class SayMsg extends AbstractReply {
    /**
     * Constant of a header that marks that a text should be shown only in the
     * editor as temporary message.
     */
    @SuppressWarnings("nls")
    private static final String EDITOR_WHISPER_HEADER = "#w ";

    /**
     * The string length of {@link #EDITOR_WHISPER_HEADER}.
     */
    private static final int EDITOR_WHISPER_HEADER_LENGHT =
        EDITOR_WHISPER_HEADER.length();

    /**
     * The location the text was spoken at.
     */
    private transient Location loc;

    /**
     * The text that was actually spoken.
     */
    private String text;

    /**
     * Default constructor for the talking message.
     */
    public SayMsg() {
        super(CommandList.MSG_SAY);
    }

    /**
     * Create a new instance of the talking message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public SayMsg clone() {
        return new SayMsg();
    }

    /**
     * Decode the talking data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        loc = decodeLocation(reader);
        text = reader.readString();
    }

    /**
     * Execute the talking message and send the decoded data to the rest of the
     * client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        ChatHandler.getInstance().handleMessage(text, loc);
        return true;
    }

    /**
     * Clean the command up before recycling it.
     */
    @Override
    public void reset() {
        text = null;
        if (loc != null) {
            loc.recycle();
            loc = null;
        }
    }

    /**
     * Get the data of this talking message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("at ");
        builder.append(loc.toString());
        builder.append(" \"");
        builder.append(text);
        builder.append("\"");
        return toString(builder.toString());
    }
}
