/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls;

import de.lessvoid.nifty.controls.Window;
import de.lessvoid.nifty.elements.Element;

/**
 * This interface is used to interact with a merchant dialog that is displayed inside the GUI.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public interface DialogCrafting extends Window {
    /**
     * This enumerator identifies the buttons displayed on this dialog.
     */
    enum DialogButtons {
        /**
         * The button that reads craft.
         */
        craft,

        /**
         * The button that reads close.
         */
        close;
    }

    /**
     * Add a category along with its items to the list.
     *
     * @param entries the categories to add
     */
    void addCraftingItems(CraftingCategoryEntry... entries);

    /**
     * Get the Nifty element that displays the crafting item.
     *
     * @return the Nifty element that is displaying the selected item
     */
    Element getCraftingItemDisplay();

    /**
     * Get the nifty element that takes care or displaying the specified ingredient.
     *
     * @param index the index of the ingredient to show
     * @return the element of the ingredient
     * @throws IndexOutOfBoundsException in case {@code index} is less then 0 or larger or equal to the amount of
     *                                   ingredients of the selected item
     */
    Element getIngredientItemDisplay(int index);

    /**
     * Get the item that was selected in this dialog.
     *
     * @return the item that is selected
     */
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
     * @param progress the new value for the progress
     */
    void setProgress(float progress);

    /**
     * This function triggers the automatic progress display. It moves the progress bar from 0% to 100% within the
     * time specified.
     *
     * @param seconds the time in seconds to fill the progress bar
     */
    void startProgress(double seconds);
}
