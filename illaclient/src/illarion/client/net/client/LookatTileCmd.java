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
import illarion.common.annotation.NonNull;
import illarion.common.net.NetCommWriter;
import illarion.common.types.Location;
import net.jcip.annotations.Immutable;

/**
 * Client Command: Looking at a tile on the map ({@link CommandList#CMD_LOOKAT_TILE}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class LookatTileCmd extends AbstractCommand {
    /**
     * The position on the map we are going to look at.
     */
    @NonNull
    private final Location location;

    /**
     * Default constructor for the look at tile command.
     *
     * @param tileLocation the location of the tile to look at
     */
    public LookatTileCmd(@NonNull final Location tileLocation) {
        super(CommandList.CMD_LOOKAT_TILE);
        location = new Location(tileLocation);
    }

    @Override
    public void encode(@NonNull final NetCommWriter writer) {
        writer.writeLocation(location);
    }

    @NonNull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("position: " + location.toString());
    }
}
