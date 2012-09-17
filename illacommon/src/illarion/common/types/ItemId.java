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
 * This class is used to store the ID of a item.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ItemId {
    /**
     * The maximal value that is valid for the item ID.
     */
    public static final int MAX_VALUE = (1 << 16) - 1;

    /**
     * The minimal value that is valid for the item ID.
     */
    public static final int MIN_VALUE = 0;

    /**
     * The item count.
     */
    private final int value;

    /**
     * Constructor of this class used to set.
     *
     * @param value the value of the item ID
     * @throws IllegalArgumentException in case the value is less then {@link #MIN_VALUE} or larger then
     *                                  {@link #MAX_VALUE}.
     */
    public ItemId(final int value) {
        if ((value < MIN_VALUE) || (value > MAX_VALUE)) {
            throw new IllegalArgumentException("value is out of range.");
        }
        this.value = value;
    }

    /**
     * This constructor is used to decode the item ID from the network interface.
     *
     * @param reader the reader
     * @throws IOException in case the reading operation fails for some reason
     */
    public ItemId(final NetCommReader reader) throws IOException {
        value = reader.readUShort();
    }

    /**
     * Check if the ID is valid for a item. This means the ID has to be not {@code null} and its value has to be
     * greater then {@code 0}.
     *
     * @param id the ID to test
     * @return {@code true} in case the id is valid
     */
    public static boolean isValidItem(final ItemId id) {
        return (id != null) && (id.getValue() > 0);
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) || ((obj instanceof ItemId) && equals((ItemId) obj));
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "Item ID: " + Integer.toString(value);
    }

    /**
     * Encode the value of the item ID to the network interface.
     *
     * @param writer the writer that receives the value
     */
    public void encode(final NetCommWriter writer) {
        writer.writeUShort(value);
    }

    /**
     * Check if two item count instances are equal.
     *
     * @param obj the second instance to check
     * @return {@code true} in case both instances represent the same value
     */
    public boolean equals(final ItemId obj) {
        return value == obj.value;
    }

    /**
     * Get the value of the item count.
     *
     * @return the item count value
     */
    public int getValue() {
        return value;
    }
}
