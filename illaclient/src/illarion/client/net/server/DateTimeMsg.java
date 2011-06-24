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

/**
 * Servermessage: Current date and time (
 * {@link illarion.client.net.CommandList#MSG_DATETIME}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.95
 * @version 1.22
 */
public final class DateTimeMsg extends AbstractReply {
    /**
     * The format of the string used in the {@link #toString()} function.
     */
    @SuppressWarnings("nls")
    private static final String TO_STRING_FORMAT =
        "Date: %1$s/%2$s/%3$s - Time: %4$s:%5$s";

    /**
     * Day of the current IG time.
     */
    private short day;

    /**
     * Hour of the current IG time.
     */
    private short hour;

    /**
     * Minute of the current IG time.
     */
    private short minute;

    /**
     * Month of the current IG time.
     */
    private short month;

    /**
     * Year of the current IG time.
     */
    private int year;

    /**
     * Default constructor for the date and time message.
     */
    public DateTimeMsg() {
        super(CommandList.MSG_DATETIME);
    }

    /**
     * Create a new instance of the date and time message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public DateTimeMsg clone() {
        return new DateTimeMsg();
    }

    /**
     * Decode the date and time data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        hour = reader.readUByte();
        minute = reader.readUByte();
        day = reader.readUByte();
        month = reader.readUByte();
        year = reader.readUShort();
    }

    /**
     * Execute the date and time message and send the decoded data to the rest
     * of the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        // final Clock clock = Gui.getInstance().getClock();
        // clock.setDate(day, month, year);
        // clock.setTime(hour, minute);

        return true;
    }

    /**
     * Get the data of this date and time message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @Override
    public String toString() {
        return toString(String.format(TO_STRING_FORMAT,
            Integer.toString(month), Integer.toString(day),
            Integer.toString(year), Integer.toString(hour),
            Integer.toString(minute)));
    }
}
