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

import illarion.common.types.ItemCount;

import javax.annotation.Nonnull;

/**
 * This interface is implemented by any interactive instance that can be dragged
 * around.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("ClassReferencesSubclass")
public interface Draggable {
    /**
     * Drag the object onto a character.
     *
     * @param targetChar the character to drag the object to
     * @param count      the amount of objects to be transferred
     */
    @SuppressWarnings("UnusedDeclaration")
    void dragTo(@Nonnull InteractiveChar targetChar, @Nonnull ItemCount count);

    /**
     * Drag the object on a inventory slot.
     *
     * @param targetSlot the slot in the inventory that is the target
     * @param count      the amount of objects to be transferred
     */
    void dragTo(@Nonnull InteractiveInventorySlot targetSlot, @Nonnull ItemCount count);

    /**
     * Drag the object to a map tile.
     *
     * @param targetTile the tile the object shall be dragged on
     * @param count      the amount of objects to be transferred
     */
    void dragTo(@Nonnull InteractiveMapTile targetTile, @Nonnull ItemCount count);

    /**
     * Drag the object to a container slot.
     *
     * @param targetSlot the target container slot
     * @param count      the amount of objects to be transferred
     */
    void dragTo(@Nonnull InteractiveContainerSlot targetSlot, @Nonnull ItemCount count);
}
