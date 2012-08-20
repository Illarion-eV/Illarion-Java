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
package illarion.easynpc.parsed;

import illarion.easynpc.data.EquipmentSlots;
import illarion.easynpc.data.Items;
import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import java.io.IOException;
import java.io.Writer;

/**
 * This class stores the parsed equipment data that contains information about what the NPC wears.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedEquipment implements ParsedData {
    /**
     * The format string for the LUA version of this data type.
     */
    @SuppressWarnings("nls")
    private static final String LUA_FORMAT = "mainNPC:setEquipment(%1$s, %2$s);";

    /**
     * The item that is supposed to be placed in this slot.
     */
    private final Items item;

    /**
     * The equipment slot the item is supposed to be placed in.
     */
    private final EquipmentSlots slot;

    /**
     * Create a instance of the parsed equipment.
     *
     * @param itemSlot the slot the item is placed in
     * @param slotItem the item that is placed in the slot
     */
    public ParsedEquipment(final EquipmentSlots itemSlot, final Items slotItem) {
        slot = itemSlot;
        item = slotItem;
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
     * {@link EasyNpcWriter.WritingStage#clothes}.
     */
    @Override
    public boolean effectsEasyNpcStage(final EasyNpcWriter.WritingStage stage) {
        return stage == EasyNpcWriter.WritingStage.clothes;
    }

    /**
     * Check if the current stage is effected by the values stores in this
     * construct.
     */
    @Override
    public boolean effectsLuaWritingStage(final LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Clothes;
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
        target.write(EasyNpcWriter.NL);
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

        target.write(String.format(LUA_FORMAT, Integer.toString(slot.getLuaId()), Integer.toString(item.getItemId())));
        target.write(LuaWriter.NL);
    }
}
