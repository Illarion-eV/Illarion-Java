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

/**
 * This event is fired in case the player looks at a ingredient in the crafting dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogCraftingLookAtIngredientItemEvent extends DialogEvent {
    /**
     * The item that is parent to the ingredient.
     */
    private final CraftingItemEntry item;

    /**
     * The index of the ingredient the player is looking at.
     */
    private final int ingredientIndex;

    /**
     * Create a new instance of this event.
     *
     * @param id              the ID of the dialog
     * @param item            the item the player is looking at
     * @param ingredientIndex the index of the ingredient the player is looking at
     */
    public DialogCraftingLookAtIngredientItemEvent(final int id, final CraftingItemEntry item, final int ingredientIndex) {
        super(id);
        this.item = item;
        this.ingredientIndex = ingredientIndex;
    }

    /**
     * Get the item the player is looking at.
     *
     * @return the item
     */
    public CraftingItemEntry getItem() {
        return item;
    }

    /**
     * Get the index of the ingredient the player is looking at.
     *
     * @return the ingredient index
     */
    public int getIngredientIndex() {
        return ingredientIndex;
    }
}
