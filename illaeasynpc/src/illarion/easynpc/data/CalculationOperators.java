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
 * This enumerator contains a list of all possible calculation operators that
 * are usable in a LUA and a easyNPC script.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public enum CalculationOperators {
    /**
     * The addition operator with all required data to detect it in a easyNPC
     * script and to write it into a LUA script.
     */
    @SuppressWarnings("nls")
    add("+", "^\\s*(\\+[=]*)\\s*$"),
    
    /**
     * The set operator with all required data to detect it in a easyNPC
     * script and to write it into a LUA script.
     */
    @SuppressWarnings("nls")
    set("=", "^\\s*=\\s*$"),
    
    /**
     * The subtraction operator with all required data to detect it in a easyNPC
     * script and to write it into a LUA script.
     */
    @SuppressWarnings("nls")
    subtract("-", "^\\s*(-[=]*)\\s*$");

    /**
     * The LUA representation for this operator.
     */
    private final String luaOp;

    /**
     * The RegExp pattern to identify operator in the string.
     */
    private final Pattern regexpOp;

    /**
     * Constructor for the calculation operators.
     * 
     * @param lua the LUA representation of this operator
     * @param regexp the RegExp pattern to identify this operator
     */
    private CalculationOperators(final String lua, final String regexp) {
        luaOp = lua;
        regexpOp = Pattern.compile(regexp);
    }

    /**
     * Get the LUA representation of this operator.
     * 
     * @return the LUA representation
     */
    public String getLuaOp() {
        return luaOp;
    }

    /**
     * Get the RegExp pattern usable to identify the operator in the easyNPC
     * script.
     * 
     * @return the pattern to find this operator
     */
    public Pattern getRegexpPattern() {
        return regexpOp;
    }
}
