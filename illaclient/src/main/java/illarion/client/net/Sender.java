/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import illarion.client.Debug;
import illarion.client.IllaClient;
import illarion.client.net.client.AbstractCommand;
import illarion.client.util.Lang;
import illarion.common.net.NetCommWriter;
import illarion.common.types.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharsetEncoder;
import java.util.concurrent.BlockingQueue;

/**
 * The Sender class handles all data that is send from the client, encodes the
 * commands and prepares them for sending.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@NotThreadSafe
@SuppressWarnings("ClassNamingConvention")
final class Sender extends Thread implements NetCommWriter {
    /**
     * The XOR mask the command ID is masked with to decode the checking ID and
     * ensure that the start of a command was found.
     */
    private static final int COMMAND_XOR_MASK = 0xFF;

    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    /**
     * The maximal size in bytes one command can use.
     */
    private static final int MAX_COMMAND_SIZE = 1000;

    /**
     * Length of the byte buffer used to store the data before its send to the
     * server.
     */
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(MAX_COMMAND_SIZE);

    /**
     * The string encoder that is used to encode the strings before they are
     * send to the server.
     */
    private final CharsetEncoder encoder;

    /**
     * The buffer that is used to temporary store the decoded characters that
     * were send to the player.
     */
    private final CharBuffer encodingBuffer = CharBuffer.allocate(65535);

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
     * data to the server
     */
    @SuppressWarnings("nls")
    Sender(
            BlockingQueue<AbstractCommand> outputQueue, WritableByteChannel out) {
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
     * @see Thread#run()
     */
    @SuppressWarnings("nls")
    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                // get first command form out queue

                AbstractCommand cmd;
                try {
                    cmd = queue.take();
                } catch (@Nonnull InterruptedException e) {
                    LOGGER.info("Thread \"{}\" got interrupted.", getName());
                    continue;
                }

                if (cmd.getId() != CommandList.CMD_KEEPALIVE) {
                    LOGGER.debug("SND: {}", cmd);
                }

                buffer.clear();
                buffer.put((byte) cmd.getId());
                buffer.put((byte) (cmd.getId() ^ COMMAND_XOR_MASK));

                // keep some space for the length and the CRC
                int headerLenCRC = buffer.position();
                buffer.putShort((short) 0);
                buffer.putShort((short) 0);

                int startOfCmd = buffer.position();
                // encode command into net protocol
                cmd.encode(this);

                int length = buffer.position() - startOfCmd;
                buffer.flip();
                buffer.position(startOfCmd);
                int crc = NetComm.getCRC(buffer, length);
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
        } catch (@Nonnull Exception e) {
            LOGGER.error("General error within the sender", e);
            IllaClient.sendDisconnectEvent(Lang.getMsg("error.sender"));
            running = false;
        }
    }

    /**
     * Shutdown the sender.
     */
    public void saveShutdown() {
        LOGGER.info("{}: Shutdown requested!", getName());
        running = false;
        interrupt();
    }

    /**
     * Write 1 byte as signed value to the network.
     *
     * @param value the signed byte that shall be send to the server
     */
    @Override
    public void writeByte(byte value) {
        buffer.put(value);
    }

    /**
     * Write 4 byte as signed value to the network.
     *
     * @param value the signed integer that shall be send to the server
     */
    @Override
    public void writeInt(int value) {
        buffer.putInt(value);
    }

    /**
     * Write a location to the network.
     *
     * @param loc the location that shall be send to the server
     */
    @Override
    public void writeLocation(@Nonnull Location loc) {
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
    public void writeShort(short value) {
        buffer.putShort(value);
    }

    /**
     * Write a string to the network. The length header of the string is written
     * automatically and its encoded to the correct CharSet automatically.
     *
     * @param value the string that shall be send to the server
     */
    @Override
    public void writeString(@Nonnull String value) {
        int startIndex = buffer.position();
        buffer.putShort((short) 0);

        encodingBuffer.clear();
        encodingBuffer.put(value, 0, Math.min(encodingBuffer.capacity(), value.length()));
        encodingBuffer.flip();

        encoder.encode(encodingBuffer, buffer, true);
        int lastIndex = buffer.position();
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
    public void writeUByte(short value) {
        buffer.put((byte) (value % (1 << Byte.SIZE)));
    }

    /**
     * Write 4 byte as unsigned value to the network.
     *
     * @param value the value that shall be send as unsigned integer
     */
    @Override
    public void writeUInt(long value) {
        buffer.putInt((int) (value % (1L << Integer.SIZE)));
    }

    /**
     * Write 2 byte as unsigned value to the network.
     *
     * @param value the value that shall be send as unsigned short
     */
    @Override
    public void writeUShort(int value) {
        buffer.putShort((short) (value % (1 << Short.SIZE)));
    }
}
