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
 * Servermessage: Magic flags of the player character (
 * {@link illarion.client.net.CommandList#MSG_MAGIC_FLAG}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class MagicFlagMsg extends AbstractReply {
    /**
     * Flags of the magic that are available. So the runes a character is
     * allowed to use.
     */
    private long flags;

    /**
     * Type of magic that is used. Such a magician, druid, bard, priest.
     */
    private short type;

    /**
     * Default constructor for the magic flags message.
     */
    public MagicFlagMsg() {
        super(CommandList.MSG_MAGIC_FLAG);
    }

    /**
     * Create a new instance of the magic flags message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public MagicFlagMsg clone() {
        return new MagicFlagMsg();
    }

    /**
     * Decode the magic flags data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        type = reader.readUByte();
        flags = reader.readUInt();
    }

    /**
     * Execute the magic flags message and send the decoded data to the rest of
     * the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        // Gui.getInstance().getSpellBook().updateMagic(type, flags);
        return true;
    }

    /**
     * Get the data of this magic flag message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Type: " + type + " Flags: " + flags);
    }
}
