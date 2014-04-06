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
package illarion.easynpc.data;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nonnull;

/**
 * This enumerator contains the valid races a player and a NPC can get in the easyNPC language.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum CharacterRace {
    dwarf(1),
    elf(3),
    halfling(2),
    human(0),
    lizardman(5),
    orc(4);

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
    CharacterRace(int id) {
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

    /**
     * Add this values to the highlighted tokens.
     *
     * @param map the map that stores the tokens
     */
    public static void enlistHighlightedWords(@Nonnull TokenMap map) {
        for (CharacterRace race : CharacterRace.values()) {
            map.put(race.name(), Token.VARIABLE);
        }
    }
}
