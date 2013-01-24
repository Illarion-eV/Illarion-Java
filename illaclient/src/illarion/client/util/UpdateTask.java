/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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
package illarion.client.util;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import javax.annotation.Nonnull;

/**
 * This interface defines a task that is executed by the {@link UpdateTaskManager}.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface UpdateTask {
    /**
     * This function is called during the text run of the update loop.
     *
     * @param container the container that displays the game
     * @param game      the reference to the game itself
     * @param delta     the time since the last update in milliseconds
     */
    void onUpdateGame(@Nonnull GameContainer container, StateBasedGame game, int delta);
}
