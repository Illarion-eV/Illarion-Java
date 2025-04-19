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
package org.illarion.engine;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

/**
 * This is the engine manager that provides access to all the backend drivers that are available.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class EngineManager {
    private EngineManager() {
    }

    /**
     * Create a new desktop game instance.
     *
     * @param backend the backend used for the game
     * @param gameListener the listener
     * @param width the width of the window
     * @param height the height of the window
     * @param fullScreen {@code true} in case this application is supposed to show up as full screen application
     * @param background Whether or not the game should limit background fps
     * @return the container that displays the desktop game
     * @throws EngineException in case the creation of the game container fails
     */
    @Nonnull
    public static DesktopGameContainer createDesktopGame(
            @Nonnull Backend backend,
            @Nonnull GameListener gameListener,
            int width,
            int height,
            boolean fullScreen,
            boolean background) throws EngineException {
        String engineClassRef = backend.getDesktopContainerClass();
        if (engineClassRef == null) {
            throw new EngineException("Selected backend " + backend.name() + " does not support desktop games.");
        }

        try {
            Class<?> clazz = Class.forName(engineClassRef);
            List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
            if (!interfaces.contains(DesktopGameContainer.class)) {
                throw new EngineException("Backend " + backend.name() + " refers to a illegal class.");
            }
            @SuppressWarnings("unchecked") Class<DesktopGameContainer> desktopGameClass = (Class<DesktopGameContainer>) clazz;

            Constructor<DesktopGameContainer> constructor = desktopGameClass
                    .getConstructor(GameListener.class, int.class, int.class, boolean.class, boolean.class);
            return constructor.newInstance(gameListener, width, height, fullScreen, background);
        } catch (@Nonnull ClassNotFoundException e) {
            throw new EngineException("Selected backend " + backend.name() + " is not available.", e);
        } catch (@Nonnull NoSuchMethodException e) {
            throw new EngineException("Selected backend " + backend.name() + " doesn't provide suited constructor.", e);
        } catch (@Nonnull InvocationTargetException | InstantiationException e) {
            throw new EngineException("Creation of backend " + backend.name() + " container failed.", e);
        } catch (@Nonnull IllegalAccessException e) {
            throw new EngineException("Access to backend " + backend.name() + " constructor was rejected.", e);
        }
    }
}
