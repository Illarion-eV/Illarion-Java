/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net;

import illarion.common.util.Location;

/**
 * This interface offers the possibility to write on a connection handled by the
 * network communication class of Illarion.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface NetCommWriter {
    /**
     * Write 1 byte as signed value to the network.
     * 
     * @param value the signed byte that shall be send to the server
     */
    void writeByte(byte value);

    /**
     * Write 4 byte as signed value to the network.
     * 
     * @param value the signed integer that shall be send to the server
     */
    void writeInt(int value);

    /**
     * Write a location to the network.
     * 
     * @param loc the location that shall be send to the server
     */
    void writeLocation(Location loc);

    /**
     * Write 2 byte as signed value to the network.
     * 
     * @param value the signed short that shall be send to the server
     */
    void writeShort(short value);

    /**
     * Write a string to the network.
     * 
     * @param value the string that shall be send to the server
     */
    void writeString(String value);

    /**
     * Write 1 byte as unsigned value to the network.
     * 
     * @param value the value that shall be send as unsigned byte
     */
    void writeUByte(short value);

    /**
     * Write 4 byte as unsigned value to the network.
     * 
     * @param value the value that shall be send as unsigned integer
     */
    void writeUInt(long value);

    /**
     * Write 2 byte as unsigned value to the network.
     * 
     * @param value the value that shall be send as unsigned short
     */
    void writeUShort(int value);
}
