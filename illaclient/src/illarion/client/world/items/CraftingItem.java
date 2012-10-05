/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world.items;

import illarion.common.types.ItemId;

import java.util.Arrays;

/**
 * This class represents a single item that is transferred using the crafting dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CraftingItem {
    /**
     * The group index this crafting belongs to.
     */
    private final int group;

    /**
     * The ID of the item that is to be crafted.
     */
    private final ItemId itemId;

    /**
     * The name of the item.
     */
    private final String name;

    /**
     * The time in (1/10)s to craft the item.
     */
    private final int buildTime;

    /**
     * The amount of items created in one crafting step.
     */
    private final int buildStackSize;

    /**
     * The ingredients required for this item.
     */
    private final CraftingIngredientItem[] ingredients;

    /**
     * Constructor that applies all required values.
     *
     * @param group          the group this item belongs to
     * @param itemId         the ID of the item that is crafted
     * @param name           the name of the item that is crafted
     * @param buildTime      the time required to craft this item
     * @param buildStackSize the amount of items crafted at once
     * @param ingredients    the ingredients required to build this
     */
    public CraftingItem(final int group, final ItemId itemId, final String name, final int buildTime,
                        final int buildStackSize, final CraftingIngredientItem[] ingredients) {
        this.group = group;
        this.itemId = itemId;
        this.name = name;
        this.buildTime = buildTime;
        this.buildStackSize = buildStackSize;
        this.ingredients = Arrays.copyOf(ingredients, ingredients.length);
    }

    /**
     * The group index this item is assigned to.
     *
     * @return the crafting item
     */
    public int getGroup() {
        return group;
    }

    /**
     * Get the ID of the item.
     *
     * @return the ID of the item
     */
    public ItemId getItemId() {
        return itemId;
    }

    /**
     * Get the name of the item that is crafted.
     *
     * @return the crafted item
     */
    public String getName() {
        return name;
    }

    /**
     * Get the time in (1/10)s required to build the item.
     *
     * @return the build time of the item
     */
    public int getBuildTime() {
        return buildTime;
    }

    /**
     * Get size of the stack that is created at once.
     *
     * @return the size of the stack that is build
     */
    public int getBuildStackSize() {
        return buildStackSize;
    }

    /**
     * The amount of ingredients required for the count.
     *
     * @return the amount of ingredients
     */
    public int getIngredientCount() {
        return ingredients.length;
    }

    /**
     * Get a specified ingredient.
     *
     * @param index the index of the ingredient
     * @return the ingredient item assigned to this index
     */
    public CraftingIngredientItem getIngredient(final int index) {
        return ingredients[index];
    }
}
