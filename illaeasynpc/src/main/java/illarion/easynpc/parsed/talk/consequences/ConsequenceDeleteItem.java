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
package illarion.easynpc.parsed.talk.consequences;

import illarion.easynpc.data.Items;
import illarion.easynpc.parsed.shared.ParsedItemData;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.writer.LuaRequireTable;
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the delete item consequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceDeleteItem implements TalkConsequence {

    /**
     * The LUA code needed to be included for a delete item consequence. This code is used in case there is a data
     * value set.
     */
    private static final String LUA_CODE_DATA =
            "talkEntry:addConsequence(%1$s(%2$s, %3$s, %4$s))" + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence.
     */
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
     * @param newItem the item that is supposed to be deleted
     * @param newValue the amount of items that is supposed to be deleted
     * @param newData the data value that is used for this delete operation
     */
    public ConsequenceDeleteItem(Items newItem, AdvancedNumber newValue, ParsedItemData newData) {
        item = newItem;
        value = newValue;
        data = newData;
    }

    /**
     * Get the module that is needed for this consequence to work.
     */
    @Nonnull
    @Override
    public String getLuaModule() {
        return LUA_MODULE;
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(@Nonnull Writer target, @Nonnull LuaRequireTable requires) throws IOException {
        target.write(String.format(LUA_CODE_DATA, requires.getStorage(LUA_MODULE), Integer.toString(item.getItemId()),
                                   value.getLua(), data.getLua()));
    }
}
