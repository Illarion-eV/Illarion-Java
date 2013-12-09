/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Client Command: Looking at a inventory slot ({@link CommandList#CMD_LOOKAT_INV}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class LookatInvCmd extends AbstractCommand {
    /**
     * The inventory slot we are looking at.
     */
    private final short slot;

    /**
     * Default constructor for the look at inventory command.
     *
     * @param slot the inventory slot to look at
     */
    public LookatInvCmd(final int slot) {
        super(CommandList.CMD_LOOKAT_INV);

        this.slot = (short) slot;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeUByte(slot);
    }

    /**
     * Get the data of this look at inventory command as string.
     *
     * @return the data of this command as string
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Slot: " + slot);
    }
}
