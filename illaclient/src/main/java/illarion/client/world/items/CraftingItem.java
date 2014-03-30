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
package illarion.client.world.items;

import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;

/**
 * This class represents a single item that is transferred using the crafting dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public class CraftingItem {
    /**
     * The index of this item in the list.
     */
    private final int itemIndex;

    /**
     * The group index this crafting belongs to.
     */
    private final int group;

    /**
     * The ID of the item that is to be crafted.
     */
    @Nonnull
    private final ItemId itemId;

    /**
     * The name of the item.
     */
    @Nonnull
    private final String name;

    /**
     * The time in (1/10)s to craft the item.
     */
    private final int buildTime;

    /**
     * The amount of items created in one crafting step.
     */
    @Nonnull
    private final ItemCount buildStackSize;

    /**
     * The ingredients required for this item.
     */
    @Nonnull
    private final CraftingIngredientItem[] ingredients;

    /**
     * Copy constructor.
     *
     * @param org the crafting item to copy
     */
    public CraftingItem(@Nonnull final CraftingItem org) {
        itemIndex = org.itemIndex;
        group = org.group;
        itemId = org.itemId;
        name = org.name;
        buildTime = org.buildTime;
        buildStackSize = org.buildStackSize;
        ingredients = org.ingredients;
    }

    /**
     * Constructor that applies all required values.
     *
     * @param itemIndex the index of this item
     * @param group the group this item belongs to
     * @param itemId the ID of the item that is crafted
     * @param name the name of the item that is crafted
     * @param buildTime the time required to craft this item
     * @param buildStackSize the amount of items crafted at once
     * @param ingredients the ingredients required to build this
     */
    public CraftingItem(
            final int itemIndex,
            final int group,
            @Nonnull final ItemId itemId,
            @Nonnull final String name,
            final int buildTime,
            @Nonnull final ItemCount buildStackSize,
            @Nonnull final CraftingIngredientItem[] ingredients) {
        this.itemIndex = itemIndex;
        this.group = group;
        this.itemId = itemId;
        this.name = name;
        this.buildTime = buildTime;
        this.buildStackSize = buildStackSize;
        this.ingredients = Arrays.copyOf(ingredients, ingredients.length);
        for (final CraftingIngredientItem ingredient : ingredients) {
            if (ingredient == null) {
                throw new IllegalArgumentException("One of the ingredient was set to NULL!");
            }
        }
    }

    /**
     * Get size of the stack that is created at once.
     *
     * @return the size of the stack that is build
     */
    @Nonnull
    public ItemCount getBuildStackSize() {
        return buildStackSize;
    }

    /**
     * Get a specified ingredient.
     *
     * @param index the index of the ingredient
     * @return the ingredient item assigned to this index
     * @throws IndexOutOfBoundsException in case {@code index} is less then 0 or larger or equal to {@link
     * #getIngredientCount()}
     */
    @Nonnull
    public CraftingIngredientItem getIngredient(final int index) {
        return ingredients[index];
    }

    /**
     * Get the ID of the item.
     *
     * @return the ID of the item
     */
    @Nonnull
    public ItemId getItemId() {
        return itemId;
    }

    /**
     * Get the name of the item that is crafted.
     *
     * @return the crafted item
     */
    @Nonnull
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
     * The group index this item is assigned to.
     *
     * @return the crafting item
     */
    public int getGroup() {
        return group;
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
     * Get the index of this item.
     *
     * @return the index of the item
     */
    public int getItemIndex() {
        return itemIndex;
    }
}
