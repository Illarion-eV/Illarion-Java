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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import illarion.client.Debug;
import illarion.client.IllaClient;
import illarion.client.net.server.AbstractReply;
import illarion.client.util.Lang;

/**
 * The Receiver class handles all data that is send from the server, decodes the
 * messages and prepares them for execution.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
final class Receiver extends Thread implements NetCommReader {
    /**
     * Length of the byte buffer used to store the data from the server.
     */
    private static final int BUFFER_LENGTH = 10000;

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
     * Time the receiver waits for more data before throwing away the incomplete
     * things it already got.
     */
    private static final int RECEIVER_TIMEOUT = 1000;

    /**
     * The buffer that stores the byte that we received from the server for
     * decoding.
     */
    private final ByteBuffer buffer;

    /**
     * The decoder that is used to decode the strings that are send to the
     * client by the server.
     */
    private final CharsetDecoder decoder;

    /**
     * The buffer that is used to temporary store the decoded characters that
     * were send to the player.
     */
    private final CharBuffer decodingBuffer = CharBuffer.allocate(255);

    /**
     * The input stream of the connection socket of the connection to the
     * server.
     */
    private final ReadableByteChannel inChannel;

    /**
     * The list that stores the commands there were decoded and prepared for the
     * NetComm for execution.
     */
    private final BlockingQueue<AbstractReply> queue;

    /**
     * Indicator if the Receiver is currently running.
     */
    private boolean running;

    /**
     * The time until a timeout occurs.
     */
    private long timeOut = 0;

    /**
     * The basic constructor for the receiver that sets up all needed data.
     * 
     * @param inputQueue the list of decoded server messages that need to be
     *            executed by NetComm
     * @param in the input stream of the socket connection to the server that
     *            contains the data that needs to be decoded
     */
    @SuppressWarnings("nls")
    public Receiver(final BlockingQueue<AbstractReply> inputQueue,
        final ReadableByteChannel in) {
        super("Illarion input thread");

        queue = inputQueue;
        inChannel = in;

        buffer = ByteBuffer.allocateDirect(BUFFER_LENGTH);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.limit(0);

        decoder = NetComm.SERVER_STRING_ENCODING.newDecoder();

        setPriority(Thread.MIN_PRIORITY);
        setDaemon(true);
    }

    /**
     * Read a single byte from the buffer and handle it as signed byte.
     * 
     * @return The byte from the buffer handled as signed byte
     * @throws IOException If there are more byte read then there are written in
     *             the buffer
     */
    @Override
    public byte readByte() throws IOException {
        return buffer.get();
    }

    /**
     * Read four bytes from the buffer and handle them as a single signed value.
     * 
     * @return The two bytes in the buffer handled as signed 4 byte value
     * @throws IOException If there are more byte read then there are written in
     *             the buffer
     */
    @Override
    public int readInt() throws IOException {
        return buffer.getInt();
    }

    /**
     * Read two bytes from the buffer and handle them as a single signed value.
     * 
     * @return The two bytes in the buffer handled as signed 2 byte value
     * @throws IOException If there are more byte read then there are written in
     *             the buffer
     */
    @Override
    public short readShort() throws IOException {
        return buffer.getShort();
    }

    /**
     * Read a string from the input buffer and encode it for further usage.
     * 
     * @return the decoded string
     * @throws IOException If there are more byte read then there are written in
     *             the buffer
     */
    @Override
    @SuppressWarnings("nls")
    public String readString() throws IOException {
        final int len = readUShort();

        if (len > buffer.remaining()) {
            throw new IndexOutOfBoundsException(
                "reading beyond receive buffer " + (buffer.remaining() + len));
        }
        decodingBuffer.clear();
        final int lastLimit = buffer.limit();
        buffer.limit(buffer.position() + len);
        decoder.decode(buffer, decodingBuffer, false);
        buffer.limit(lastLimit);
        decodingBuffer.flip();

        return decodingBuffer.toString();
    }

    /**
     * Read a single byte from the buffer and handle it as unsigned byte.
     * 
     * @return The byte of the buffer handled as unsigned byte.
     * @throws IOException If there are more byte read then there are written in
     *             the buffer
     */
    @Override
    public short readUByte() throws IOException {
        final short data = readByte();
        if (data < 0) {
            return (short) (data + (1 << Byte.SIZE));
        }
        return data;
    }

    /**
     * Read four bytes from the buffer and handle them as a single unsigned
     * value.
     * 
     * @return The two bytes in the buffer handled as unsigned 4 byte value
     * @throws IOException If there are more byte read then there are written in
     *             the buffer
     */
    @Override
    public long readUInt() throws IOException {
        final long data = readInt();
        if (data < 0) {
            return data + (1L << Integer.SIZE);
        }
        return data;
    }

    /**
     * Read two bytes from the buffer and handle them as a single unsigned
     * value.
     * 
     * @return The two bytes in the buffer handled as unsigned 2 byte value
     * @throws IOException If there are more byte read then there are written in
     *             the buffer
     */
    @Override
    public int readUShort() throws IOException {
        final int data = readShort();
        if (data < 0) {
            return data + (1 << Short.SIZE);
        }
        return data;
    }

    /**
     * The main loop the the receiver thread. Decodes the data of the input
     * stream and places the server messages in the queue.
     * <p>
     * The decoding of the data happens as instantly as soon as a command is
     * completely read from the input stream. Searching the start of a command
     * is done by looking for a valid ID with a valid XOR id right behind.
     * </p>
     */
    @SuppressWarnings("nls")
    @Override
    public void run() {
        running = true;
        int minRequiredData = CommandList.HEADER_SIZE;

        while (running) {
            try {
                while (running && receiveData(minRequiredData)) {
                    while (true) {
                        // wait for a complete message header
                        if (buffer.remaining() < CommandList.HEADER_SIZE) {
                            break;
                        }

                        // identify command
                        final int id = readUByte();
                        final int xor = readUByte();

                        // valid command id
                        if (id != (xor ^ COMMAND_XOR_MASK)) {
                            // delete only first byte from buffer, scanning for
                            // valid command
                            buffer.position(1);
                            buffer.compact();

                            LOGGER.warn("Skipping invalid data [" + id + "]");

                            continue;
                        }

                        // read length and CRC
                        final int len = readUShort();
                        final int crc = readUShort();

                        // wait for complete data
                        if (!dataComplete(len)) {
                            // scroll the cursor back and wait for more.
                            buffer.position(0);
                            minRequiredData = len + CommandList.HEADER_SIZE;
                            break;
                        }

                        minRequiredData = CommandList.HEADER_SIZE;

                        // check CRC
                        if (crc != NetComm.getCRC(buffer, len)) {
                            final int oldLimit = buffer.limit();
                            buffer.limit(len + CommandList.HEADER_SIZE);
                            buffer.position(CommandList.HEADER_SIZE);
                            NetComm.dump("Invalid CRC ", buffer);

                            buffer.position(1);
                            buffer.limit(oldLimit);
                            buffer.compact();
                            buffer.flip();
                            continue;
                        }

                        // decode
                        try {
                            final AbstractReply rpl =
                                ReplyFactory.getInstance().getCommand(id);

                            // explicitly set id for mapped commands
                            rpl.activate(id);
                            rpl.decode(this);

                            if (IllaClient.isDebug(Debug.protocol)) {
                                LOGGER.debug("REC: " + rpl.toString());
                            }

                            // put decoded command in input queue
                            queue.put(rpl);
                        } catch (final IllegalArgumentException ex) {
                            LOGGER.error("Invalid command id received "
                                + Integer.toHexString(id));
                        }

                        buffer.compact();
                        buffer.flip();
                    }
                }
            } catch (final IOException e) {
                if (running) {
                    LOGGER.fatal("The connection to the server is not"
                        + " working anymore.", e);
                    IllaClient.fallbackToLogin(Lang.getMsg("error.receiver"));
                    return;
                }
            } catch (final Exception e) {
                if (running) {
                    LOGGER.fatal("General error in the receiver", e);
                    IllaClient.fallbackToLogin(Lang.getMsg("error.receiver"));
                    return;
                }
            }
        }
    }

    /**
     * Set of the receiver is running or not. If this is set to false the
     * receiver waits ready but does nothing.
     * 
     * @param newRunning the new state of the running flag
     */
    public void setRunning(final boolean newRunning) {
        running = newRunning;
    }

    /**
     * This function checks of the received data contains a complete command.
     * 
     * @param len the amount of bytes that were received for that command
     * @return true in case the command is complete, false if not
     */
    @SuppressWarnings("nls")
    private boolean dataComplete(final int len) {
        if (len <= buffer.remaining()) {
            timeOut = 0;
            return true;
        }

        // set timeout for data
        if (timeOut == 0) {
            timeOut = System.currentTimeMillis() + RECEIVER_TIMEOUT;
            LOGGER.warn("Waiting for missing data "
                + ((CommandList.HEADER_SIZE + len) - buffer.remaining()));
        }

        // timeout exceeded
        if (System.currentTimeMillis() > timeOut) {
            NetComm.dump("Receiver timeout. Skipping ", buffer);
            buffer.clear();
            buffer.limit(0);
        } else { // still waiting
            buffer.position(0);
        }

        return false;
    }

    /**
     * Read data from the input stream of the socket and store it in the buffer.
     * 
     * @param neededDataInBuffer The data that is needed at least before the
     *            method has to return in order to parse the values correctly
     * @return true in case there is any data to be decoded in the buffer
     * @throws IOException In case there is something wrong with the input
     *             stream
     */
    @SuppressWarnings("nls")
    private boolean receiveData(final int neededDataInBuffer)
        throws IOException {

        int data = buffer.remaining();
        int newData = 0;

        final int appPos = buffer.limit();
        buffer.clear();
        buffer.position(appPos);

        while (true) {
            if (inChannel.isOpen()) {
                newData = inChannel.read(buffer);
            }
            data += newData;
            if (data >= neededDataInBuffer) {
                break;
            }
            try {
                Thread.sleep(2);
            } catch (final InterruptedException e) {
                LOGGER.warn("Interrupted wait time for new data");
            }
        }

        buffer.flip();

        if ((newData > 0) && IllaClient.isDebug(Debug.net)) {
            buffer.position(appPos);
            NetComm.dump("rcv <= ", buffer);
            buffer.position(0);
        }

        return buffer.hasRemaining();
    }

}
