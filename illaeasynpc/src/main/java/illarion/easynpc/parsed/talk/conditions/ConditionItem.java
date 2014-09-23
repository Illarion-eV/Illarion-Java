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
package illarion.easynpc.parsed.talk.conditions;

import illarion.easynpc.data.CompareOperators;
import illarion.easynpc.data.ItemPositions;
import illarion.easynpc.data.Items;
import illarion.easynpc.parsed.shared.ParsedItemData;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.writer.LuaRequireTable;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the item condition.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConditionItem implements TalkCondition {
    /**
     * The LUA code needed for this consequence to work. This code uses the additional data parameter this condition
     * can use.
     */
    private static final String LUA_CODE =
            "talkEntry:addCondition(%1$s(%2$s, \"%3$s\", \"%4$s\", %5$s, %6$s))" + LuaWriter.NL;

    /**
     * The LUA module required for this condition to work.
     */
    private static final String LUA_MODULE = BASE_LUA_MODULE + "item";

    /**
     * The data value the search for the item is limited to.
     */
    private final ParsedItemData data;

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
     * Constructor to set the values of the item.
     *
     * @param newItem the item that is stored in this condition
     * @param pos the position where to search for this item
     * @param op the operation that is used to compare the amount
     * @param newValue the value the amount of items is compared against
     * @param newData the data value the search is limited to
     */
    public ConditionItem(
            Items newItem, ItemPositions pos, CompareOperators op, AdvancedNumber newValue, ParsedItemData newData) {
        item = newItem;
        itemPos = pos;
        operator = op;
        value = newValue;
        data = newData;
    }

    /**
     * Get the LUA module needed for this condition.
     */
    @Nonnull
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write the LUA code needed for this item condition.
     */
    @Override
    public void writeLua(@Nonnull Writer target, @Nonnull LuaRequireTable requires) throws IOException {
        target.write(String.format(LUA_CODE, requires.getStorage(LUA_MODULE), Integer.toString(item.getItemId()),
                                   itemPos.name(), operator.getLuaComp(), value.getLua(), data.getLua()));
    }
}
