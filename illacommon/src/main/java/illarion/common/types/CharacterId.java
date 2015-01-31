/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.common.types;

import illarion.common.net.NetCommReader;
import illarion.common.net.NetCommWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class is used to store the ID of a character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public final class CharacterId implements Serializable, Comparable<CharacterId> {
    /**
     * The maximal value that is valid for the character ID
     */
    public static final long MAX_VALUE = (1L << 32) - 1L;

    /**
     * This is the mask used to find out the type of the character that is represented by this ID.
     */
    private static final int TYPE_MASK = 0xFF000000;

    /**
     * In case the character ID fits this mask, the character is a NPC.
     */
    private static final int NPC_MASK = 0xFF000000;

    /**
     * In case the character ID fits this mask, the character is a monster.
     */
    private static final int MONSTER_MASK = 0xFE000000;

    /**
     * The minimal value that is valid for the character ID
     */
    public static final long MIN_VALUE = 0;

    /**
     * The item count.
     */
    private final int value;

    /**
     * Constructor of this class used to set.
     *
     * @param value the value of the character ID
     */
    public CharacterId(int value) {
        this.value = value;
    }

    /**
     * This constructor is used to decode the character ID from the network interface.
     *
     * @param reader the reader
     * @throws IOException in case the reading operation fails for some reason
     */
    public CharacterId(@Nonnull NetCommReader reader) throws IOException {
        this(reader.readInt());
    }

    /**
     * Check if this character ID is the ID of a NPC.
     *
     * @return {@code true} in case this ID marks a NPC
     */
    public boolean isNPC() {
        return (value & TYPE_MASK) == NPC_MASK;
    }

    /**
     * Check if this character ID is the ID of a monster.
     *
     * @return {@code true} in case this ID marks a monster
     */
    public boolean isMonster() {
        return (value & TYPE_MASK) == MONSTER_MASK;
    }

    /**
     * Check if this character ID is the ID of a human controlled character.
     *
     * @return {@code true} in case this ID marks a human controlled character
     */
    public boolean isHuman() {
        return !isNPC() && !isMonster();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj) || ((obj instanceof CharacterId) && equals((CharacterId) obj));
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Nonnull
    @Override
    public String toString() {
        return "character ID: " + Long.toString(getValue());
    }

    /**
     * Encode the value of the item count to the network interface.
     *
     * @param writer the writer that receives the value
     */
    public void encode(@Nonnull NetCommWriter writer) {
        writer.writeInt(value);
    }

    /**
     * Check if two item count instances are equal.
     *
     * @param obj the second instance to check
     * @return {@code true} in case both instances represent the same value
     */
    public boolean equals(@Nullable CharacterId obj) {
        return (obj != null) && (value == obj.value);
    }

    /**
     * Get the value of the item count.
     *
     * @return the item count value
     */
    public long getValue() {
        if (value < 0) {
            return value + (1L << Integer.SIZE);
        }
        return value;
    }

    public int getAsInteger() {
        return value;
    }

    @Override
    public int compareTo(@Nonnull CharacterId o) {
        if (value == o.value) {
            return 0;
        }
        if (getValue() < o.getValue()) {
            return -1;
        }
        return 1;
    }
}
