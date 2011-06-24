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
package illarion.easynpc.parsed.talk.conditions;

import java.io.IOException;
import java.io.Writer;

import javolution.context.ObjectFactory;

import illarion.easynpc.data.CompareOperators;
import illarion.easynpc.data.ItemPositions;
import illarion.easynpc.data.Items;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkCondition;

/**
 * This class is used to store all required values for the item condition.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConditionItem implements TalkCondition {
    /**
     * The factory class that creates and buffers ConditionItem objects for
     * later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConditionItemFactory extends
        ObjectFactory<ConditionItem> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConditionItemFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConditionItem create() {
            return new ConditionItem();
        }
    }

    /**
     * The code needed for this condition in the easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "item(%1$s, %2$s) %3$s %4$s";

    /**
     * The code needed for this condition in the easyNPC script. This code uses
     * the additional data parameter this condition can use.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE_DATA =
        "item(%1$s, %2$s, %5$s) %3$s %4$s";

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConditionItemFactory FACTORY =
        new ConditionItemFactory();

    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
        "talkEntry:addCondition(%1$s.item(%2$s, \"%3$s\", \"%4$s\", %5$s));"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The LUA code needed for this consequence to work. This code uses the
     * additional data parameter this condition can use.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE_DATA =
        "talkEntry:addCondition(%1$s.item(%2$s, \"%3$s\", \"%4$s\", %5$s, %6$s));"
            + illarion.easynpc.writer.LuaWriter.NL;
    /**
     * The LUA module required for this condition to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "item";

    /**
     * The constant used to mark the data value that no data is used.
     */
    private static final long NO_DATA = -1L;

    /**
     * The data value the search for the item is limited to.
     */
    private long data;

    /**
     * The item that is assigned to this item condition.
     */
    private Items item;

    /**
     * The position where to look for this items.
     */
    private ItemPositions itemPos;

    /**
     * The compare operator that is used to compare with the amount.
     */
    private CompareOperators operator;

    /**
     * The value the amount of items is compared against.
     */
    private AdvancedNumber value;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConditionItem() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConditionItem getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the LUA module needed for this condition.
     */
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Recycle the object so it can be used again later.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Reset the state of this instance to its ready to be used later.
     */
    @Override
    public void reset() {
        item = null;
        itemPos = null;
        operator = null;
        if (value != null) {
            value.recycle();
            value = null;
        }
    }

    /**
     * Set the data of this item condition.
     * 
     * @param newItem the item that is stored in this condition
     * @param pos the position where to search for this item
     * @param op the operation that is used to compare the amount
     * @param newValue the value the amount of items is compared against
     */
    public void setData(final Items newItem, final ItemPositions pos,
        final CompareOperators op, final AdvancedNumber newValue) {
        item = newItem;
        itemPos = pos;
        operator = op;
        value = newValue;
        data = NO_DATA;
    }

    /**
     * Set the data of this item condition.
     * 
     * @param newItem the item that is stored in this condition
     * @param pos the position where to search for this item
     * @param op the operation that is used to compare the amount
     * @param newValue the value the amount of items is compared against
     * @param newData the data value the search is limited to
     */
    public void setData(final Items newItem, final ItemPositions pos,
        final CompareOperators op, final AdvancedNumber newValue,
        final long newData) {
        item = newItem;
        itemPos = pos;
        operator = op;
        value = newValue;
        data = newData;
    }

    /**
     * Write this item condition into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        if (data == NO_DATA) {
            target.write(String.format(EASY_CODE,
                Integer.toString(item.getItemId()), itemPos.name(),
                operator.getLuaComp(), value.getEasyNPC()));
        } else {
            target
                .write(String.format(EASY_CODE_DATA,
                    Integer.toString(item.getItemId()), itemPos.name(),
                    operator.getLuaComp(), value.getEasyNPC(),
                    Long.toString(data)));
        }
    }

    /**
     * Write the LUA code needed for this item condition.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        if (data == NO_DATA) {
            target.write(String.format(LUA_CODE, LUA_MODULE,
                Integer.toString(item.getItemId()), itemPos.name(),
                operator.getLuaComp(), value.getLua()));
        } else {
            target.write(String.format(LUA_CODE_DATA, LUA_MODULE,
                Integer.toString(item.getItemId()), itemPos.name(),
                operator.getLuaComp(), value.getLua(), Long.toString(data)));
        }
    }
}
