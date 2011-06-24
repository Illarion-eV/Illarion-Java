/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util.lzma;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

/**
 * This input stream allows the usage of the LZMA API using the default java
 * stream scheme.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class LzmaInputStream extends FilterInputStream {
    /**
     * The decoder that takes care for decoding.
     */
    protected final Future<Boolean> decoderTask;

    /**
     * Construct a LZMA input stream that takes its resources from another input
     * stream. The data from the input stream will be filtered by this one.
     * 
     * @param _in the source input stream
     */
    public LzmaInputStream(final InputStream _in) {
        super(null);
        final DecoderTask dth = new DecoderTask(_in);
        in = ConcurrentBufferInputStream.create(dth.q);
        decoderTask = LzmaLoadManager.getInstance().addTask(dth);
    }

    /**
     * Read the next byte from the stream.
     */
    @Override
    public int read() throws IOException {
        final int k = in.read();

        // if (decoderTask.isDone()) {
        // throw new IOException();
        // }

        return k;
    }

    /**
     * Read a bulk of data from the stream.
     */
    @Override
    public int read(final byte[] b, final int off, final int len)
        throws IOException {
        final int k = in.read(b, off, len);

        // if (decoderTask.isDone()) {
        // throw new IOException();
        // }

        return k;
    }

    /**
     * Get the string representation of this input stream.
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("lzmaIn@%x", Integer.valueOf(hashCode()));
    }
}
