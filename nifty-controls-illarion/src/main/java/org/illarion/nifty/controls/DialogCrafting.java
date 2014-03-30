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
import de.lessvoid.nifty.elements.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This interface is used to interact with a merchant dialog that is displayed inside the GUI.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
@NotThreadSafe
public interface DialogCrafting extends Window {
    /**
     * Add a category along with its items to the list.
     *
     * @param entries the categories to add
     */
    void addCraftingItems(@Nonnull CraftingCategoryEntry... entries);

    /**
     * Get the Nifty element that displays the crafting item.
     *
     * @return the Nifty element that is displaying the selected item
     */
    @Nonnull
    Element getCraftingItemDisplay();

    /**
     * Get the nifty element that takes care or displaying the specified ingredient.
     *
     * @param index the index of the ingredient to show
     * @return the element of the ingredient
     * @throws IndexOutOfBoundsException in case {@code index} is less then 0 or larger or equal to the amount of
     * ingredients of the selected item
     */
    @Nonnull
    Element getIngredientItemDisplay(int index);

    /**
     * Get the item that was selected in this dialog.
     *
     * @return the item that is selected
     */
    @Nullable
    CraftingItemEntry getSelectedCraftingItem();

    /**
     * Remove everything from the current item list.
     */
    void clearItemList();

    /**
     * Select a item by the item index of the entry.
     */
    void selectItemByItemIndex(int index);

    /**
     * Set the displayed state of the progress.
     *
     * @param progress the new value for the progress, will be clamped to {@code 0.f} to {@code 1.f}
     */
    void setProgress(float progress);

    /**
     * This function triggers the automatic progress display. It moves the progress bar from 0% to 100% within the
     * time specified.
     *
     * @param seconds the time in seconds to fill the progress bar
     * @throws IllegalArgumentException in case {@code seconds} is less then 0.0
     */
    void startProgress(double seconds);

    /**
     * Set the ID of this crafting dialog.
     *
     * @param id the id of the dialog
     */
    void setDialogId(int id);

    /**
     * Get the ID of the dialog.
     *
     * @return the ID of the dialog
     */
    int getDialogId();

    /**
     * Get the currently selected amount.
     *
     * @return the current amount
     */
    int getAmount();

    /**
     * Set the currently selected amount to a new value.
     *
     * @param amount the new amount value
     */
    void setAmount(int amount);
}
