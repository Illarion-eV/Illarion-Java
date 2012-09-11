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
package illarion.easynpc.parsed.talk.consequences;

import illarion.easynpc.data.Items;
import illarion.easynpc.parsed.shared.ParsedItemData;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaWriter;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the delete item consequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceDeleteItem implements TalkConsequence {
    /**
     * The format string for the easyNPC code.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "deleteItem(%1$s, %2$s)";

    /**
     * The format string for the easyNPC code. This code is used in case there is a data value set.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE_DATA = "deleteItem(%1$s, %2$s, %3$s)";

    /**
     * The LUA code needed to be included for a delete item consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addConsequence(%1$s.deleteitem(%2$s, %3$s));" + LuaWriter.NL;

    /**
     * The LUA code needed to be included for a delete item consequence. This code is used in case there is a data
     * value set.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE_DATA = "talkEntry:addConsequence(%1$s.deleteitem(%2$s, %3$s, %4$s));"
            + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "deleteitem";

    /**
     * The data value for this consequence in case there is any.
     */
    private final ParsedItemData data;

    /**
     * The item that is supposed to be deleted by this consequence.
     */
    private final Items item;

    /**
     * The amount of items to be deleted.
     */
    private final AdvancedNumber value;

    /**
     * The constructor that allows setting the item, the count and the data of items that are created by this
     * consequence.
     *
     * @param newItem  the item that is supposed to be deleted
     * @param newValue the amount of items that is supposed to be deleted
     * @param newData  the data value that is used for this delete operation
     */
    public ConsequenceDeleteItem(final Items newItem, final AdvancedNumber newValue, final ParsedItemData newData) {
        item = newItem;
        value = newValue;
        data = newData;
    }

    /**
     * Get the module that is needed for this consequence to work.
     */
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write this delete item consequence into its easyNPC shape
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        if (data.hasValues()) {
            target.write(String.format(EASY_CODE_DATA, Integer.toString(item.getItemId()), value.getEasyNPC(),
                    data.getEasyNPC()));
        } else {
            target.write(String.format(EASY_CODE, Integer.toString(item.getItemId()), value.getEasyNPC()));
        }
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE_DATA, LUA_MODULE, Integer.toString(item.getItemId()), value.getLua(),
                data.getLua()));
    }
}
