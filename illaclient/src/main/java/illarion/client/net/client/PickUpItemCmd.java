/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * This command is used to tell the server that a item on a specified location on the map is picked up and added
 * anywhere to the inventory of the player.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class PickUpItemCmd extends AbstractCommand {
    /**
     * The location on the map where the item is fetched from.
     */
    @Nonnull
    private final Location pickUpLocation;

    /**
     * Default constructor for the pickup command.
     *
     * @param location the location the item is taken from
     */
    public PickUpItemCmd(@Nonnull final Location location) {
        super(CommandList.CMD_PICK_UP);

        pickUpLocation = new Location(location);
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeLocation(pickUpLocation);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(pickUpLocation.toString());
    }
}
