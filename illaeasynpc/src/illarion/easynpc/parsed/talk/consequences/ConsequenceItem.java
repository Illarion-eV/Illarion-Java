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
 * This class is used to store all required values for the item consequence.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConsequenceItem implements TalkConsequence {
    /**
     * The factory class that creates and buffers ConsequenceItem objects for
     * later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConsequenceItemFactory extends
        ObjectFactory<ConsequenceItem> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConsequenceItemFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConsequenceItem create() {
            return new ConsequenceItem();
        }
    }

    /**
     * The easyNPC code needed for this consequence.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "item(%1$s, %2$s, %3$s, %4$s)";

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConsequenceItemFactory FACTORY =
        new ConsequenceItemFactory();

    /**
     * The LUA code needed to be included for a create item consequence.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE =
        "talkEntry:addConsequence(%1$s.item(%2$s, %3$s, %4$s, %5$s));"
            + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The LUA module needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_MODULE = BASE_LUA_MODULE + "item";

    /**
     * The data value of the created items.
     */
    private int data;
    /**
     * The item that is supposed to be created with this consequence.
     */
    private Items item;

    /**
     * The quality value of the created items.
     */
    private int quality;

    /**
     * The amount of items to be created.
     */
    private AdvancedNumber value;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConsequenceItem() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConsequenceItem getInstance() {
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
    }

    /**
     * Set the data needed for this create item consequence.
     * 
     * @param newItem the item to create
     * @param newValue the amount to create
     * @param newQuality the quality value of the item
     * @param newData the data value of the item
     */
    public void setData(final Items newItem, final AdvancedNumber newValue,
        final int newQuality, final int newData) {
        item = newItem;
        value = newValue;
        quality = newQuality;
        data = newData;
    }

    /**
     * Write this create item consequence into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE,
            Integer.toString(item.getItemId()), value.getEasyNPC(),
            Integer.toString(quality), Integer.toString(data)));
    }

    /**
     * Write the LUA code of this consequence.
     */
    @Override
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE, LUA_MODULE,
            Integer.toString(item.getItemId()), value.getLua(),
            Integer.toString(quality), Integer.toString(data)));
    }
}
