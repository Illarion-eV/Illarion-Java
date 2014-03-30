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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Client Command: Open a container within another container shown in a container ({@link
 * CommandList#CMD_OPEN_SHOWCASE}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class OpenInContainerCmd extends AbstractCommand {
    /**
     * The showcase the container is located in and also the showcase that will show up the opened container.
     */
    private final short containerId;

    /**
     * The slot within the showcase that contains the container.
     */
    private final short slot;

    /**
     * Default constructor for the open container in container command.
     *
     * @param containerId the ID of the container
     * @param slot the slot in the container where the item that is supposed to be opened is located
     */
    public OpenInContainerCmd(final int containerId, final int slot) {
        super(CommandList.CMD_OPEN_SHOWCASE);
        this.containerId = (short) containerId;
        this.slot = (short) slot;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeUByte(containerId);
        writer.writeUByte(slot);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Container:" + containerId + " Slot: " + slot);
    }
}
