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
package illarion.easynpc.parsed.talk.conditions;

import illarion.easynpc.data.CompareOperators;
import illarion.easynpc.data.ItemPositions;
import illarion.easynpc.data.Items;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.writer.LuaWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the item condition.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConditionItem implements TalkCondition {
    /**
     * The code needed for this condition in the easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "item(%1$s, %2$s) %3$s %4$s";

    /**
     * The code needed for this condition in the easyNPC script. This code uses the additional data parameter this
     * condition can use.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE_DATA = "item(%1$s, %2$s, %5$s) %3$s %4$s";

    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addCondition(%1$s.item(%2$s, \"%3$s\", \"%4$s\", %5$s));"
            + LuaWriter.NL;

    /**
     * The LUA code needed for this consequence to work. This code uses the additional data parameter this condition
     * can use.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE_DATA =
            "talkEntry:addCondition(%1$s.item(%2$s, \"%3$s\", \"%4$s\", %5$s, %6$s));" + LuaWriter.NL;

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
    private final long data;

    /**
     * The item that is assigned to this item condition.
     */
    private final Items item;

    /**
     * The position where to look for this items.
     */
    private final ItemPositions itemPos;

    /**
     * The compare operator that is used to compare with the amount.
     */
    private final CompareOperators operator;

    /**
     * The value the amount of items is compared against.
     */
    private final AdvancedNumber value;

    /**
     * Constructor to set the values of the item along with the default data value
     *
     * @param newItem  the item that is stored in this condition
     * @param pos      the position where to search for this item
     * @param op       the operation that is used to compare the amount
     * @param newValue the value the amount of items is compared against
     */
    public ConditionItem(final Items newItem, final ItemPositions pos, final CompareOperators op,
                         final AdvancedNumber newValue) {
        this(newItem, pos, op, newValue, NO_DATA);
    }

    /**
     * Constructor to set the values of the item.
     *
     * @param newItem  the item that is stored in this condition
     * @param pos      the position where to search for this item
     * @param op       the operation that is used to compare the amount
     * @param newValue the value the amount of items is compared against
     * @param newData  the data value the search is limited to
     */
    public ConditionItem(final Items newItem, final ItemPositions pos, final CompareOperators op,
                         final AdvancedNumber newValue, final long newData) {
        item = newItem;
        itemPos = pos;
        operator = op;
        value = newValue;
        data = newData;
    }

    /**
     * Get the LUA module needed for this condition.
     */
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write this item condition into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        if (data == NO_DATA) {
            target.write(String.format(EASY_CODE, Integer.toString(item.getItemId()), itemPos.name(),
                    operator.getLuaComp(), value.getEasyNPC()));
        } else {
            target.write(String.format(EASY_CODE_DATA, Integer.toString(item.getItemId()), itemPos.name(),
                    operator.getLuaComp(), value.getEasyNPC(), Long.toString(data)));
        }
    }

    /**
     * Write the LUA code needed for this item condition.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        if (data == NO_DATA) {
            target.write(String.format(LUA_CODE, LUA_MODULE, Integer.toString(item.getItemId()), itemPos.name(),
                    operator.getLuaComp(), value.getLua()));
        } else {
            target.write(String.format(LUA_CODE_DATA, LUA_MODULE, Integer.toString(item.getItemId()), itemPos.name(),
                    operator.getLuaComp(), value.getLua(), Long.toString(data)));
        }
    }
}
