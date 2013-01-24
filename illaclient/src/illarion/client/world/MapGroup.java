/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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
package illarion.client.world;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is used to organise the maps into groups. This is done to show and hide whole groups of maps.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MapGroup {
    /**
     * In case this flag is turned {@code true} the entire map group is hidden. This value has no effect at all in
     * case the {@link #parent} is not set to {@code null}.
     */
    private boolean hidden;

    /**
     * The parent group that will overwrite the local hidden value. This is used to connect multiple map groups.
     */
    @Nullable
    private MapGroup parent;

    /**
     * Check if this map group is currently hidden.
     *
     * @return {@code in case the map group is hidden}
     */
    public boolean isHidden() {
        MapGroup other = this;
        while (true) {
            assert other != null;
            if (other.parent == null) {
                return other.hidden;
            }
            other = other.parent;
        }
    }

    /**
     * Set the hidden flag of this map group.
     *
     * @param hidden the hidden flag
     */
    public void setHidden(final boolean hidden) {
        MapGroup other = this;
        while (true) {
            assert other != null;
            if (other.parent == null) {
                other.hidden = hidden;
                return;
            }
            other = other.parent;
        }
    }

    /**
     * Apply a parent to this map group.
     *
     * @param parent the parent of this group
     */
    public void setParent(@Nullable final MapGroup parent) {
        this.parent = parent;
        setHidden(hidden);
    }

    @Nonnull
    public MapGroup getRootGroup() {
        MapGroup currentGroup = this;
        while (true) {
            assert currentGroup != null;
            if (currentGroup.parent == null) {
                return currentGroup;
            }
            currentGroup = currentGroup.parent;
        }
    }
}
