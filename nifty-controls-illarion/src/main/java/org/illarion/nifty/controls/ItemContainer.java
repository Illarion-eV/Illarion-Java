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
package org.illarion.nifty.controls;

import de.lessvoid.nifty.controls.Window;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This interface offers access to a container that stores items.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public interface ItemContainer extends Window {
    /**
     * Get the amount of item slots in this container.
     *
     * @return the amount of item slots in this container
     */
    int getSlotCount();

    /**
     * Get one of the inventory slots.
     *
     * @param index the index of the requested slot
     * @return the slot control assigned to this index
     * @throws IndexOutOfBoundsException in case index is &lt; 0 or &gt;= {@link #getSlotCount()}
     */
    @Nonnull
    InventorySlot getSlot(int index);
}
