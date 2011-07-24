/*
 * This file is part of the Illarion Download Manager.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Download Manager is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Download Manager is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Download Manager. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.tasks.unpack;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This filter stream simply counts the amount of bytes send past it.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class CountBytesInputStream extends FilterInputStream {
    /**
     * This interface is used for the monitors for this stream class.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    public static interface Callback {
        /**
         * This function is called to notify the monitor about the new value of
         * the stream.
         * 
         * @param newPos the new stream position
         */
        void reportUpdate(final long newPos);
    }

    /**
     * This helper class is used as multiplexer for the callback. In case more
     * then one callback is added to this stream, those multiplexers are used to
     * chain the callback classes.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.00
     */
    private static final class CallbackMultiplex implements Callback {
        /**
         * The first callback.
         */
        private final Callback cb1;

        /**
         * The second callback.
         */
        private final Callback cb2;

        /**
         * The public constructor used so the parent class is able to create
         * instances of this function. It is also used to set the two callback
         * classes this multiplexer multiplexes to.
         * 
         * @param callback1 the first callback
         * @param callback2 the second callback
         */
        public CallbackMultiplex(final Callback callback1,
            final Callback callback2) {
            cb1 = callback1;
            cb2 = callback2;
        }

        @Override
        public void reportUpdate(final long newPos) {
            cb1.reportUpdate(newPos);
            cb2.reportUpdate(newPos);
        }
    }

    /**
     * The callback that is informed about on this stream.
     */
    private Callback callback;

    /**
     * The update interval of the callback classes. The amount of bytes set here
     * has to pass the stream before the callback is updated.
     */
    private long callbackInterval;

    /**
     * This variable stores when the last update of the callback functions
     * happened.
     */
    private long lastCallbackUpdate;

    /**
     * The current location in the file.
     */
    private long position;

    /**
     * Create this filter stream to measure the amount of bytes moving past this
     * stream.
     * 
     * @param in the next input stream
     */
    @SuppressWarnings("hiding")
    public CountBytesInputStream(final InputStream in) {
        super(in);
        position = 0L;
        callbackInterval = 1024L;
        lastCallbackUpdate = 0L;
    }

    /**
     * Add a callback class to this stream. This callback class will be informed
     * about updates on a regular base.
     * 
     * @param newCallback the callback class to add
     */
    public void addCallback(final Callback newCallback) {
        if (callback == null) {
            callback = newCallback;
        } else {
            callback = new CallbackMultiplex(callback, newCallback);
        }
    }

    /**
     * Get the position inside the stream. This is actually the count of bytes
     * that were past though this stream.
     * 
     * @return the amount of bytes that passed this stream
     */
    public long getPosition() {
        return position;
    }

    @Override
    public int read() throws IOException {
        final int temp = super.read();
        if (temp != -1) {
            updatePosition(1L);
        }
        return temp;
    }

    @Override
    public int read(final byte b[]) throws IOException {
        final int temp = super.read(b);
        if (temp != -1) {
            updatePosition(temp);
        }
        return temp;
    }

    @Override
    public int read(final byte b[], final int off, final int len)
        throws IOException {
        final int temp = super.read(b, off, len);
        if (temp != -1) {
            updatePosition(temp);
        }
        return temp;
    }

    /**
     * Set the byte interval that is used to update the callback classes.
     * 
     * @param interval the new byte interval
     */
    @SuppressWarnings("nls")
    public void setCallbackInterval(final long interval) {
        if (interval < 1) {
            throw new IllegalArgumentException("Interval has to be > 0");
        }
        callbackInterval = interval;
    }

    @Override
    public long skip(final long n) throws IOException {
        final long temp = in.skip(n);
        if (temp > -1) {
            updatePosition(temp);
        }
        return temp;
    }

    /**
     * This function is used to update the current position value. It will also
     * trigger the notification of the callback monitors in case there are any
     * and a update is needed.
     * 
     * @param value the value the stream changes by
     */
    private void updatePosition(final long value) {
        position += value;

        if (position > lastCallbackUpdate) {
            if (callback != null) {
                try {
                    callback.reportUpdate(position);
                } catch (final Exception ex) {
                    // nothing crashes me!!
                }
            }
            while (position > lastCallbackUpdate) {
                lastCallbackUpdate += callbackInterval;
            }
        }
    }
}
