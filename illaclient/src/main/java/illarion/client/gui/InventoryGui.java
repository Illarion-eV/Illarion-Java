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
package illarion.client.gui;

import illarion.client.world.items.Inventory;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This interface is used to control inventory display in the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface InventoryGui {
    /**
     * Hide the inventory.
     */
    void hideInventory();

    /**
     * Check if the inventory is visible.
     *
     * @return {@code true} in case the inventory is visible
     */
    boolean isVisible();

    /**
     * Set the convent of a inventory slot.
     *
     * @param slotId the ID of the slot to set
     * @param itemId the ID of the item in this slot, in case this is {@code null} the item ID is assumed to be
     * {@code 0}
     * @param count the amount of items in this slot, {@code null} or {@code 0} is only allowed in case the item ID
     * is {@code null} or {@code 0} as well
     * @throws IllegalArgumentException in case the slot ID is less then {@code 0} or larger or equal then
     * {@link Inventory#SLOT_COUNT}
     */
    void setItemSlot(int slotId, @Nullable ItemId itemId, @Nullable ItemCount count);

    /**
     * Show the inventory.
     */
    void showInventory();

    /**
     * Show a tooltip for a specified slot on the inventory. This tooltip will only become visible in case the mouse
     * cursor is inside the interactive area of the slot. It will be displayed as long as the mouse cursor stays
     * inside this area.
     *
     * @param slotId the ID of the slot
     * @param tooltip the tooltip
     */
    void showTooltip(int slotId, @Nonnull Tooltip tooltip);

    /**
     * Hide the inventory in case its shown and show it in case its hidden.
     */
    void toggleInventory();

    void updateCarryLoad();
}
