/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
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
public class EngineManager {
    private EngineManager() {
    }

    /**
     * Create a new desktop game instance.
     *
     * @param backend      the backend used for the game
     * @param gameListener the listener
     * @param width        the width of the window
     * @param height       the height of the window
     * @param fullScreen   {@code true} in case this application is supposed to show up as full screen application
     * @return the container that displays the desktop game
     * @throws EngineException in case the creation of the game container fails
     */
    @Nonnull
    public static DesktopGameContainer createDesktopGame(@Nonnull final Backend backend,
                                                         @Nonnull final GameListener gameListener, final int width,
                                                         final int height, final boolean fullScreen) throws
            EngineException {
        final String engineClassRef = backend.getDesktopContainerClass();
        if (engineClassRef == null) {
            throw new EngineException("Selected backend " + backend.name() + " does not support desktop games.");
        }

        try {
            final Class<?> clazz = Class.forName(engineClassRef);
            final List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
            if (!interfaces.contains(DesktopGameContainer.class)) {
                throw new EngineException("Backend " + backend.name() + " refers to a illegal class.");
            }
            @SuppressWarnings("unchecked")
            final Class<DesktopGameContainer> desktopGameClass = (Class<DesktopGameContainer>) clazz;

            final Constructor<DesktopGameContainer> constructor = desktopGameClass.getConstructor(GameListener.class,
                    int.class, int.class, boolean.class);
            return constructor.newInstance(gameListener, width, height, fullScreen);
        } catch (@Nonnull final ClassNotFoundException e) {
            throw new EngineException("Selected backend " + backend.name() + " is not available.", e);
        } catch (@Nonnull final NoSuchMethodException e) {
            throw new EngineException("Selected backend " + backend.name() + " doesn't provide suited constructor.", e);
        } catch (@Nonnull final InvocationTargetException e) {
            throw new EngineException("Creation of backend " + backend.name() + " container failed.", e);
        } catch (@Nonnull final InstantiationException e) {
            throw new EngineException("Creation of backend " + backend.name() + " container failed.", e);
        } catch (@Nonnull final IllegalAccessException e) {
            throw new EngineException("Access to backend " + backend.name() + " constructor was rejected.", e);
        }
    }
}
