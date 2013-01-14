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

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

/**
 * This constant contains the valid boolean flags for a easyNPC script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum BooleanFlagValues {
    /**
     * The easyNPC representation for <code>false</code>.
     */
    @SuppressWarnings("nls")
    off("((off)|(false)|(no))", "off", "false"),

    /**
     * The easyNPC representation for <code>true</code>.
     */
    @SuppressWarnings("nls")
    on("((on)|(true)|(yes))", "on", "true");

    /**
     * The string used in the easyNPC script for this flag.
     */
    private final String easyString;

    /**
     * The pattern used to identify the boolean flag.
     */
    private final Pattern findPattern;

    /**
     * The string used in LUA for this flag.
     */
    private final String luaString;

    /**
     * The constructor for the boolean flag values.
     *
     * @param regex the regular expression used to identify the flag
     * @param easy  the easyNPC representation of this flag
     * @param lua   the LUa representation of this flag
     */
    private BooleanFlagValues(final String regex, final String easy,
                              final String lua) {
        findPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        luaString = lua;
        easyString = easy;
    }

    /**
     * Get the easyNPC representation of this flag.
     *
     * @return the easyNPC representation
     */
    public String getEasyNpc() {
        return easyString;
    }

    /**
     * Get the LUA representation of this flag.
     *
     * @return the LUA representation
     */
    public String getLUA() {
        return luaString;
    }

    /**
     * Get the pattern needed to identify the flag.
     *
     * @return the pattern
     */
    public Pattern getPattern() {
        return findPattern;
    }

    /**
     * Add this values to the highlighted tokens.
     *
     * @param map the map that stores the tokens
     */
    public static void enlistHighlightedWords(@Nonnull final TokenMap map) {
        map.put(off.easyString, Token.VARIABLE);
        map.put(on.easyString, Token.VARIABLE);
    }
}
