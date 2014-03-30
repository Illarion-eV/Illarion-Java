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
package illarion.client.gui.util;

import org.illarion.nifty.controls.CraftingCategoryEntry;
import org.illarion.nifty.controls.CraftingItemEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class contains the data of a category that is displayed in the crafting dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class NiftyCraftingCategory implements CraftingCategoryEntry {
    /**
     * The name of the category.
     */
    private final String name;

    /**
     * The items inside this category.
     */
    @Nonnull
    private final List<CraftingItemEntry> children;

    /**
     * Create a new category with a specified name.
     *
     * @param categoryName the name of the category
     */
    public NiftyCraftingCategory(final String categoryName) {
        name = categoryName;
        children = new ArrayList<>();
    }

    @Nonnull
    @Override
    public String getCategoryName() {
        return name;
    }

    @Nonnull
    @Override
    public List<CraftingItemEntry> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Add a child to this category.
     *
     * @param child the item that will belong to this category
     */
    public void addChild(final CraftingItemEntry child) {
        children.add(child);
    }

    @Nonnull
    @Override
    public String getTreeLabel() {
        return name;
    }
}
