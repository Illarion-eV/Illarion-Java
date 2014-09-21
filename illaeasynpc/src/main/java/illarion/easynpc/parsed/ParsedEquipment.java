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
package illarion.easynpc.parsed;

import illarion.easynpc.data.EquipmentSlots;
import illarion.easynpc.data.Items;
import illarion.easynpc.writer.LuaRequireTable;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;

/**
 * This class stores the parsed equipment data that contains information about what the NPC wears.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedEquipment implements ParsedData {
    /**
     * The format string for the LUA version of this data type.
     */
    private static final String LUA_FORMAT = "mainNPC:setEquipment(%1$s, %2$s)";

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
    public ParsedEquipment(EquipmentSlots itemSlot, Items slotItem) {
        slot = itemSlot;
        item = slotItem;
    }

    /**
     * The equipment data does not effect the query.
     */
    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
        // nothing to add to the query
    }

    /**
     * Check if the current stage is effected by the values stores in this
     * construct.
     */
    @Override
    public boolean effectsLuaWritingStage(@Nonnull LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Clothes;
    }

    /**
     * The equipment of the character is a part of the base NPC. There are no
     * additional modules needed.
     */
    @Nonnull
    @Override
    public Collection<String> getRequiredModules() {
        return Collections.emptyList();
    }

    /**
     * Write the LUA representation of this data to the LUA NPC script.
     */
    @Override
    public void writeLua(
            @Nonnull Writer target, @Nonnull LuaRequireTable requires, @Nonnull LuaWriter.WritingStage stage) throws IOException {
        if (!effectsLuaWritingStage(stage)) {
            return;
        }

        target.write(String.format(LUA_FORMAT, Integer.toString(slot.getLuaId()), Integer.toString(item.getItemId())));
        target.write(LuaWriter.NL);
    }
}
