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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import javax.annotation.Nonnull;

/**
 * This is a overwritten LWJGL application that is used to extend the default way this class works by some needed
 * components such as a proper handling of the application shutdown.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxLwjglApplication extends LwjglApplication {
    /**
     * The listener that receives the events of the application. This reference is required to forward the closing
     * request.
     */
    @Nonnull
    private ListenerApplication listener;

    /**
     * Create a new application.
     *
     * @param listener the listener that receives the updates
     * @param config the configuration used to create the application
     */
    GdxLwjglApplication(@Nonnull final ListenerApplication listener, final LwjglApplicationConfiguration config) {
        super(listener, config);
        this.listener = listener;
    }

    /**
     * This is the overwritten exit function that queries the listener if the close is allowed or not. In case the
     * listener rejects the close, the game will keep running. This changes the default way this function works.
     * Usually it directly terminates the game.
     */
    @Override
    public void exit() {
        if (listener.isExitAllowed()) {
            super.exit();
        }
    }

    /**
     * This function terminates the game without querying the listener. This function does what the original exit
     * function did.
     */
    public void shutdownGame() {
        super.exit();
    }
}
