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
 * Client Command: Looking at a inventory slot (
 * {@link illarion.client.net.CommandList#CMD_LOOKAT_INV}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class LookatInvCmd extends AbstractCommand {
    /**
     * The inventory slot we are looking at.
     */
    private byte slot;

    /**
     * Default constructor for the look at inventory command.
     */
    public LookatInvCmd() {
        super(CommandList.CMD_LOOKAT_INV);
    }

    /**
     * Create a duplicate of this look at inventory command.
     * 
     * @return new instance of this command
     */
    @Override
    public LookatInvCmd clone() {
        return new LookatInvCmd();
    }

    /**
     * Encode the data of this look at inventory command and put the values into
     * the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte(slot);
    }

    /**
     * Set the target inventory slot we want to look at.
     * 
     * @param lookAtSlot the slot we want to look at
     */
    public void setSlot(final int lookAtSlot) {
        slot = (byte) lookAtSlot;
    }

    /**
     * Get the data of this look at inventory command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Slot: " + slot);
    }
}
