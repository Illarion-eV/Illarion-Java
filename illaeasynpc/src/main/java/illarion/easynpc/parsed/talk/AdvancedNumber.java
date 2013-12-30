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
package illarion.easynpc.parsed.talk;

import javax.annotation.Nullable;

/**
 * This class is used to store a advanced number value that is possibly used by
 * the easyNPC language. Such a number can contain a normal number, a reference
 * to the last said number or a formula.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AdvancedNumber {
    /**
     * This enumerator contains the possible values for a advanced number.
     */
    private enum AdvancedNumberType {
        /**
         * This constant means that the number is a expression.
         */
        Expression,

        /**
         * This constant means that the number is the normal number.
         */
        Normal,

        /**
         * This constant means that the number is a reference to the spoken number of the user.
         */
        SaidNumber
    }

    /**
     * The expression stored in this number. This is only used if the type of this number is
     * {@link AdvancedNumber.AdvancedNumberType#Expression}.
     */
    private String expression;

    /**
     * The type of this number.
     */
    private AdvancedNumber.AdvancedNumberType type;

    /**
     * The value of this number. This is used in case the type is
     * {@link {@link AdvancedNumber.AdvancedNumberType#Normal}.
     */
    private int value;

    /**
     * The default constructor causes this number to be a reference to the number the player spoke last.
     */
    public AdvancedNumber() {
        type = AdvancedNumber.AdvancedNumberType.SaidNumber;
    }

    /**
     * This constructor causes this number to refer to a simple number value.
     *
     * @param number the number value
     */
    public AdvancedNumber(final int number) {
        type = AdvancedNumber.AdvancedNumberType.Normal;
        value = number;
    }

    /**
     * This constructor causes this number to refer to a expression string.
     *
     * @param expressionString the expression string
     */
    public AdvancedNumber(final String expressionString) {
        type = AdvancedNumber.AdvancedNumberType.Expression;
        expression = expressionString;
    }

    /**
     * Get the easyNPC representation of this advanced number value.
     *
     * @return the easyNPC representation of the advanced number
     */
    @Nullable
    @SuppressWarnings("nls")
    public String getEasyNPC() {
        switch (type) {
            case Normal:
                return Integer.toString(value);
            case SaidNumber:
                return "%NUMBER";
            case Expression:
                return "expr( " + expression + ')';
        }
        return null;
    }

    /**
     * Get the LUA representation of this advanced number value.
     *
     * @return the LUA representation of the advanced number
     */
    @Nullable
    @SuppressWarnings("nls")
    public String getLua() {
        switch (type) {
            case Normal:
                return Integer.toString(value);
            case SaidNumber:
                return "\"%NUMBER\"";
            case Expression:
                return "function(number) return (" + expression.replace("%NUMBER", "number") + "); end";
        }
        return null;
    }
}
