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

/**
 * Servermessage: Close a container (
 * {@link illarion.client.net.CommandList#MSG_CLOSE_SHOWCASE}).
 * 
 * @author Blay09
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class CloseShowcaseMsg extends AbstractReply {
    /**
     * The container that shall be closed.
     */
    private short sc;

    /**
     * Default constructor for the close container message.
     */
    public CloseShowcaseMsg() {
        super(CommandList.MSG_CLOSE_SHOWCASE);
    }

    /**
     * Create a new instance of the close container message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public CloseShowcaseMsg clone() {
        return new CloseShowcaseMsg();
    }

    /**
     * Decode the close container data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        sc = reader.readUByte();
    }

    /**
     * Execute the close container message and send the decoded data to the rest
     * of the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        GUI.getInstance().getContainer(sc).closeContainer();
        return true;
    }

    /**
     * Get the data of this close container message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Showcase: " + sc);
    }
}
