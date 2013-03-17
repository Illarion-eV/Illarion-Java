/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.resources.loaders;

import illarion.client.resources.Resource;
import illarion.client.resources.ResourceFactory;
import illarion.common.util.ProgressMonitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * This abstract resource loader contains the shared code for all resource loaders.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractResourceLoader<T extends Resource> implements Callable<ResourceFactory<T>> {
    /**
     * The factory that is supposed to store the load objects.
     */
    @Nullable
    private ResourceFactory<T> targetFactory;

    /**
     * The progress monitor that keeps track of the loading progress.
     */
    @Nonnull
    private final ProgressMonitor monitor;

    /**
     * Create a new resource loader and apply the weight value for the progress tracker.
     *
     * @param weight the weight value for the progress tracker
     */
    protected AbstractResourceLoader(final float weight) {
        monitor = new ProgressMonitor(weight);
    }

    /**
     * Create a new resource loader and apply the default value for the progress tracker.
     */
    protected AbstractResourceLoader() {
        this(1.f);
    }

    /**
     * Get the target factory that is set.
     *
     * @return the target factory of this loader
     */
    @Nullable
    protected final ResourceFactory<T> getTargetFactory() {
        return targetFactory;
    }

    /**
     * Check if this resource loader has a assigned target factory that is supposed to receive the data.
     *
     * @return {@code true} in case this resource loader has a assigned factory
     */
    protected final boolean hasTargetFactory() {
        return targetFactory != null;
    }

    /**
     * Set the resource factory that will take the data.
     *
     * @param factory the factory that will take the data
     */
    @Nonnull
    public final AbstractResourceLoader<T> setTarget(@Nonnull final ResourceFactory<T> factory) {
        if (hasTargetFactory()) {
            throw new IllegalStateException("Changing the target factory once set is not allowed");
        }
        targetFactory = factory;
        return this;
    }

    /**
     * Get the progress monitor that is assigned to this loader.
     *
     * @return the progress monitor of this loader
     */
    @Nonnull
    public final ProgressMonitor getProgressMonitor() {
        return monitor;
    }

    private boolean loadingDone;

    protected void loadingDone() {
        monitor.setProgress(1.f);
        loadingDone = true;
    }

    public boolean isLoadingDone() {
        return loadingDone;
    }
}
