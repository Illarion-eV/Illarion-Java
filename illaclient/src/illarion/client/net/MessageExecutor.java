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

import java.util.concurrent.BlockingQueue;

import javolution.util.FastList;

import org.apache.log4j.Logger;

import illarion.client.Debug;
import illarion.client.IllaClient;
import illarion.client.net.server.AbstractReply;

import illarion.common.util.Stoppable;

/**
 * This class will take care that the messages received from the server are
 * executes properly.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
final class MessageExecutor extends Thread implements Stoppable {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(MessageExecutor.class);

    /**
     * This queue contains all tasks that were executed already once and need to
     * be executed a second time.
     */
    private final FastList<AbstractReply> delayedQueue;

    /**
     * The queue that contains all the tasks that were received from the server
     * and still need to be executed.
     */
    private final BlockingQueue<AbstractReply> input;

    /**
     * This boolean stores if anything was received already from the server.
     */
    private boolean receivedAnything = false;

    /**
     * This reply is to be repeated at the next run.
     */
    private AbstractReply repeatReply;

    /**
     * The running flag. The loop of this thread will keep running until this
     * flag is set to <code>false</code>.
     */
    private volatile boolean running = true;

    /**
     * Default constructor for a message executor.
     * 
     * @param inputQueue the input queue of messages that need to be handled
     */
    @SuppressWarnings("nls")
    public MessageExecutor(final BlockingQueue<AbstractReply> inputQueue) {
        super("NetComm MessageExecutor");
        input = inputQueue;
        delayedQueue = new FastList<AbstractReply>();
    }

    /**
     * Check if the client received already anything from the server.
     * 
     * @return <code>true</code> in case anything was received from server
     */
    public boolean hasReceivedAnything() {
        return receivedAnything;
    }

    /**
     * Main loop of the Message Executor. The messages are handled as soon as
     * they appear in the queue.
     */
    @SuppressWarnings("nls")
    @Override
    public void run() {
        while (running) {
            /*
             * First we handle the delayed stuff in case there is any and it
             * does not block from executing.
             */
            if (!delayedQueue.isEmpty()
                && delayedQueue.getFirst().processNow()) {
                final AbstractReply rpl = delayedQueue.removeFirst();
                rpl.executeUpdate();
                rpl.recycle();
                continue;
            }

            AbstractReply rpl;

            if (repeatReply == null) {
                try {
                    rpl = input.take();
                } catch (final InterruptedException e) {
                    // Got and interrupt, quit the thread right now.
                    LOGGER
                        .warn("MessageExecutor got interrupted and will exit now!");
                    return;
                }
            } else {
                rpl = repeatReply;
            }
            receivedAnything = true;

            /*
             * Process the updates or put them into the delayed queue.
             */
            if (rpl.processNow()) {
                if (IllaClient.isDebug(Debug.net)) {
                    LOGGER.debug("executing " + rpl.getClass());
                }

                if (rpl.executeUpdate()) {
                    if (IllaClient.isDebug(Debug.net)) {
                        LOGGER.debug("finished " + rpl.getClass());
                    }

                    rpl.recycle();
                } else {
                    if (IllaClient.isDebug(Debug.net)) {
                        LOGGER.debug("repeating " + rpl.getClass());
                    }

                    repeatReply = rpl;
                }
            } else {
                delayedQueue.addLast(rpl);
            }
        }
    }

    /**
     * Have the thread finishing the current message and shut the thread down
     * after.
     */
    @Override
    public void saveShutdown() {
        running = false;
        synchronized (input) {
            input.notify();
        }
    }

}
