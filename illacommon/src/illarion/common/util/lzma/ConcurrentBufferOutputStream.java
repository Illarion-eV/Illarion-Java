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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * This is a output stream that is thread save and moves its data over a
 * blocking queue. Its save to use this output stream with threads.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
final class ConcurrentBufferOutputStream extends OutputStream {
    /**
     * The size of each byte buffer in the queue.
     */
    private static final int BUFSIZE = 16384;

    /**
     * The size of the queue.
     */
    private static final int QUEUESIZE = 4096;

    /**
     * The queue that is used to transfer the byte data.
     */
    private final ArrayBlockingQueue<byte[]> q;

    /**
     * Create a concurrent output stream that streams the data in the specified
     * queue.
     * 
     * @param queue the queue that is the core of this output stream
     */
    private ConcurrentBufferOutputStream(final ArrayBlockingQueue<byte[]> queue) {
        q = queue;
    }

    /**
     * Create a new concurrent output stream that uses the specified queue.
     * 
     * @param queue the queue to use
     * @return the created output stream
     */
    public static OutputStream create(final ArrayBlockingQueue<byte[]> queue) {
        OutputStream out = new ConcurrentBufferOutputStream(queue);
        out = new BufferedOutputStream(out, BUFSIZE);
        return out;
    }

    /**
     * Create a new queue for this concurrent output stream with the default
     * settings.
     * 
     * @return a new array blocking queue with the default settings for the use
     *         with this class
     */
    public static ArrayBlockingQueue<byte[]> newQueue() {
        return new ArrayBlockingQueue<byte[]>(QUEUESIZE);
    }

    /**
     * Close the stream transfer.
     */
    @Override
    public void close() throws IOException {
        final byte b[] = new byte[0]; // sentinel
        guarded_put(b);
    }

    /**
     * Write data to the stream.
     */
    @Override
    public void write(final byte[] b, final int off, final int len)
        throws IOException {
        final byte[] a = new byte[len];
        System.arraycopy(b, off, a, 0, len);
        guarded_put(a);
    }

    /**
     * Write a byte to the stream.
     */
    @Override
    public void write(final int i) throws IOException {
        final byte b[] = new byte[1];
        b[0] = (byte) (i & 0xFF);
        guarded_put(b);
    }

    /**
     * Put a byte array into the blocking queue.
     * 
     * @param a the array to put into the queue
     * @throws IOException in case the put operation fails
     */
    protected void guarded_put(final byte[] a) throws IOException {
        try {
            q.put(a);
        } catch (final InterruptedException exn) {
            throw new InterruptedIOException(exn.getMessage());
        }
    }
}
