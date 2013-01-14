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
 * This command is used to tell the server that the player is using a item in a item container.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class UseContainerCmd extends AbstractCommand {
    /**
     * The ID of the container that contains the used slot.
     */
    private final short containerId;

    /**
     * The slot that is used.
     */
    private final short slot;

    /**
     * Default constructor for the use command.
     *
     * @param container the ID of the container that is used
     * @param slot      the ID of the container slot that is used
     */
    public UseContainerCmd(final int container, final int slot) {
        super(CommandList.CMD_USE);

        containerId = (short) container;
        this.slot = (short) slot;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeUByte((short) 2); // CONTAINER REFERENCE
        writer.writeUByte(containerId);
        writer.writeUByte(slot);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Container: " + containerId + " Slot: " + slot);
    }
}
