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

/**
 * This class needs to be implemented by the game itself. It provides the callbacks required to interact with the
 * lifecycle of the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface GameListener {
    /**
     * This function is called once the game is created.
     *
     * @param container the game container
     */
    void create(@Nonnull GameContainer container);

    /**
     * This function is called at the destruction of the game. (Should be used for cleanups before the shutdown itself)
     */
    void dispose();

    /**
     * This function is called in case the application got a resize. Its called right before the
     * {@link #update(GameContainer, int)} function is called.
     *
     * @param container the game container
     * @param width     the new width
     * @param height    the new height
     */
    void resize(@Nonnull GameContainer container, int width, int height);

    /**
     * During the call of this function the application is supposed to perform the update of the game logic.
     *
     * @param container the game container
     * @param delta     the time since the last update call
     */
    void update(@Nonnull GameContainer container, int delta);

    /**
     * During the call of this function the application is supposed to perform all rendering operations.
     *
     * @param container the game container
     */
    void render(@Nonnull GameContainer container);

    /**
     * This function is called in case the game receives a request to be closed.
     *
     * @return {@code true} in case the game is supposed to shutdown, else the closing request is rejected
     */
    boolean isClosingGame();
}
