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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This is the LZMA output stream that allows to create LZMA compressed files.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class LzmaOutputStream extends FilterOutputStream {
    /**
     * The future of the encoding task and the method to read what this task is
     * doing.
     */
    private final Future<Boolean> encoderTask;

    /**
     * Create a LZMA output stream that filters the data and forwards it to the
     * output stream that is set as parameter. The created instance will use the
     * default dictionary size and no fast bytes.
     * 
     * @param _out the output stream that receives the filtered data
     */
    public LzmaOutputStream(final OutputStream _out) {
        this(_out, EncoderTask.DEFAULT_DICT_SZ_POW2);
    }

    /**
     * Create a LZMA output stream that filters the data and forwards it to the
     * output stream that is set as parameter. The created instance will use no
     * fast bytes.
     * 
     * @param _out the output stream that receives the filtered data
     * @param dictSzPow2 the size of the dictionary, power 2
     */
    public LzmaOutputStream(final OutputStream _out, final int dictSzPow2) {
        this(_out, dictSzPow2, 3);
    }

    /**
     * Create a LZMA output stream that filters the data and forwards it to the
     * output stream that is set as parameter.
     * 
     * @param _out the output stream that receives the filtered data
     * @param dictSzPow2 the size of the dictionary, power 2
     * @param fastBytes the amount of fast bytes used to compress the data
     */
    public LzmaOutputStream(final OutputStream _out, final int dictSzPow2,
        final int fastBytes) {
        super(null);
        final EncoderTask eth = new EncoderTask(_out, dictSzPow2, fastBytes);
        out = ConcurrentBufferOutputStream.create(eth.q);
        encoderTask = LzmaLoadManager.getInstance().addTask(eth);
    }

    /**
     * Close the stream.
     */
    @Override
    public void close() throws IOException {
        out.close();
        boolean result;
        try {
            result = encoderTask.get().booleanValue();
        } catch (final InterruptedException e) {
            throw new IOException(e);
        } catch (final ExecutionException e) {
            throw new IOException(e);
        }

        if (!result) {
            throw new IOException();
        }
    }

    /**
     * Get the string representation of this input stream.
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("lzmaOut@%x", Integer.valueOf(hashCode()));
    }

    /**
     * Write one byte to this stream.
     */
    @Override
    public void write(final int i) throws IOException {
        if (encoderTask.isDone()) {
            throw new IOException();
        }
        out.write(i);
    }
}
