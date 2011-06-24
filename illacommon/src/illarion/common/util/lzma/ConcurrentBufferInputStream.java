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
import java.io.InterruptedIOException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * The concurrent input stream that streams data for the use with different
 * threads.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
final class ConcurrentBufferInputStream extends InputStream {
    /**
     * The currently used byte buffer.
     */
    protected byte[] buf = null;

    /**
     * End of file was found.
     */
    protected boolean eof = false;

    /**
     * The pointer to the next entry in the buffer.
     */
    protected int next = 0;

    /**
     * The queue that stores the internal data.
     */
    protected ArrayBlockingQueue<byte[]> q;

    /**
     * Create a concurrent buffer input stream with a specified queue.
     * 
     * @param queue the queue the use
     */
    private ConcurrentBufferInputStream(final ArrayBlockingQueue<byte[]> queue) {
        q = queue;
        eof = false;
    }

    /**
     * Create a concurrent input stream that uses the specified queue.
     * 
     * @param queue the queue to use
     * @return the input stream that was created
     */
    public static InputStream create(final ArrayBlockingQueue<byte[]> queue) {
        final InputStream in = new ConcurrentBufferInputStream(queue);
        return in;
    }

    /**
     * Read one byte from the input stream.
     */
    @Override
    public int read() throws IOException {
        if (prepareAndCheckEOF()) {
            return -1;
        }
        final int x = buf[next];
        next++;
        return x & 0xff;
    }

    /**
     * Read a bulk of bytes from the input stream.
     */
    @Override
    public int read(final byte[] b, final int off, final int len)
        throws IOException {
        if (prepareAndCheckEOF()) {
            return -1;
        }
        int k = buf.length - next;
        if (len < k) {
            k = len;
        }
        System.arraycopy(buf, next, b, off, k);
        next += k;
        return k;
    }

    /**
     * Get the human readable form of this input stream.
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("cbIn@%x", Integer.valueOf(hashCode()));
    }

    /**
     * Take some new data from the queue.
     * 
     * @return the data taken from the queue
     * @throws IOException in case the take failed
     */
    protected byte[] guarded_take() throws IOException {
        try {
            return q.take();
        } catch (final InterruptedException exn) {
            throw new InterruptedIOException(exn.getMessage());
        }
    }

    /**
     * Check if the end of the file was reached.
     * 
     * @return <code>true</code> in case the end of the file was found
     * @throws IOException in case anything failed
     */
    protected boolean prepareAndCheckEOF() throws IOException {
        if (eof) {
            return true;
        }
        if ((buf == null) || (next >= buf.length)) {
            buf = guarded_take();
            next = 0;
            if (buf.length == 0) {
                eof = true;
                return true;
            }
        }
        return false;
    }
}
