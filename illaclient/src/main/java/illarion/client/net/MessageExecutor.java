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
import illarion.client.net.server.AbstractReply;
import illarion.common.util.Stoppable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * This class will take care that the messages received from the server are executes properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class MessageExecutor extends Thread implements Stoppable {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageExecutor.class);

    /**
     * This queue contains all tasks that were executed already once and need to be executed a second time.
     */
    @Nonnull
    private final Queue<AbstractReply> delayedQueue;

    /**
     * The queue that contains all the tasks that were received from the server and still need to be executed.
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
     * The running flag. The loop of this thread will keep running until this flag is set to <code>false</code>.
     */
    private volatile boolean running;

    /**
     * Default constructor for a message executor.
     *
     * @param inputQueue the input queue of messages that need to be handled
     */
    @SuppressWarnings("nls")
    public MessageExecutor(final BlockingQueue<AbstractReply> inputQueue) {
        super("NetComm MessageExecutor");
        input = inputQueue;
        delayedQueue = new LinkedList<>();
    }

    @Override
    public synchronized void start() {
        running = true;
        super.start();
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
     * Main loop of the Message Executor. The messages are handled as soon as they appear in the queue.
     */
    @SuppressWarnings("nls")
    @Override
    public void run() {
        while (running) {
            /*
             * First we handle the delayed stuff in case there is any and it
             * does not block from executing.
             */
            if (!delayedQueue.isEmpty() && delayedQueue.peek().processNow()) {
                final AbstractReply rpl = delayedQueue.poll();
                rpl.executeUpdate();
                continue;
            }

            AbstractReply rpl;

            if (repeatReply == null) {
                try {
                    rpl = input.take();
                } catch (@Nonnull final InterruptedException e) {
                    // Got and interrupt, quit the thread right now.
                    LOGGER.warn("MessageExecutor got interrupted and will exit now!");
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
                    LOGGER.debug("executing " + rpl.toString());
                }

                if (rpl.executeUpdate()) {
                    if (IllaClient.isDebug(Debug.net)) {
                        LOGGER.debug("finished " + rpl.toString());
                    }
                } else {
                    if (IllaClient.isDebug(Debug.net)) {
                        LOGGER.debug("repeating " + rpl.toString());
                    }

                    repeatReply = rpl;
                }
            } else {
                delayedQueue.offer(rpl);
            }
        }
    }

    /**
     * Have the thread finishing the current message and shut the thread down after.
     */
    @Override
    public void saveShutdown() {
        LOGGER.info(getName() + ": Shutdown requested!");
        running = false;
        interrupt();
    }
}
