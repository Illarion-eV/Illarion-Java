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
package illarion.easynpc.data;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nonnull;

/**
 * This enumerator stores all possible slots for equipment used in the easyNPC
 * scripts.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum EquipmentSlots {
    /**
     * The chest slot. A place for shirts and armors.
     */
    chest(3),

    /**
     * The coat slots for... coats.
     */
    coat(11),

    /**
     * Feet slot for shoes and boots.
     */
    feet(10),

    /**
     * Hands slot for gloves.
     */
    hands(4),

    /**
     * The head slot. Helmets and heads have a place there.
     */
    head(1),

    /**
     * Main hand for tools and swords.
     */
    mainHand(5),

    /**
     * Second hand for shields and arrows.
     */
    secondHand(6),

    /**
     * Trousers slot for trousers and grieves.
     */
    trousers(9);

    /**
     * The ID for each slot in a lua script.
     */
    private final int slotId;

    /**
     * Constructor for the equipment slot that stores the ID needed in LUA.
     *
     * @param id the Id of the slot
     */
    EquipmentSlots(final int id) {
        slotId = id;
    }

    /**
     * Get the LUA id of the slot.
     *
     * @return the LUA id of this slot
     */
    public int getLuaId() {
        return slotId;
    }

    /**
     * Add this values to the highlighted tokens.
     *
     * @param map the map that stores the tokens
     */
    public static void enlistHighlightedWords(@Nonnull final TokenMap map) {
        for (EquipmentSlots slot : EquipmentSlots.values()) {
            map.put(slot.name(), Token.VARIABLE);
        }
    }
}
