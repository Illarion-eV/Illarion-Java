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
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * This interface defines a category of items that can be crafted.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public interface CraftingCategoryEntry extends CraftingTreeItem {
    /**
     * Get the name of this category.
     *
     * @return the name of the category
     */
    @Nonnull
    String getCategoryName();

    /**
     * Get the items that are part of this category.
     *
     * @return the items that are listed as part of this category
     */
    @Nonnull
    List<CraftingItemEntry> getChildren();
}
