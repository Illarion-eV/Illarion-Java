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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This event is fired in case the player looks at a item in the crafting dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ThreadSafe
@Immutable
public final class DialogCraftingLookAtItemEvent extends DialogEvent {
    /**
     * The item the player is looking at.
     */
    @Nonnull
    private final CraftingItemEntry item;

    /**
     * Create a new instance of this event.
     *
     * @param id the ID of the dialog
     * @param item the item the player is looking at
     */
    public DialogCraftingLookAtItemEvent(final int id, @Nonnull final CraftingItemEntry item) {
        super(id);
        this.item = item;
    }

    /**
     * Get the item the player is looking at.
     *
     * @return the item
     */
    @Nonnull
    public CraftingItemEntry getItem() {
        return item;
    }
}
