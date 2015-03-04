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
package illarion.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is a small helper class that is used to generate threads for executors and thread pools. The nice thing over
 * the normal implementations of the thread factory is the possibility to create threads with a specific pool name
 * and the possibility to create daemon threads.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class PoolThreadFactory implements ThreadFactory {
    @Nonnull
    private final String threadNameHead;
    private final boolean daemonThreads;
    @Nullable
    private final ThreadGroup group;
    @Nonnull
    private final AtomicInteger threadNumber;

    public PoolThreadFactory(@Nonnull String poolName, boolean daemonThreads) {
        threadNameHead = poolName + " Thread-";
        this.daemonThreads = daemonThreads;

        SecurityManager sManager = System.getSecurityManager();
        group = (sManager == null) ? Thread.currentThread().getThreadGroup() : sManager.getThreadGroup();
        threadNumber = new AtomicInteger(1);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread createdThread = new Thread(group, r, threadNameHead + threadNumber.getAndIncrement(), 0);
        if (createdThread.isDaemon() != daemonThreads) {
            createdThread.setDaemon(daemonThreads);
        }
        if (createdThread.getPriority() != Thread.NORM_PRIORITY) {
            createdThread.setPriority(Thread.NORM_PRIORITY);
        }
        return createdThread;
    }
}
