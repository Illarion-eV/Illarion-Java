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
package illarion.easynpc.parsed.talk;

import javolution.context.ObjectFactory;

import illarion.common.util.Reusable;

/**
 * This class is used to store a advanced number value that is possibly used by
 * the easyNPC language. Such a number can contain a normal number, a reference
 * to the last said number or a formula.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class AdvancedNumber implements Reusable {
    /**
     * This factory is used to store unused and create new AdvancedNumber
     * objects.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class AdvancedNumberFactory extends
        ObjectFactory<AdvancedNumber> {
        /**
         * Public constructor to ensure that the parent class is able to create
         * a instance.
         */
        public AdvancedNumberFactory() {
            // nothing to do
        }

        /**
         * Create a new object.
         */
        @Override
        protected AdvancedNumber create() {
            return new AdvancedNumber();
        }
    }

    /**
     * The factory used to create and recycle objects of this type.
     */
    private static final AdvancedNumberFactory FACTORY =
        new AdvancedNumberFactory();

    /**
     * The type constant for this number to be a calculation constant.
     */
    private static final int TYPE_EXPRESSION = 3;

    /**
     * The type constant for this number to be a normal number.
     */
    private static final int TYPE_NORMAL = 1;

    /**
     * The type constant for this number to be a spoken number.
     */
    private static final int TYPE_SAIDNUMBER = 2;

    /**
     * The expression stored in this number. This is only used if the type of
     * this number is {@link #TYPE_EXPRESSION}.
     */
    private String expression;

    /**
     * The type of this number.
     */
    private int type;

    /**
     * The value of this number. This is used in case the type is
     * {@link #TYPE_NORMAL}.
     */
    private int value;

    /**
     * Default scope constructor to avoid other classes to create instances.
     */
    AdvancedNumber() {
        // nothing to do
    }

    /**
     * Get a instance of the advanced number object. This returns either a old
     * object that is reused or a newly created one.
     * 
     * @return the instance that is free to be used
     */
    public static AdvancedNumber getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the easyNPC representation of this advanced number value.
     * 
     * @return the easyNPC representation of the advanced number
     */
    @SuppressWarnings("nls")
    public String getEasyNPC() {
        if (type == TYPE_NORMAL) {
            return Integer.toString(value);
        }
        if (type == TYPE_SAIDNUMBER) {
            return "%NUMBER";
        }
        if (type == TYPE_EXPRESSION) {
            return "expr( " + expression + ")";
        }
        return null;
    }

    /**
     * Get the LUA representation of this advanced number value.
     * 
     * @return the LUA representation of the advanced number
     */
    @SuppressWarnings("nls")
    public String getLua() {
        if (type == TYPE_NORMAL) {
            return Integer.toString(value);
        }
        if (type == TYPE_SAIDNUMBER) {
            return "\"%NUMBER\"";
        }
        if (type == TYPE_EXPRESSION) {
            return "function(number) return ("
                + expression.replace("%NUMBER", "number") + "); end";
        }
        return null;
    }

    /**
     * Recycle the instance and put it back into the factory so it can be reused
     * later.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Reset this instace.
     */
    @Override
    public void reset() {
        expression = null;
        type = -1;
    }

    /**
     * Set the expression of this number. In this case the number stores a
     * calculation expression.
     * 
     * @param newExpr the string of the calculation expression
     */
    public void setExpression(final String newExpr) {
        expression = newExpr;
        type = TYPE_EXPRESSION;
    }

    /**
     * Set this number to be a normal number.
     * 
     * @param newValue the value of the number
     */
    public void setNormal(final int newValue) {
        type = TYPE_NORMAL;
        value = newValue;
    }

    /**
     * Set this number to be a spoken number.
     */
    public void setSaidNumber() {
        type = TYPE_SAIDNUMBER;
    }
}
