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

/**
 * @author Fredrik K
 */
public class ItemReplaceEvent {
    private final int index;
    private final int newIndex;

    public ItemReplaceEvent(int index, int newIndex) {
        this.index = index;
        this.newIndex = newIndex;
    }

    public int getIndex() {
        return index;
    }

    public int getNewIndex() {
        return newIndex;
    }
}
