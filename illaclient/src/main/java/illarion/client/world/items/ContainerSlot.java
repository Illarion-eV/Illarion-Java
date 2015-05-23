/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.client.world.items;

import illarion.client.world.interactive.InteractiveContainerSlot;

import javax.annotation.Nonnull;

/**
 * This class represents a single slot in a container that is able to store one item that is located in the container.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ContainerSlot extends AbstractItemSlot {
    /**
     * The location of the item within the container.
     */
    private final int location;

    /**
     * The ID of the parent container.
     */
    private final int containerId;

    /**
     * The interactive reference to this container slot.
     */
    @Nonnull
    private final InteractiveContainerSlot interactive;

    /**
     * Create a container slot for a specified location in the container.
     *
     * @param conId the ID of the parent container
     * @param loc the location of this slot in this container.
     */
    public ContainerSlot(int conId, int loc) {
        location = loc;
        containerId = conId;
        interactive = new InteractiveContainerSlot(this);
    }

    /**
     * Get the location of this slot in the container.
     *
     * @return the location of the slot
     */
    public int getLocation() {
        return location;
    }

    /**
     * Get the ID of the parent container.
     *
     * @return the ID of the parent container
     */
    public int getContainerId() {
        return containerId;
    }

    /**
     * Get the interactive reference to this container slot.
     *
     * @return the interactive reference
     */
    @Nonnull
    public InteractiveContainerSlot getInteractive() {
        return interactive;
    }
}
