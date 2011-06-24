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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javolution.text.TextBuilder;

import org.apache.log4j.Logger;

import illarion.client.Debug;
import illarion.client.IllaClient;
import illarion.client.Servers;
import illarion.client.crash.NetCommCrashHandler;
import illarion.client.net.client.AbstractCommand;
import illarion.client.net.server.AbstractReply;

import illarion.common.util.Scheduler;

/**
 * Network communication interface. All activities like sending and transmitting
 * of messages and commands in handled by this class. It handles the sockets and
 * the in and output queues.
 */
public final class NetComm {
    /**
     * This constant holds the encoding for strings that are received from and
     * send to the server.
     */
    @SuppressWarnings("nls")
    public static final Charset SERVER_STRING_ENCODING = Charset
        .forName("ISO-8859-1");

    /**
     * The value that is added and used for the modulus division that is done on
     * the buffer value before printing it.
     */
    private static final int CHAR_MOD = 265;

    /**
     * This is the string used to format the debugging output of the received
     * and transmitted data.
     */
    @SuppressWarnings("nls")
    private static final String DUMP_FORMAT_BYTES = "[%1$02X]";

    /**
     * This is the string used to format the debugging output of the total
     * amount of received bytes.
     */
    @SuppressWarnings("nls")
    private static final String DUMP_FORMAT_TOTAL = "[%1$d byte]";

    /**
     * The value of the first printable character using
     * {@link java.lang.String#valueOf(char)}.
     */
    private static final int FIRST_PRINT_CHAR = 65;

    /**
     * Delay in ms before the first render of the screen. This is needed to give
     * the network interface some time to fetch the data.
     */
    private static final int INITIAL_DELAY = 500;

    /**
     * The delay in ms between two keep alive commands that are send to ensure
     * the server that the client is still working and the connection is stable.
     * Now set to 10 seconds.
     */
    private static final int KEEP_ALIVE_DELAY = 10 * 1000;

    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(NetComm.class);

    /**
     * General time to wait in case its needed that other threads need to react
     * on some input.
     */
    private static final int THREAD_WAIT_TIME = 100;

    /**
     * List of server messages that got received and decoded but were not yet
     * executed.
     */
    private final BlockingQueue<AbstractReply> inputQueue;

    /**
     * The receiver that accepts and decodes data that was received from the
     * server.
     */
    private Receiver inputThread;

    /**
     * The thread that handles the messages that arrive from the server.
     */
    private MessageExecutor messageHandler;

    /**
     * The queue of commands that were not yet send but are planned to be send.
     */
    private final BlockingQueue<AbstractCommand> outputQueue;

    /**
     * The sender instance that accepts all server client commands that shall be
     * send and forwards the data to this class.
     */
    private Sender sender;

    /**
     * Communication socket to the Illarion server.
     */
    private SocketChannel socket;

    /**
     * Default constructor that prepares all values of the NetComm.
     */
    public NetComm() {
        CommandFactory.getInstance();
        ReplyFactory.getInstance();

        inputQueue = new LinkedBlockingQueue<AbstractReply>();
        outputQueue = new LinkedBlockingQueue<AbstractCommand>();
    }

    /**
     * New version of the checksum calculation. All bytes from the current
     * position of the buffer to the limit are included to the calculation. The
     * limit, mark and position is restored by this function. So the ByteBuffer
     * is unchanged after the function leaves.
     * 
     * @param buffer the byte buffer that provides the byte data
     * @param len the amount of byte that shall be included to the checksum
     *            calculation
     * @return the calculated checksum
     */
    public static int getCRC(final ByteBuffer buffer, final int len) {
        int crc = 0;
        int remain = len;
        final int pos = buffer.position();
        while (buffer.hasRemaining() && (remain-- > 0)) {
            final byte data = buffer.get();
            crc += data;
            if (data < 0) {
                crc += 1 << Byte.SIZE;
            }
        }
        buffer.position(pos);
        return crc % ((1 << Short.SIZE) - 1);
    }

