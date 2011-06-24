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

import illarion.common.util.Location;

/**
 * This enumerator contains the valid direction values a easyNPC script is
 * allowed to contain.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public enum CharacterDirection {
    east(Location.DIR_EAST), north(Location.DIR_NORTH), northeast(
        Location.DIR_NORTHEAST), northwest(Location.DIR_NORTHWEST), south(
        Location.DIR_SOUTH), southeast(Location.DIR_SOUTHEAST), southwest(
        Location.DIR_SOUTHWEST), west(Location.DIR_WEST);

    /**
     * The ID of this direction value used to identify it in the lua script.
     */
    private final int dirId;

    /**
     * The constructor for the NPC constant that stores the string
     * representation of the constants along with.
     * 
     * @param id the ID representation of this constant.
     */
    private CharacterDirection(final int id) {
        dirId = id;
    }

    /**
     * Get the ID of this direction representation.
     * 
     * @return the ID of this direction representation
     */
    public int getId() {
        return dirId;
    }
}
