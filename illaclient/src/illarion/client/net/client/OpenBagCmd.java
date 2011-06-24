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
 * Client Command: Open the bag the character carries in the bag slot (
 * {@link illarion.client.net.CommandList#CMD_OPEN_BAG}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class OpenBagCmd extends AbstractCommand {
    /**
     * The showcase the container shall be opened in.
     */
    private byte sc;

    /**
     * Default constructor for the open bag command.
     */
    public OpenBagCmd() {
        super(CommandList.CMD_OPEN_BAG);
    }

    /**
     * Create a duplicate of this open bag command.
     * 
     * @return new instance of this command
     */
    @Override
    public OpenBagCmd clone() {
        return new OpenBagCmd();
    }

    /**
     * Encode the data of this open bag command and put the values into the
     * buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(sc);
    }

    /**
     * Set the showcase the bag shall be opend in.
     * 
     * @param targetSc the showcase that shall show the bag contents.
     */
    public void setShowcase(final int targetSc) {
        sc = (byte) targetSc;
    }

    /**
     * Get the data of this open bag command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("in Showcase: " + sc);
    }
}