    /**
     * This function has only debug purposes and is used to print the contents
     * of a buffer to the output log. This is used for the debug output when
     * debugging the protocol. The bytes that are written are all remaining
     * bytes of the buffer. Also the position of the buffer with point at the
     * end after this function was called.
     * 
     * @param prefix The prefix that shall be written first to the log
     * @param buffer The buffer that contains the values that shall be written
     */
    protected static void dump(final String prefix, final ByteBuffer buffer) {
        final TextBuilder builder = TextBuilder.newInstance();
        final TextBuilder builderText = TextBuilder.newInstance();

        int bytes = 0;
        while (buffer.hasRemaining()) {
            final byte bufferValue = buffer.get();
            builder.append(String.format(DUMP_FORMAT_BYTES,
                Byte.valueOf(bufferValue)));

            final char c = (char) ((bufferValue + CHAR_MOD) % CHAR_MOD);
            if (c >= FIRST_PRINT_CHAR) {
                builderText.append(c);
            } else {
                builderText.append('.');
            }
            ++bytes;
        }

        builder.append(' ');
        builder
            .append(String.format(DUMP_FORMAT_TOTAL, Integer.valueOf(bytes)));
        builder.append(' ');
        builder.append('<');
        builder.append(builderText);
        builder.append('>');

        LOGGER.debug(builder.toString());
        TextBuilder.recycle(builder);
        TextBuilder.recycle(builderText);
    }

    /**
     * Establish a connection with the server.
     * 
     * @return true in case the connection got established. False if not.
     */
    @SuppressWarnings("nls")
    public boolean connect() {
        try {
            final Servers usedServer =
                IllaClient.getInstance().getUsedServer();

            final InetSocketAddress address =
                new InetSocketAddress(usedServer.getServerHost(),
                    usedServer.getServerPort());
            socket = SelectorProvider.provider().openSocketChannel();
            socket.configureBlocking(true);
            socket.socket().setPerformancePreferences(0, 2, 1);

            if (!socket.connect(address)) {
                while (socket.isConnectionPending()) {
                    socket.finishConnect();
                    try {
                        Thread.sleep(1);
                    } catch (final InterruptedException e) {
                        LOGGER.warn("Waiting time for connection finished got"
                            + " interrupted");
                    }
                }
            }

            sender = new Sender(outputQueue, socket);
            sender.setUncaughtExceptionHandler(NetCommCrashHandler
                .getInstance());
            inputThread = new Receiver(inputQueue, socket);
            inputThread.setUncaughtExceptionHandler(NetCommCrashHandler
                .getInstance());
            messageHandler = new MessageExecutor(inputQueue);
            messageHandler.setUncaughtExceptionHandler(NetCommCrashHandler
                .getInstance());

            sender.start();
            inputThread.start();
            messageHandler.start();

            Scheduler.getInstance().schedule(new TimerTask() {
                @Override
                public void run() {
                    final AbstractCommand cmd =
                        CommandFactory.getInstance().getCommand(
                            CommandList.CMD_KEEPALIVE);
                    sendCommand(cmd);
                }
            }, INITIAL_DELAY, KEEP_ALIVE_DELAY);
        } catch (final IOException e) {
            LOGGER.fatal("Connection error");
            return false;
        }
        return true;
    }

    /**
     * Disconnect the client-server connection and shut the socket along with
     * all threads for sending and receiving down.
     */
    @SuppressWarnings("nls")
    public void disconnect() {
        try {
            // stop threads
            if (sender != null) {
                sender.setRunning(false);
                sender = null;
            }

            if (inputThread != null) {
                inputThread.setRunning(false);
                inputThread = null;
            }

            if (messageHandler != null) {
                messageHandler.saveShutdown();
                messageHandler = null;
            }

            // wait for threads to react
            try {
                Thread.sleep(THREAD_WAIT_TIME);
            } catch (final InterruptedException e) {
                LOGGER.warn("Disconnecting wait got interrupted.");
            }

            inputQueue.clear();
            outputQueue.clear();

            // close connection
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (final IOException e) {
            LOGGER.warn("Disconnecting failed.", e);
        }
    }

    /**
     * Check if the network interface received anything from the server since it
     * started.
     * 
     * @return true in case anything was received from the server.
     */
    public boolean receivedAnything() {
        return messageHandler.hasReceivedAnything();
    }

    /**
     * Put command in send queue so its send at the next send loop.
     * 
     * @param cmd the command that shall be added to the queue
     */
    @SuppressWarnings("nls")
    public void sendCommand(final AbstractCommand cmd) {
        if (IllaClient.isDebug(Debug.protocol)) {
            if (cmd.getId() != CommandList.CMD_KEEPALIVE) {
                LOGGER.debug("SND: " + cmd.toString());
            }
        }

        try {
            outputQueue.put(cmd);
        } catch (final InterruptedException e) {
            LOGGER
                .error("Got interrupted while trying to add a command to to the queue.");
        }
    }
}
