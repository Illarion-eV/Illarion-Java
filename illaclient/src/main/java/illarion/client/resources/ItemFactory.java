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
package illarion.client.resources;

import illarion.client.resources.data.ItemTemplate;
import illarion.common.types.ItemId;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

/**
 * The Item factory loads creates and stores all instances of the item class
 * that are around in the client.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class ItemFactory extends AbstractTemplateFactory<ItemTemplate> {
    /**
     * The singleton instance of this class.
     */
    @Nonnull
    private static final ItemFactory INSTANCE = new ItemFactory();

    /**
     * The ID of the item that is supposed to be displayed by default.
     */
    public static final int DEFAULT_ITEM_ID = 2;

    /**
     * Get the singleton instance of this factory.
     *
     * @return the singleton instance of this factory
     */
    @Nonnull
    @Contract(pure = true)
    public static ItemFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Construct the item factory.
     */
    private ItemFactory() {
        super(DEFAULT_ITEM_ID);
    }

    @Nonnull
    @Contract(pure = true)
    public ItemTemplate getTemplate(@Nonnull ItemId id) {
        return getTemplate(id.getValue());
    }
}
