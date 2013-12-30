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
package illarion.client.gui.controller.game;

import org.illarion.engine.GameContainer;

/**
 * This interface has to be implemented by update handlers that want to receive update calls during the main loop.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public interface UpdatableHandler {
    /**
     * This function is called once during a update loop. It should be used to perform changes at the optics of the
     * game.
     *
     * @param container the container that contains the game
     * @param delta     the time since the last update
     */
    void update(GameContainer container, int delta);
}
