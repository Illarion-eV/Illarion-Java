/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.data;

/**
 * This enumerator contains a list of valid locations for a item. At this
 * positions a item can be searched or created.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public enum ItemPositions {
    /**
     * All possible locations on the character. Means in the backpack, at the
     * belt or on the body.
     */
    all,

    /**
     * All slots in the bag the character is wearing.
     */
    backpack,

    /**
     * Only the six belt slots.
     */
    belt,

    /**
     * Only body positions. So the clothing/armor positions and the hands.
     */
    body;
}
