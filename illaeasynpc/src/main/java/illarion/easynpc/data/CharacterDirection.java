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

import illarion.common.types.Direction;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nonnull;

/**
 * This enumerator contains the valid direction values a easyNPC script is allowed to contain.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum CharacterDirection {
    east(Direction.East),
    north(Direction.North),
    northeast(Direction.NorthEast),
    northwest(Direction.NorthWest),
    south(Direction.South),
    southeast(Direction.SouthEast),
    southwest(Direction.SouthWest),
    west(Direction.West);

    /**
     * The ID of this direction value used to identify it in the lua script.
     */
    @Nonnull
    private final Direction dir;

    /**
     * The constructor for the NPC constant that stores the string
     * representation of the constants along with.
     *
     * @param dir the ID representation of this constant.
     */
    CharacterDirection(@Nonnull Direction dir) {
        this.dir = dir;
    }

    /**
     * Get the ID of this direction representation.
     *
     * @return the ID of this direction representation
     */
    public int getId() {
        return dir.getServerId();
    }

    /**
     * Add this values to the highlighted tokens.
     *
     * @param map the map that stores the tokens
     */
    public static void enlistHighlightedWords(@Nonnull TokenMap map) {
        for (CharacterDirection direction : CharacterDirection.values()) {
            map.put(direction.name(), Token.VARIABLE);
        }
    }
}
