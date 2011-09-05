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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import illarion.client.Debug;
import illarion.client.IllaClient;
import illarion.client.net.client.AbstractCommand;
import illarion.client.util.Lang;

import illarion.common.util.Location;

/**
 * The Sender class handles all data that is send from the client, encodes the
 * commands and prepares them for sending.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
final class Sender extends Thread implements NetCommWriter {
    /**
     * The XOR mask the command ID is masked with to decode the checking ID and
     * ensure that the start of a command was found.
     */
    private static final int COMMAND_XOR_MASK = 0xFF;

    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(Receiver.class);

    /**
     * The maximal size in bytes one command can use.
     */
    private static final int MAX_COMMAND_SIZE = 1000;

    /**
     * Length of the byte buffer used to store the data before its send to the
     * server.
     */
    private final ByteBuffer buffer = ByteBuffer
        .allocateDirect(MAX_COMMAND_SIZE);

    /**
     * The string encoder that is used to encode the strings before they are
     * send to the server.
     */
    private final CharsetEncoder encoder;

    /**
     * The buffer that is used to temporary store the decoded characters that
     * were send to the player.
     */
    private final CharBuffer encodingBuffer = CharBuffer.allocate(255);

    /**
     * The output stream of the socket connection to the server. The encoded
     * data is written on this stream to be send to the server.
     */
    private final WritableByteChannel outChannel;

    /**
     * The list that stores the commands that were not yet encoded.
     */
    private final BlockingQueue<AbstractCommand> queue;

    /**
     * Indicator if the Sender is currently running.
     */
    private boolean running;

    /**
     * The basic constructor for the sender that sets up all needed data.
     * 
     * @param outputQueue the list of yet not encoded server commands
     * @param out the output channel of the socket connection used to send the
     *            data to the server
     */
    @SuppressWarnings("nls")
    protected Sender(final BlockingQueue<AbstractCommand> outputQueue,
        final WritableByteChannel out) {
        super("Illarion output thread");

        queue = outputQueue;
        outChannel = out;

        encoder = NetComm.SERVER_STRING_ENCODING.newEncoder();

        setPriority(Thread.MIN_PRIORITY);
        setDaemon(true);
    }

    /**
     * The main loop the the server thread. Encodes the commands in the queue
     * and prepares them for sending to the server.
     * 
     * @see java.lang.Thread#run()
     */
    @SuppressWarnings("nls")
    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                // get first command form out queue

                AbstractCommand cmd;
                cmd = queue.take();

                buffer.clear();
                buffer.put((byte) cmd.getId());
                buffer.put((byte) (cmd.getId() ^ COMMAND_XOR_MASK));

                // keep some space for the length and the CRC
                final int headerLenCRC = buffer.position();
                buffer.putShort((short) 0);
                buffer.putShort((short) 0);

                final int startOfCmd = buffer.position();
                // encode command into net protocol
                cmd.encode(this);
                cmd.recycle();

                final int length = buffer.position() - startOfCmd;
                buffer.flip();
                buffer.position(startOfCmd);
                final int crc = NetComm.getCRC(buffer, length);
                buffer.position(headerLenCRC);
                buffer.putShort((short) length);
                buffer.putShort((short) crc);
                buffer.position(0);

                if (IllaClient.isDebug(Debug.net)) {
                    NetComm.dump("snd => ", buffer);
                    buffer.flip();
                }

                outChannel.write(buffer);
            }
        } catch (final Exception e) {
            LOGGER.fatal("General error within the sender", e);
            IllaClient.fallbackToLogin(Lang.getMsg("error.sender"));
        }
    }

    /**
     * Set of the sender is running or not. If this is set to false the sender
     * waits ready but does nothing.
     * 
     * @param newRunning the new state of the running flag
     */
    public void setRunning(final boolean newRunning) {
        running = newRunning;
        synchronized (queue) {
            queue.notify();
        }
    }

    /**
     * Write 1 byte as signed value to the network.
     * 
     * @param value the signed byte that shall be send to the server
     */
    @Override
    public void writeByte(final byte value) {
        buffer.put(value);
    }

    /**
     * Write 4 byte as signed value to the network.
     * 
     * @param value the signed integer that shall be send to the server
     */
    @Override
    public void writeInt(final int value) {
        buffer.putInt(value);
    }

    /**
     * Write a location to the network.
     * 
     * @param loc the location that shall be send to the server
     */
    @Override
    public void writeLocation(final Location loc) {
        buffer.putShort((short) loc.getScX());
        buffer.putShort((short) loc.getScY());
        buffer.putShort((short) loc.getScZ());
    }

    /**
     * Write 2 byte as signed value to the network.
     * 
     * @param value the signed short that shall be send to the server
     */
    @Override
    public void writeShort(final short value) {
        buffer.putShort(value);
    }

    /**
     * Write a string to the network. The length header of the string is written
     * automatically and its encoded to the correct CharSet automatically.
     * 
     * @param value the string that shall be send to the server
     */
    @Override
    public void writeString(final String value) {
        final int startIndex = buffer.position();
        buffer.putShort((short) 0);

        encodingBuffer.clear();
        encodingBuffer.put(value, 0, Math.min(1 << Short.SIZE, value.length()));
        encodingBuffer.flip();

        encoder.encode(encodingBuffer, buffer, true);
        final int lastIndex = buffer.position();
        buffer.position(startIndex);
        writeUShort(lastIndex - startIndex - 2);
        buffer.position(lastIndex);
    }

    /**
     * Write 1 byte as unsigned value to the network.
     * 
     * @param value the value that shall be send as unsigned byte
     */
    @Override
    public void writeUByte(final short value) {
        buffer.put((byte) (value % ((1 << Byte.SIZE) - 1)));
    }

    /**
     * Write 4 byte as unsigned value to the network.
     * 
     * @param value the value that shall be send as unsigned integer
     */
    @Override
    public void writeUInt(final long value) {
        buffer.putInt((int) (value % ((1L << Integer.SIZE) - 1)));
    }

    /**
     * Write 2 byte as unsigned value to the network.
     * 
     * @param value the value that shall be send as unsigned short
     */
    @Override
    public void writeUShort(final int value) {
        buffer.putShort((short) (value % ((1 << Short.SIZE) - 1)));
    }
}
