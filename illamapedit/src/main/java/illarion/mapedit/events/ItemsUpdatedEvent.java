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
package illarion.mapedit.events;

import illarion.mapedit.data.MapItem;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Fredrik K
 */
public class ItemsUpdatedEvent {
    @Nullable
    private final List<MapItem> items;

    public ItemsUpdatedEvent(@Nullable List<MapItem> items) {
        this.items = items;
    }

    @Nullable
    public List<MapItem> getItems() {
        return items;
    }
}
