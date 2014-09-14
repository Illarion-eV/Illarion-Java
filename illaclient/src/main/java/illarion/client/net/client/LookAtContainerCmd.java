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
 * Client Command: Looking at a container slot ({@link CommandList#CMD_LOOKAT_CONTAINER}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class LookAtContainerCmd extends AbstractCommand {
    /**
     * The container we are going to look at.
     */
    private final short containerId;

    /**
     * The slot within the container we are going to look at.
     */
    private final short slot;

    /**
     * Default constructor for the look at container command.
     *
     * @param containerId the ID of the container
     * @param slot the ID of the slot in the container
     */
    public LookAtContainerCmd(int containerId, int slot) {
        super(CommandList.CMD_LOOKAT_CONTAINER);
        this.containerId = (short) containerId;
        this.slot = (short) slot;
    }

    @Override
    public void encode(@Nonnull NetCommWriter writer) {
        writer.writeUByte(containerId);
        writer.writeUByte(slot);
    }

    /**
     * Get the data of this look at showcase command as string.
     *
     * @return the data of this command as string
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Container: " + containerId + " Slot: " + slot);
    }
}
