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
package illarion.easynpc.parsed;

import java.io.IOException;
import java.io.Writer;

import javolution.context.ObjectFactory;

import illarion.easynpc.data.EquipmentSlots;
import illarion.easynpc.data.Items;
import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

/**
 * This class stores the parsed equipment data that contains informations about
 * what the NPC wears.
 * 
 * @author Martin Karing
 * @version 1.00
 * @since 1.02
 */
public final class ParsedEquipment implements ParsedData {
    /**
     * The factory for the parsed equipment. This stores all formerly created
     * and currently unused instances of the ParsedEquipment class.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ParsedEquipmentFactory extends
        ObjectFactory<ParsedEquipment> {
        /**
         * Public constructor to allow the parent class to create a proper
         * instance.
         */
        public ParsedEquipmentFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of the recycled object.
         */
        @Override
        protected ParsedEquipment create() {
            return new ParsedEquipment();
        }
    }

    /**
     * The instance of the factory used to create the objects for this class.
     */
    private static final ParsedEquipmentFactory FACTORY =
        new ParsedEquipmentFactory();

    /**
     * The format string for the LUA version of this data type.
     */
    @SuppressWarnings("nls")
    private static final String LUA_FORMAT =
        "mainNPC:setEquipment(%1$s, %2$s);";

    /**
     * The item that is supposed to be placed in this slot.
     */
    private Items item;

    /**
     * The equipment slot the item is supposed to be placed in.
     */
    private EquipmentSlots slot;

    /**
     * Create a instance of the parsed equipment.
     */
    ParsedEquipment() {
        // nothing to do
    }

    /**
     * Get a old reused or a newly created instance of this class that stores
     * the set data.
     * 
     * @param itemSlot the slot the item is placed in
     * @param slotItem the item that is placed in the slot
     * @return the object that stores the data
     */
    public static ParsedEquipment getInstance(final EquipmentSlots itemSlot,
        final Items slotItem) {
        final ParsedEquipment result = FACTORY.object();
        result.setSlotItem(itemSlot, slotItem);
        return result;
    }

    /**
     * The equipment data does not effect the query.
     */
    @Override
    public void buildSQL(final SQLBuilder builder) {
        // nothing to add to the query
    }

    /**
     * Check if the current NPC stage is effected by this data entries. This
     * will return only <code>true</code> in case the stage is
     * {@link illarion.easynpc.writer.EasyNpcWriter.WritingStage#clothes}.
     */
    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return (stage == EasyNpcWriter.WritingStage.clothes);
    }

    /**
     * Check if the current stage is effected by the values stores in this
     * construct.
     */
    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return (stage == LuaWriter.WritingStage.clothes);
    }

    /**
     * The equipment of the character is a part of the base NPC. There are no
     * additional modules needed.
     */
    @Override
    public String[] getRequiredModules() {
        return null;
    }

    /**
     * Put the instance back into the factory for later reuse.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Reset this instance to it can be reused later.
     */
    @Override
    public void reset() {
        item = null;
        slot = null;
    }

    /**
     * Write the values stores in this equipment values to the easyNPC script.
     */
    @Override
    public void writeEasyNpc(final Writer target,
        final EasyNpcWriter.WritingStage stage) throws IOException {
        if (!effectsEasyNpcStage(stage)) {
            return;
        }

        switch (slot) {
            case chest:
                target.write("itemChest = "); //$NON-NLS-1$
                break;
            case coat:
                target.write("itemCoat = "); //$NON-NLS-1$
                break;
            case feet:
                target.write("itemShoes = "); //$NON-NLS-1$
                break;
            case hands:
                target.write("itemHands = "); //$NON-NLS-1$
                break;
            case head:
                target.write("itemHead = "); //$NON-NLS-1$
                break;
            case mainHand:
                target.write("itemMainHand = "); //$NON-NLS-1$
                break;
            case secondHand:
                target.write("itemSecondHand = "); //$NON-NLS-1$
                break;
            case trousers:
                target.write("itemTrousers = "); //$NON-NLS-1$
                break;
        }

        target.write(Integer.toString(item.getItemId()));
        target.write(illarion.easynpc.writer.EasyNpcWriter.NL);
    }

    /**
     * Write the LUA representation of this data to the LUA NPC script.
     */
    @Override
    public void writeLua(final Writer target,
        final LuaWriter.WritingStage stage) throws IOException {
        if (!effectsLuaWritingStage(stage)) {
            return;
        }

        target.write(String.format(LUA_FORMAT,
            Integer.toString(slot.getLuaId()),
            Integer.toString(item.getItemId())));
        target.write(illarion.easynpc.writer.LuaWriter.NL);
    }

    /**
     * Set the slot item stored in this instance.
     * 
     * @param itemSlot the slot the item is placed in
     * @param slotItem the item that is placed in the slot
     */
    private void setSlotItem(final EquipmentSlots itemSlot,
        final Items slotItem) {
        slot = itemSlot;
        item = slotItem;
    }

}
