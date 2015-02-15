/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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

import illarion.client.net.server.ServerReply;
import illarion.client.net.server.ServerReplyResult;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.*;

/**
 * This class will take care that the messages received from the server are executes properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class MessageExecutor {
    @Nonnull
    private static final Marker NET = MarkerFactory.getMarker("Net");
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(MessageExecutor.class);

    @Nonnull
    private final ExecutorService executorService;

    /**
     * Default constructor for a message executor.
     */
    @SuppressWarnings("nls")
    MessageExecutor() {
        executorService = Executors.newSingleThreadExecutor();
    }

    void scheduleReplyExecution(@Nonnull final ServerReply reply) {
        log.debug(NET, "scheduled {}", reply);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                executeReply(reply);
            }
        });
    }

    private void executeReply(@Nonnull ServerReply reply) {
        log.debug(NET, "executing {}", reply);
        ServerReplyResult result = reply.execute();

        switch (result) {
            case Success:
                log.debug(NET, "finished with success {}", reply);
                break;
            case Failed:
                log.error(NET, "finished with failure {}", reply);
                break;
            case Reschedule:
                log.debug(NET, "delaying {}", reply);
                scheduleReplyExecution(reply);
        }
    }

    /**
     * Shutdown the sender.
     */
    @Nonnull
    public Future<Boolean> saveShutdown() {
        executorService.shutdown();

        return new Future<Boolean>() {
            @Override
            @Contract(value = "_ -> false", pure = true)
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            @Contract(value = "-> false", pure = true)
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return executorService.isTerminated();
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
                return executorService.awaitTermination(timeout, unit);
            }
        };
    }
}
