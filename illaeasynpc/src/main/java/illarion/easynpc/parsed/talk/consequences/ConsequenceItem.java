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
import illarion.easynpc.writer.LuaWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * This class is used to store all required values for the item consequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConsequenceItem implements TalkConsequence {
    /**
     * The easyNPC code (without data) needed for this consequence.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE_NO_DATA = "item(%1$s, %2$s, %3$s)";

    /**
     * The LUA code needed to be included for a create item consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
            "talkEntry:addConsequence(%1$s.item(%2$s, %3$s, %4$s, %5$s));" + LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "item";

    /**
     * The data value of the created items.
     */
    private final ParsedItemData data;

    /**
     * The item that is supposed to be created with this consequence.
     */
    private final Items item;

    /**
     * The quality value of the created items.
     */
    private final int quality;

    /**
     * The amount of items to be created.
     */
    private final AdvancedNumber value;

    /**
     * The constructor that allows setting the parameters of this item.
     *
     * @param newItem the item to create
     * @param newValue the amount to create
     * @param newQuality the quality value of the item
     * @param newData the data value of the item
     */
    public ConsequenceItem(
            final Items newItem, final AdvancedNumber newValue, final int newQuality, final ParsedItemData newData) {
        item = newItem;
        value = newValue;
        quality = newQuality;
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
    public void writeLua(@Nonnull final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE, Integer.toString(item.getItemId()), value.getLua(),
                                   Integer.toString(quality), data.getLua()));
    }
}
