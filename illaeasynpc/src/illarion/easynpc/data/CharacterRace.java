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
 * This enumerator contains the valid races a player and a NPC can get in the
 * easyNPC language.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public enum CharacterRace {
    dwarf(1), elf(3), halfling(2), human(0), lizardman(5), orc(4);

    /**
     * The ID of the race to identify it in the lua script.
     */
    private final int raceId;

    /**
     * The constructor for the NPC constant that stores the ID of this contant
     * along with it.
     * 
     * @param id the ID representation of this constant.
     */
    private CharacterRace(final int id) {
        raceId = id;
    }

    /**
     * Get the ID of this race representation.
     * 
     * @return the ID of this race representation
     */
    public int getId() {
        return raceId;
    }
}
