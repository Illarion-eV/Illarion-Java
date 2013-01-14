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
package illarion.client.world.interactive;

import illarion.client.world.Char;
import illarion.common.types.ItemCount;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * This class represents the interactive variant of a character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class InteractiveChar implements Draggable, DropTarget {
    /**
     * The character this interactive reference points to.
     */
    @Nonnull
    private final Char parentChar;

    /**
     * Create a new interactive reference to a character.
     *
     * @param parent the character this interactive reference points to
     */
    public InteractiveChar(@Nonnull final Char parent) {
        parentChar = parent;
    }

    /**
     * Drag one character to another character. Does nothing currently.
     */
    @Override
    public void dragTo(@Nonnull final InteractiveChar targetChar, @Nonnull final ItemCount count) {
        // nothing
    }

    /**
     * Dragging the character into the inventory does nothing at all.
     */
    @Override
    public void dragTo(@Nonnull final InteractiveInventorySlot targetSlot, @Nonnull final ItemCount count) {
        // nothing
    }

    /**
     * Drag the character to another spot on the map. This causes pushing the
     * character.
     */
    @Override
    public void dragTo(@Nonnull final InteractiveMapTile targetTile, @Nonnull final ItemCount count) {
        // nothing
    }

    @Override
    public void dragTo(@Nonnull final InteractiveContainerSlot targetSlot, @Nonnull final ItemCount count) {
        // nothing
    }

    /**
     * Get the location of the character on the map.
     *
     * @return the location of the character on the map
     */
    @Nonnull
    public Location getLocation() {
        return parentChar.getLocation();
    }
}
