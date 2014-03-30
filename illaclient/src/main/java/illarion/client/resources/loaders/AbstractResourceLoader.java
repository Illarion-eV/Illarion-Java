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
package illarion.client.resources.loaders;

import illarion.client.resources.Resource;
import illarion.client.resources.ResourceFactory;
import illarion.common.util.ProgressMonitor;
import org.illarion.engine.assets.TextureManager;
import org.illarion.engine.graphic.Texture;

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
     * @throws IllegalStateException in case the target factory was not set before
     */
    @Nonnull
    protected final ResourceFactory<T> getTargetFactory() {
        if (targetFactory == null) {
            throw new IllegalStateException("Requested target factory before it was set.");
        }
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

    /**
     * This variable is set {@code true} once the loading is done.
     */
    private boolean loadingDone;

    /**
     * Report the loading progress as done.
     */
    protected void loadingDone() {
        monitor.setProgress(1.f);
        loadingDone = true;
    }

    /**
     * Check if the loading progress is done.
     *
     * @return {@code true} if the loading is done
     */
    public boolean isLoadingDone() {
        return loadingDone;
    }

    /**
     * Get the textures that are needed for a single object. This function is a utility to all the loaders that need
     * to create sprite objects.
     *
     * @param textureManager the texture manager that supplies the textures
     * @param path the root path of the textures
     * @param name the name of the texture
     * @param frames the amount of frames
     * @return a array with the length equal to the frames that contains the load textures
     */
    @Nonnull
    protected static Texture[] getTextures(
            @Nonnull final TextureManager textureManager,
            @Nonnull final String path,
            @Nonnull final String name,
            final int frames) {
        final Texture[] resultTextures = new Texture[frames];
        if (frames == 1) {
            resultTextures[0] = textureManager.getTexture(path, name);
            if (resultTextures[0] == null) {
                System.err.println("Problem loading texture!");
            }
        } else {
            for (int i = 0; i < frames; i++) {
                resultTextures[i] = textureManager.getTexture(path, name + '-' + i);
            }
        }
        return resultTextures;
    }
}
