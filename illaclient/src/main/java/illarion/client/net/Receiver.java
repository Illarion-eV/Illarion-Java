/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
package illarion.client.net;

import illarion.client.IllaClient;
import illarion.client.net.server.ServerReply;
import illarion.client.util.Lang;
import illarion.common.net.NetCommReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharsetDecoder;

/**
 * The Receiver class handles all data that is send from the server, decodes the messages and prepares them for
 * execution.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@NotThreadSafe
final class Receiver extends Thread implements NetCommReader {
    /**
     * Length of the byte buffer used to store the data from the server.
     */
    private static final int INITIAL_BUFFER_SIZE = 1000;

    /**
     * The XOR mask the command ID is masked with to decode the checking ID and ensure that the start of a command
     * was found.
     */
    private static final int COMMAND_XOR_MASK = 0xFF;

    /**
     * The instance of the logger that is used to write out the data.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(Receiver.class);

    /**
     * Time the receiver waits for more data before throwing away the incomplete things it already got.
     */
    private static final int RECEIVER_TIMEOUT = 1000;
    /**
     * The decoder that is used to decode the strings that are send to the client by the server.
     */
    @Nonnull
    private final CharsetDecoder decoder;
    /**
     * The buffer that is used to temporary store the decoded characters that were send to the player.
     */
    @Nonnull
    private final CharBuffer decodingBuffer = CharBuffer.allocate(65535);
    /**
     * The input stream of the connection socket of the connection to the server.
     */
    @Nonnull
    private final ReadableByteChannel inChannel;
    /**
     * The list that stores the commands there were decoded and prepared for the NetComm for execution.
     */
    @Nonnull
    private final MessageExecutor executor;
    /**
     * The buffer that stores the byte that we received from the server for decoding.
     */
    @Nullable
    private ByteBuffer buffer = null;
    /**
     * Indicator if the Receiver is currently running.
     */
    private boolean running;

    /**
     * The time until a timeout occurs.
     */
    private long timeOut;

    /**
     * The basic constructor for the receiver that sets up all needed data.
     *
     * @param executor the executor that takes care to send the messages to the rest of the client
     * @param in the input stream of the socket connection to the server that contains the data that needs to
     * be decoded
     */
    Receiver(@Nonnull MessageExecutor executor, @Nonnull ReadableByteChannel in) {
        super("Illarion input thread");

        this.executor = executor;
        inChannel = in;

        decoder = NetComm.SERVER_STRING_ENCODING.newDecoder();
        setDaemon(true);
    }

    @Nonnull
    private ByteBuffer getBuffer() {
        return getBuffer(0);
    }

    @Nonnull
    private ByteBuffer getBuffer(int bufferSize) {
        ByteBuffer oldBuffer = buffer;
        if ((oldBuffer != null) && (oldBuffer.capacity() >= bufferSize)) {
            return oldBuffer;
        }

        buffer = ByteBuffer.allocateDirect(
                ((bufferSize / INITIAL_BUFFER_SIZE) * INITIAL_BUFFER_SIZE) + INITIAL_BUFFER_SIZE);
        buffer.order(ByteOrder.BIG_ENDIAN);

        if (oldBuffer != null) {
            buffer.put(oldBuffer);
            buffer.flip();
        } else {
            buffer.limit(0);
        }
        return buffer;
    }

    /**
     * Read a single byte from the buffer and handle it as signed byte.
     *
     * @return The byte from the buffer handled as signed byte
     * @throws IOException If there are more byte read then there are written in
     * the buffer
     */
    @Override
    public byte readByte() throws IOException {
        return getBuffer().get();
    }

    /**
     * Read four bytes from the buffer and handle them as a single signed value.
     *
     * @return The two bytes in the buffer handled as signed 4 byte value
     * @throws IOException If there are more byte read then there are written in
     * the buffer
     */
    @Override
    public int readInt() throws IOException {
        return getBuffer().getInt();
    }

    /**
     * Read two bytes from the buffer and handle them as a single signed value.
     *
     * @return The two bytes in the buffer handled as signed 2 byte value
     * @throws IOException If there are more byte read then there are written in
     * the buffer
     */
    @Override
    public short readShort() throws IOException {
        return getBuffer().getShort();
    }

    /**
     * Read a string from the input buffer and encode it for further usage.
     *
     * @return the decoded string
     * @throws IOException If there are more byte read then there are written in
     * the buffer
     */
    @Nonnull
    @Override
    public String readString() throws IOException {
        int len = readUShort();

        if (len == 0) {
            return "";
        }

        ByteBuffer buffer = getBuffer();
        if (len > buffer.remaining()) {
            throw new IndexOutOfBoundsException("reading beyond receive buffer " + (buffer.remaining() + len));
        }
        decodingBuffer.clear();
        int lastLimit = buffer.limit();
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
     * the buffer
     */
    @Override
    public short readUByte() throws IOException {
        short data = readByte();
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
     * the buffer
     */
    @Override
    public long readUInt() throws IOException {
        long data = readInt();
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
     * the buffer
     */
    @Override
    public int readUShort() throws IOException {
        int data = readShort();
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
    @Override
    public void run() {
        running = true;
        int minRequiredData = CommandList.HEADER_SIZE;

        while (running) {
            try {
                while (running && receiveData(minRequiredData)) {
                    while (true) {
                        ByteBuffer buffer = getBuffer();
                        // wait for a complete message header
                        if (buffer.remaining() < CommandList.HEADER_SIZE) {
                            break;
                        }

                        // identify command
                        int id = readUByte();
                        int xor = readUByte();

                        // valid command id
                        if (id != (xor ^ COMMAND_XOR_MASK)) {
                            // delete only first byte from buffer, scanning for valid command
                            buffer.position(1);
                            buffer.compact();

                            log.warn("Skipping invalid data [{}]", id);

                            continue;
                        }

                        // read length and CRC
                        int len = readUShort();
                        int crc = readUShort();

                        // wait for complete data
                        if (!isDataComplete(len)) {
                            // scroll the cursor back and wait for more.
                            buffer.position(0);
                            minRequiredData = len + CommandList.HEADER_SIZE;
                            break;
                        }

                        minRequiredData = CommandList.HEADER_SIZE;

                        // check CRC
                        if (crc != NetComm.getCRC(buffer, len)) {
                            int oldLimit = buffer.limit();
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
                            ServerReply rpl = ReplyFactory.getInstance().getReply(id);
                            if (rpl != null) {
                                rpl.decode(this);
                                if (id != CommandList.MSG_KEEP_ALIVE) {
                                    log.debug("REC: {}", rpl);
                                }

                                // put decoded command in input queue
                                executor.scheduleReplyExecution(rpl);
                            } else {
                                // throw away the command that was incorrectly decoded
                                buffer.position(len + CommandList.HEADER_SIZE);
                            }
                        } catch (@Nonnull IllegalArgumentException ex) {
                            log.error("Invalid command id received {}", Integer.toHexString(id));
                        }

                        buffer.compact();
                        buffer.flip();
                    }
                }
            } catch (@Nonnull IOException e) {
                if (running) {
                    log.error("The connection to the server is not working anymore.", e);
                    IllaClient.sendDisconnectEvent(Lang.getMsg("error.receiver"), true);
                    running = false;
                    return;
                }
            } catch (@Nonnull Exception e) {
                if (running) {
                    log.error("General error in the receiver", e);
                    IllaClient.sendDisconnectEvent(Lang.getMsg("error.receiver"), true);
                    running = false;
                    return;
                }
            }
        }
    }

    /**
     * Shutdown the receiver.
     */
    public void saveShutdown() {
        log.info("{}: Shutdown requested!", getName());
        running = false;
        interrupt();
    }

    /**
     * This function checks of the received data contains a complete command.
     *
     * @param len the amount of bytes that were received for that command
     * @return true in case the command is complete, false if not
     */
    private boolean isDataComplete(int len) {
        ByteBuffer buffer = getBuffer();
        if (len <= buffer.remaining()) {
            timeOut = 0;
            return true;
        }

        // set timeout for data
        if (timeOut == 0) {
            timeOut = System.currentTimeMillis() + RECEIVER_TIMEOUT;
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
     * @param neededDataInBuffer The data that is needed at least before the method has to return in order to parse
     * the values correctly
     * @return true in case there is any data to be decoded in the buffer
     * @throws IOException In case there is something wrong with the input stream
     */
    private boolean receiveData(int neededDataInBuffer) throws IOException {
        ByteBuffer buffer = getBuffer(neededDataInBuffer);
        int data = buffer.remaining();

        int appPos = buffer.limit();
        buffer.clear();
        buffer.position(appPos);

        int newData = 0;
        while (true) {
            if (inChannel.isOpen()) {
                newData = inChannel.read(buffer);
            }
            data += newData;
            if (data >= neededDataInBuffer) {
                break;
            }
        }

        buffer.flip();

        if ((newData > 0) && NetComm.isDumpingActive()) {
            buffer.position(appPos);
            NetComm.dump("rcv <= ", buffer);
            buffer.position(0);
        }

        return buffer.hasRemaining();
    }
}