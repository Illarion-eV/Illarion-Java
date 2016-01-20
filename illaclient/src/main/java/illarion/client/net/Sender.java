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
import illarion.client.net.client.AbstractCommand;
import illarion.common.net.NetCommWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.concurrent.*;

/**
 * The Sender class handles all data that is send from the client, encodes the
 * commands and prepares them for sending.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@NotThreadSafe
final class Sender implements NetCommWriter {
    /**
     * The XOR mask the command ID is masked with to decode the checking ID and
     * ensure that the start of a command was found.
     */
    private static final int COMMAND_XOR_MASK = 0xFF;

    /**
     * The instance of the logger that is used to write out the data.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(Sender.class);

    /**
     * The maximal size in bytes one command can use.
     */
    private static final int MAX_COMMAND_SIZE = 1000;

    /**
     * Length of the byte buffer used to store the data before its send to the
     * server.
     */
    @Nonnull
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(MAX_COMMAND_SIZE);

    /**
     * The string encoder that is used to encode the strings before they are
     * send to the server.
     */
    @Nonnull
    private final CharsetEncoder encoder;

    /**
     * The buffer that is used to temporary store the decoded characters that
     * were send to the player.
     */
    @Nonnull
    private final CharBuffer encodingBuffer = CharBuffer.allocate(65535);

    /**
     * The output stream of the socket connection to the server. The encoded
     * data is written on this stream to be send to the server.
     */
    @Nonnull
    private final WritableByteChannel outChannel;

    @Nonnull
    private final ExecutorService commandExecutor;

    /**
     * The basic constructor for the sender that sets up all needed data.
     *
     * @param out the output channel of the socket connection used to send the
     * data to the server
     */
    Sender(@Nonnull WritableByteChannel out) {
        commandExecutor = Executors.newSingleThreadExecutor();
        outChannel = out;

        encoder = NetComm.SERVER_STRING_ENCODING.newEncoder();
    }

    void sendCommand(@Nonnull AbstractCommand cmd) {
        commandExecutor.submit(() -> {
            try {
                encodeCommand(cmd);
            } catch (IOException e) {
                log.error("Connection failure: {}", e.getMessage());
                IllaClient.returnToLogin(e.getLocalizedMessage());
                commandExecutor.shutdownNow();
            } catch (Exception e) {
                log.error("Error while sending command.", e);
            }
            return null;
        });
    }

    private void encodeCommand(@Nonnull AbstractCommand cmd) throws IOException {
        if (cmd.getId() != CommandList.CMD_KEEPALIVE) {
            log.debug("SND: {}", cmd);
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

        if (NetComm.isDumpingActive()) {
            NetComm.dump("snd => ", buffer);
            buffer.flip();
        }

        outChannel.write(buffer);
    }

    /**
     * Shutdown the sender.
     */
    @Nonnull
    public Future<Boolean> saveShutdown() {
        commandExecutor.shutdown();

        return new Future<Boolean>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return commandExecutor.isTerminated();
            }

            @Override
            @Nonnull
            public Boolean get() throws InterruptedException, ExecutionException {
                try {
                    return get(1, TimeUnit.HOURS);
                } catch (TimeoutException e) {
                    throw new ExecutionException(e);
                }
            }

            @Override
            @Nonnull
            public Boolean get(long timeout, @Nonnull TimeUnit unit)
                    throws InterruptedException, ExecutionException, TimeoutException {
                return commandExecutor.awaitTermination(timeout, unit);
            }
        };
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
     * Write 2 byte as signed value to the network.
     *
     * @param value the signed integer that shall be send to the server
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
    public void writeString(@Nonnull String value) throws CharacterCodingException {
        int startIndex = buffer.position();
        buffer.putShort((short) 0);

        encodingBuffer.clear();
        encodingBuffer.put(value, 0, Math.min(encodingBuffer.capacity(), value.length()));
        encodingBuffer.flip();

        do {
            CoderResult encodingResult = encoder.encode(encodingBuffer, buffer, true);
            if (!encodingResult.isError()) {
                break;
            }
            if (encodingResult.isUnmappable()) {
                log.warn("Found a character that failed to encode for the transfer to the server: {} - SKIP",
                        encodingBuffer.get());
            } else {
                encodingResult.throwException();
            }
        } while (encodingBuffer.hasRemaining());

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
     * Write 2 byte as unsigned value to the network.
     *
     * @param value the value that shall be send as unsigned short
     */
    @Override
    public void writeUShort(int value) {
        buffer.putShort((short) (value % (1 << Short.SIZE)));
    }
}
