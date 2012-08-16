/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyNPC Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyNPC Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.data;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

/**
 * This enumerator stores the information about all towns possible to be used in the scripts.
 *
 * @author Martin Karing
 */
public enum Towns {
    Cadomyr(true, 1), Free(false, -1), Galmair(true, 3), None(false, -1),
    Runewick(true, 2);

    /**
     * The ID of this town needed for the LUA scripts.
     */
    private final int id;

    /**
     * This valid contains <code>true</code> in case this constant is useable
     * for rankpoints.
     */
    private final boolean rankpt;

    /**
     * Default constructor for this groups.
     *
     * @param rankpoints <code>true</code> in case this constant can be used for
     *                   rankpoints
     */
    private Towns(final boolean rankpoints, final int factionId) {
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
     *         rankpoints
     */
    public boolean validForRankpoints() {
        return rankpt;
    }

    /**
     * Add this values to the highlighted tokens.
     *
     * @param map the map that stores the tokens
     */
    public static void enlistHighlightedWords(final TokenMap map) {
        for (final Towns town : Towns.values()) {
            map.put(town.name(), Token.VARIABLE);
        }
    }
}
