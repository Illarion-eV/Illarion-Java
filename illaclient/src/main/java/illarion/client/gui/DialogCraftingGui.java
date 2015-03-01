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
package illarion.client.gui;

import illarion.client.world.items.CraftingItem;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * This interface defines the access to the crafting dialog GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface DialogCraftingGui {
    /**
     * Show a tooltip for a crafting ingredient.
     *
     * @param dialogId the ID of the crafting dialog
     * @param index the index of the crafted item
     * @param ingredientIndex the index of the ingredient
     * @param tooltip the tooltip
     */
    void showCraftIngredientTooltip(int dialogId, int index, int ingredientIndex, @Nonnull Tooltip tooltip);

    /**
     * Show a tooltip for a crafting item.
     *
     * @param dialogId the ID of the crafting dialog
     * @param index the index of the crafted item
     * @param tooltip the tooltip
     */
    void showCraftItemTooltip(int dialogId, int index, @Nonnull Tooltip tooltip);

    /**
     * Show a new crafting dialog or update a existing one.
     *
     * @param dialogId the ID of the dialog
     * @param title    the new title of the dialog
     * @param groups   the names of the categories of the dialog
     * @param items    the items in the dialog
     */
    void showCraftingDialog(int dialogId, @Nonnull String title, @Nonnull Collection<String> groups,
                            @Nonnull Collection<CraftingItem> items);

    /**
     * Start the production indicator.
     *
     * @param dialogId              the ID of the dialog
     * @param remainingItemCount    the remaining items to produce
     * @param requiredTimeInSeconds the time in seconds required to produce the item
     */
    void startProductionIndicator(int dialogId, int remainingItemCount, double requiredTimeInSeconds);

    /**
     * Indicate that a production finished.
     *
     * @param dialogId the ID of the dialog
     */
    void finishProduction(int dialogId);

    /**
     * Indicate that the production was aborted.
     *
     * @param dialogId the ID of the dialog
     */
    void abortProduction(int dialogId);
}
