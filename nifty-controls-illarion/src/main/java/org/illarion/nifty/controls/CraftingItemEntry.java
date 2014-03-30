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

import de.lessvoid.nifty.render.NiftyImage;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * This interface defines defines a single item that can be crafted and is displayed in the crafting dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public interface CraftingItemEntry extends CraftingTreeItem {
    /**
     * Get the index of this item.
     *
     * @return the index of the item
     */
    int getItemIndex();

    /**
     * Get the crafting time in seconds.
     *
     * @return the crafting time in seconds
     */
    double getCraftTime();

    /**
     * Get the image of this item.
     *
     * @return the image of this item
     */
    @Nonnull
    NiftyImage getImage();

    /**
     * Get the amount of ingredients of the same type required.
     *
     * @param index the index of the ingredient
     * @return the amount of items required of this ingredient
     * @throws IndexOutOfBoundsException in case {@code index} is less then 0 or equal or greater then the result of
     * {@link #getIngredientCount()}
     */
    @Nonnull
    ItemCount getIngredientAmount(int index);

    /**
     * Get the amount of ingredients that are required for this item.
     *
     * @return the amount of ingredients
     */
    int getIngredientCount();

    /**
     * Get the image of the specified ingredient.
     *
     * @param index the index of the ingredient
     * @return the image of this ingredient
     * @throws IndexOutOfBoundsException in case {@code index} is less then 0 or equal or greater then the result of
     * {@link #getIngredientCount()}
     */
    @Nonnull
    NiftyImage getIngredientImage(int index);

    /**
     * Get the item ID of a specific ingredient.
     *
     * @param index the index of the ingredient
     * @return the item ID of the ingredient with that index
     * @throws IndexOutOfBoundsException in case {@code index} is less then 0 or equal or greater then the result of
     * {@link #getIngredientCount()}
     */
    @Nonnull
    ItemId getIngredientItemId(int index);

    /**
     * Get the name of this item.
     *
     * @return the name of the item
     */
    @Nonnull
    String getName();

    /**
     * Get size of the stack that is created at once.
     *
     * @return the size of the stack that is build
     */
    @Nonnull
    ItemCount getBuildStackSize();
}
