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

import SevenZip.Compression.LZMA.Encoder;

/**
 * The encoder task is taking care for the LZMA compression of the data that are
 * send input the LzmaOutputStream. This task is supposed to be executed in a
 * additional thread.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
final class EncoderTask implements Callable<Boolean> {
    /**
     * The default dictionary size that is used in case the output stream does
     * not set this value properly.
     */
    public static final int DEFAULT_DICT_SZ_POW2 = 15;

    /**
     * The LZMA encoder that handles the compression.
     */
    protected final Encoder enc;

    /**
     * The input stream that is internal used to manage the data.
     */
    protected final InputStream in;

    /**
     * The output stream that receives the data.
     */
    protected final OutputStream out;

    /**
     * The data queue of yet unused data.
     */
    protected final ArrayBlockingQueue<byte[]> q;

    /**
     * Create the encoder thread that will take care for encoding the data to
     * the output stream.
     * 
     * @param _out the stream to receive the encoded data
     * @param dictSzPow2 the size of the dictionary power 2
     * @param fastBytes the amount of fast bytes applied
     */
    EncoderTask(final OutputStream _out, final int dictSzPow2,
        final int fastBytes) {
        q = ConcurrentBufferOutputStream.newQueue();
        in = ConcurrentBufferInputStream.create(q);
        out = _out;
        enc = new Encoder();
        enc.SetDictionarySize(1 << (dictSzPow2 < 0 ? DEFAULT_DICT_SZ_POW2
            : dictSzPow2));
        if (fastBytes < 0) {
            enc.SetNumFastBytes(fastBytes);
        }
    }

    /**
     * The main function of this task. It will take care to compress the data.
     */
    @Override
    public Boolean call() throws IOException {
        enc.SetEndMarkerMode(true);
        enc.Code(in, out, -1, -1, null);
        out.close();
        return Boolean.TRUE;
    }

    /**
     * The human readable representation of this class.
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Enc@%x", Integer.valueOf(hashCode()));
    }
}
