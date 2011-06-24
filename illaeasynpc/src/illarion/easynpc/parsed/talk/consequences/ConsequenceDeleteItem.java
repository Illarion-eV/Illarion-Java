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
package illarion.easynpc.parsed.talk.consequences;

import java.io.IOException;
import java.io.Writer;

import javolution.context.ObjectFactory;

import illarion.easynpc.data.Items;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkConsequence;

/**
 * This class is used to store all required values for the delete item
 * consequence.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConsequenceDeleteItem implements TalkConsequence {
    /**
     * The factory class that creates and buffers ConsequenceDeleteItem objects
     * for later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConsequenceDeleteItemFactory extends
        ObjectFactory<ConsequenceDeleteItem> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConsequenceDeleteItemFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConsequenceDeleteItem create() {
            return new ConsequenceDeleteItem();
        }
    }

    /**
     * The format string for the easyNPC code.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "deleteItem(%1$s, %2$s)";

    /**
     * The format string for the easyNPC code. This code is used in case there
     * is a data value set.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE_DATA =
        "deleteItem(%1$s, %2$s, %3$s)";

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConsequenceDeleteItemFactory FACTORY =
        new ConsequenceDeleteItemFactory();

    /**
     * The LUA code needed to be included for a delete item consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
        "talkEntry:addConsequence(%1$s.deleteitem(%2$s, %3$s));"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The LUA code needed to be included for a delete item consequence. This
     * code is used in case there is a data value set.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE_DATA =
        "talkEntry:addConsequence(%1$s.deleteitem(%2$s, %3$s, %4$s));"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The LUA module needed for this consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "deleteitem";

    /**
     * The constant for the data value in case there is no data used.
     */
    private static final long NO_DATA = -1L;
    /**
     * The data value for this consequence in case there is any.
     */
    private long data;

    /**
     * The item that is supposed to be deleted by this consequence.
     */
    private Items item;

    /**
     * The amount of items to be deleted.
     */
    private AdvancedNumber value;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConsequenceDeleteItem() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConsequenceDeleteItem getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the module that is needed for this consequence to work.
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
        if (value != null) {
            value.recycle();
            value = null;
        }
        data = NO_DATA;
    }

    /**
     * Set the data required for this delete item consequence.
     * 
     * @param newItem the item that is supposed to be deleted
     * @param newValue the amount of items that is supposed to be deleted
     */
    public void setData(final Items newItem, final AdvancedNumber newValue) {
        item = newItem;
        value = newValue;
        data = NO_DATA;
    }

    /**
     * Set the data required for this delete item consequence.
     * 
     * @param newItem the item that is supposed to be deleted
     * @param newValue the amount of items that is supposed to be deleted
     * @param newData the data value that is used for this delete operation
     */
    public void setData(final Items newItem, final AdvancedNumber newValue,
        final long newData) {
        item = newItem;
        value = newValue;
        data = newData;
    }

    /**
     * Write this delete item consequence into its easyNPC shape
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        if (data == NO_DATA) {
            target.write(String.format(EASY_CODE,
                Integer.toString(item.getItemId()), value.getEasyNPC()));
        } else {
            target.write(String.format(EASY_CODE_DATA,
                Integer.toString(item.getItemId()), value.getEasyNPC(),
                Long.toString(data)));
        }
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        if (data == NO_DATA) {
            target.write(String.format(LUA_CODE, LUA_MODULE,
                Integer.toString(item.getItemId()), value.getLua()));
        } else {
            target.write(String.format(LUA_CODE_DATA, LUA_MODULE,
                Integer.toString(item.getItemId()), value.getLua(),
                Long.toString(data)));
        }
    }
}
