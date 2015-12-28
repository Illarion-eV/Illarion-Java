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
package illarion.client.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class stores the reference to the global executor service.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GlobalExecutorService {
    /**
     * The executor service instance.
     */
    @Nonnull
    private static final ListeningExecutorService SERVICE = MoreExecutors.listeningDecorator(
            Executors.newSingleThreadExecutor());

    /**
     * Get the service instance.
     *
     * @return the service instance
     */
    @Nonnull
    public static ListeningExecutorService getService() {
        return SERVICE;
    }

    /**
     * Shut the service down. This class will be rendered unusable after calling this function.
     */
    public static void shutdown() {
        SERVICE.shutdown();
    }

    /**
     * Blocking any instance of this class.
     */
    private GlobalExecutorService() {
        // nothing
    }
}
