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
 * Client Command: Request the appearance data of a unknown character (
 * {@link illarion.client.net.CommandList#CMD_REQUEST_APPEARANCE}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class RequestAppearanceCmd extends AbstractCommand {
    /**
     * The ID of the characters who's appearance is needed.
     */
    private long charId;

    /**
     * Default constructor for the request appearance command.
     */
    public RequestAppearanceCmd() {
        super(CommandList.CMD_REQUEST_APPEARANCE);
    }

    /**
     * Create a duplicate of this request appearance command.
     * 
     * @return new instance of this command
     */
    @Override
    public RequestAppearanceCmd clone() {
        return new RequestAppearanceCmd();
    }

    /**
     * Encode the data of this request appearance command and put the values
     * into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeUInt(charId);
    }

    /**
     * Set the ID of the characters who's appearance data is needed.
     * 
     * @param requestCharId the ID of the character who's appearance is needed
     */
    public void request(final long requestCharId) {
        charId = requestCharId;
    }

    /**
     * Get the data of this request appearance command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("ID: " + charId);
    }
}
