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
package illarion.common.net;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * This interface offers the possibility to read from a connection handled by the network communication class of
 * Illarion.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface NetCommReader {
    /**
     * Read a single byte from the buffer and handle it as signed byte.
     *
     * @return The byte from the buffer handled as signed byte
     * @throws IOException If there are more byte read then there are written in the buffer
     */
    byte readByte() throws IOException;

    /**
     * Read four bytes from the buffer and handle them as a single signed value.
     *
     * @return The two bytes in the buffer handled as signed 4 byte value
     * @throws IOException If there are more byte read then there are written in the buffer
     */
    int readInt() throws IOException;

    /**
     * Read two bytes from the buffer and handle them as a single signed value.
     *
     * @return The two bytes in the buffer handled as signed 2 byte value
     * @throws IOException If there are more byte read then there are written in the buffer
     */
    short readShort() throws IOException;

    /**
     * Read a string from the input buffer and encode it for further usage.
     *
     * @return the decoded string
     * @throws IOException If there are more byte read then there are written in the buffer
     */
    @Nonnull
    String readString() throws IOException;

    /**
     * Read a single byte from the buffer and handle it as unsigned byte.
     *
     * @return The byte of the buffer handled as unsigned byte.
     * @throws IOException If there are more byte read then there are written in the buffer
     */
    short readUByte() throws IOException;

    /**
     * Read four bytes from the buffer and handle them as a single unsigned
     * value.
     *
     * @return The two bytes in the buffer handled as unsigned 4 byte value
     * @throws IOException If there are more byte read then there are written in the buffer
     */
    long readUInt() throws IOException;

    /**
     * Read two bytes from the buffer and handle them as a single unsigned
     * value.
     *
     * @return The two bytes in the buffer handled as unsigned 2 byte value
     * @throws IOException If there are more byte read then there are written in the buffer
     */
    int readUShort() throws IOException;
}
