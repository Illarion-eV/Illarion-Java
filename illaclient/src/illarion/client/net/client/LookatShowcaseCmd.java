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
 * Client Command: Looking at a showcase slot (
 * {@link illarion.client.net.CommandList#CMD_LOOKAT_SHOWCASE}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class LookatShowcaseCmd extends AbstractCommand {
    /**
     * The showcase we are going to look at.
     */
    private byte sc;

    /**
     * The slot within the showcase we are going to look at.
     */
    private byte slot;

    /**
     * Default constructor for the look at showcase command.
     */
    public LookatShowcaseCmd() {
        super(CommandList.CMD_LOOKAT_SHOWCASE);
    }

    /**
     * Create a duplicate of this look at showcase command.
     * 
     * @return new instance of this command
     */
    @Override
    public LookatShowcaseCmd clone() {
        return new LookatShowcaseCmd();
    }

    /**
     * Encode the data of this look at showcase command and put the values into
     * the buffer.
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
     * Set the target inventory slot we want to look at.
     * 
     * @param lookAtSc the showcase we want to look at
     * @param lookAtSlot the slot within the showcase we want to look at
     */
    public void setSlot(final int lookAtSc, final int lookAtSlot) {
        sc = (byte) lookAtSc;
        slot = (byte) lookAtSlot;
    }

    /**
     * Get the data of this look at showcase command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Showcase: " + sc + " Slot: " + slot);
    }
}
