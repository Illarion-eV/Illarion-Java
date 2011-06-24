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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import SevenZip.Compression.LZMA.Decoder;

/**
 * The decoder task is taking care for decompressing LZMA data from a input
 * stream. This task is meant to run is a thread.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
final class DecoderTask implements Callable<Boolean> {
    /**
     * The properties applied to the decoder.
     */
    static final byte[] props;

    static {
        props = new byte[5];

        props[0] = 0x5d;
        props[1] = 0x00;
        props[2] = 0x00;
        props[3] = 0x10;
        props[4] = 0x00;
    }

    /**
     * The decoder that is used to decode the LZMA data.
     */
    protected final Decoder dec;

    /**
     * The input stream that is the source of the data.
     */
    protected final InputStream in;

    /**
     * The output stream that is used for internal handling.
     */
    protected OutputStream out;

    /**
     * The byte data queue that contains the block of data yet not handled.
     */
    protected ArrayBlockingQueue<byte[]> q;

    /**
     * Create a instance of the decoder task that takes care for decoding a
     * input stream.
     * 
     * @param _in the input stream that is decoded
     */
    DecoderTask(final InputStream _in) {
        q = ConcurrentBufferOutputStream.newQueue();
        in = _in;
        out = ConcurrentBufferOutputStream.create(q);
        dec = new Decoder();
    }

    /**
     * This is the main routine of this task. It will take care for decoding the
     * LZMA data.
     */
    @Override
    public Boolean call() throws IOException {
        try {
            final long outSize = -1;
            dec.SetDecoderProperties(props);
            dec.Code(in, out, outSize);
        } finally {
            out.close();
        }

        return Boolean.TRUE;
    }

    /**
     * Get a human readable representation of this task.
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Dec@%x", Integer.valueOf(hashCode()));
    }
}
