/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.types;

import illarion.common.net.NetCommReader;
import illarion.common.net.NetCommWriter;

import java.io.IOException;

/**
 * This class is used to store the ID of a character
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharacterId {
    /**
     * The maximal value that is valid for the character ID
     */
    public static final long MAX_VALUE = Math.round(Math.pow(2, 32)) - 1L;

    /**
     * The minimal value that is valid for the character ID
     */
    public static final long MIN_VALUE = 0;

    /**
     * The item count.
     */
    private final long value;

    /**
     * Constructor of this class used to set.
     *
     * @param value the value of the character ID
     * @throws IllegalArgumentException in case the value is less then {@link #MIN_VALUE} or larger then
     *                                  {@link #MAX_VALUE}.
     */
    public CharacterId(final long value) {
        if ((value < MIN_VALUE) || (value > MAX_VALUE)) {
            throw new IllegalArgumentException("value is out of range.");
        }
        this.value = value;
    }

    /**
     * This constructor is used to decode the character ID from the network interface.
     *
     * @param reader the reader
     * @throws IOException in case the reading operation fails for some reason
     */
    public CharacterId(final NetCommReader reader) throws IOException {
        value = reader.readUInt();
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) || ((obj instanceof CharacterId) && equals((CharacterId) obj));
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {
        return "character ID: " + Long.toString(value);
    }

    /**
     * Encode the value of the item count to the network interface.
     *
     * @param writer the writer that receives the value
     */
    public void encode(final NetCommWriter writer) {
        writer.writeUInt(value);
    }

    /**
     * Check if two item count instances are equal.
     *
     * @param obj the second instance to check
     * @return {@code true} in case both instances represent the same value
     */
    public boolean equals(final CharacterId obj) {
        return value == obj.value;
    }

    /**
     * Get the value of the item count.
     *
     * @return the item count value
     */
    public long getValue() {
        return value;
    }
}
