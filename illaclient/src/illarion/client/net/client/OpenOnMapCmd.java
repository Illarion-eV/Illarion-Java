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
import illarion.client.world.World;
import illarion.common.net.NetCommWriter;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Client Command: Open a container on the map ({@link CommandList#CMD_OPEN_MAP}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class OpenOnMapCmd extends AbstractCommand {
    /**
     * The direction relative to the player character the bag is located at.
     */
    private final short direction;

    /**
     * Default constructor for the open container on the map command.
     *
     * @param mapLocation the location on the map where the container is supposed to be opened
     */
    public OpenOnMapCmd(@Nonnull final Location mapLocation) {
        super(CommandList.CMD_OPEN_MAP);
        direction = (short) World.getPlayer().getLocation().getDirection(mapLocation);
    }

    /**
     * Encode the data of this open container on map command and put the values into the buffer.
     *
     * @param writer the interface that allows writing data to the network communication system
     */
    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeUByte(direction);
    }

    /**
     * Get the data of this open container on map command as string.
     *
     * @return the data of this command as string
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("from direction:" + direction);
    }
}
