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
 * This enumerator stores the information about all towns possible to be used in the scripts.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum Towns {
    Cadomyr(true, 1),
    Free(false, -1),
    Galmair(true, 3),
    None(false, 0),
    Runewick(true, 2);

    /**
     * The ID of this town needed for the LUA scripts.
     */
    private final int id;

    /**
     * This valid contains <code>true</code> in case this constant is usable for rankpoints.
     */
    private final boolean rankpt;

    /**
     * Default constructor for this groups.
     *
     * @param rankpoints <code>true</code> in case this constant can be used for
     * rankpoints
     */
    Towns(final boolean rankpoints, final int factionId) {
        rankpt = rankpoints;
        id = factionId;
    }

    /**
     * Get the faction ID of this town.
     *
     * @return the faction id
     */
    public int getFactionId() {
        return id;
    }

    /**
     * Check if this constant can be used for rankpoints.
     *
     * @return <code>true</code> in case this constant can be used for
     * rankpoints
     */
    public boolean isValidForRankpoints() {
        return rankpt;
    }

    /**
     * Add this values to the highlighted tokens.
     *
     * @param map the map that stores the tokens
     */
    public static void enlistHighlightedWords(@Nonnull final TokenMap map) {
        for (final Towns town : Towns.values()) {
            map.put(town.name(), Token.VARIABLE);
        }
    }
}
