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

import java.util.regex.Pattern;

/**
 * This enumerator contains a list of all possible compare operators that are
 * usable in a LUA and a easyNPC script.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public enum CompareOperators {
    @SuppressWarnings("nls")
    equal("=", "^\\s*=\\s*$"), @SuppressWarnings("nls")
    greater(">", "^\\s*>\\s*$"), @SuppressWarnings("nls")
    greaterEqual("=>", "^\\s*((>=)|(=>))\\s*$"), @SuppressWarnings("nls")
    lesser("<", "^\\s*<\\s*$"), @SuppressWarnings("nls")
    lesserEqual("=<", "^\\s*((<=)|(=<))\\s*$"), @SuppressWarnings("nls")
    notEqual("~=", "^\\s*(([!~]=)|(<>))\\s*$");

    /**
     * The lua representation for this comparator.
     */
    private final String luaComp;

    /**
     * The RegExp pattern to identify comparator in the string.
     */
    private final Pattern regexpComp;

    /**
     * Constructor for the compare operators.
     * 
     * @param lua the LUA representation of this operator
     * @param regexp the RegExp pattern to identify this operator
     */
    private CompareOperators(final String lua, final String regexp) {
        luaComp = lua;
        regexpComp = Pattern.compile(regexp);
    }

    /**
     * Get the LUA representation of this operator.
     * 
     * @return the LUA representation
     */
    public String getLuaComp() {
        return luaComp;
    }

    /**
     * Get the RegExp pattern usable to identify the operator in the easyNPC
     * script.
     * 
     * @return the pattern to find this operator
     */
    public Pattern getRegexpPattern() {
        return regexpComp;
    }
}
