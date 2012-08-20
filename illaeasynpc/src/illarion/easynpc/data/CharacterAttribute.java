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
 * The list of character attributes, the easyNPC script and the LUA script is able to use.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum CharacterAttribute {
    agility, constitution, dexterity, essence, foodlevel, hitpoints,
    intelligence, manapoints, perception, strength, willpower;

    /**
     * Add this values to the highlighted tokens.
     *
     * @param map the map that stores the tokens
     */
    public static void enlistHighlightedWords(final TokenMap map) {
        for (CharacterAttribute attribute : CharacterAttribute.values()) {
            map.put(attribute.name(), Token.VARIABLE);
        }
    }
}
