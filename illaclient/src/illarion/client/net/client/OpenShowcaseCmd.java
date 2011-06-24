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
 * Client Command: Open a container within another container shown in a showcase
 * ({@link illarion.client.net.CommandList#CMD_OPEN_MAP}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class OpenShowcaseCmd extends AbstractCommand {
    /**
     * The showcase the container is located in and also the showcase that will
     * show up the opened container.
     */
    private byte sc;

    /**
     * The slot within the showcase that contains the container.
     */
    private byte slot;

    /**
     * Default constructor for the open container in showcase command.
     */
    public OpenShowcaseCmd() {
        super(CommandList.CMD_OPEN_SHOWCASE);
    }

    /**
     * Create a duplicate of this open container in showcase command.
     * 
     * @return new instance of this command
     */
    @Override
    public OpenShowcaseCmd clone() {
        return new OpenShowcaseCmd();
    }

    /**
     * Encode the data of this open container in showcase command and put the
     * values into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(sc);
        writer.writeByte(slot);
    }

    /**
     * Set the showcase and the showcase slot that contains the container.
     * 
     * @param openSc the showcase that contains the container that shall be
     *            opened and also the container that will contain the content of
     *            the container that is opened.
     * @param openSlot the slot in the showcase that contains the container that
     *            shall be opened
     */
    public void setShowcase(final int openSc, final int openSlot) {
        sc = (byte) openSc;
        slot = (byte) openSlot;
    }

    /**
     * Get the data of this open container in showcase command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Showcase:" + sc + " Slot: " + slot);
    }
}
