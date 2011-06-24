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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;

/**
 * Client Command: Send a spoken text or a emote or a text command (
 * {@link illarion.client.net.CommandList#CMD_SAY}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class SayCmd extends AbstractCommand {
    /**
     * The text that is send to the server. This can be text spoken by the
     * player character, also a emote of the character or a text based command
     * like the GM-commands.
     */
    private String text;

    /**
     * Default constructor for the say text command.
     */
    public SayCmd() {
        super(CommandList.CMD_SAY);
    }

    /**
     * Create a duplicate of this say command.
     * 
     * @return new instance of this command
     */
    @Override
    public SayCmd clone() {
        return new SayCmd();
    }

    /**
     * Encode the data of this say command and put the values into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeString(text);
    }

    /**
     * Clean up the command before put it back into the recycler for later
     * reuse.
     */
    @Override
    public void reset() {
        text = null;
    }

    /**
     * Set the text that shall be send to the server.
     * 
     * @param sayText the text that is send
     */
    public void setText(final String sayText) {
        text = sayText;
    }

    /**
     * Get the data of this say command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Text: " + text);
    }
}
