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
package illarion.client.states;

import de.lessvoid.nifty.Nifty;
import illarion.client.Game;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;

/**
 * This interface defines the different states the game is able to enter.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface GameState {
    /**
     * This function is called once the game is created.
     * <p/>
     * Its called for all game states, not just for the active one.
     *
     * @param game      the game that is running
     * @param container the container that carries the game
     * @param nifty     the instance of the Nifty GUI that displays the game
     */
    void create(@Nonnull Game game, @Nonnull GameContainer container, @Nonnull Nifty nifty);

    /**
     * This function is called once the game is shut down.
     * <p/>
     * Its called for all game states, not just for the active one.
     */
    void dispose();

    /**
     * This function is called for the active state once the size of the game container changes.
     *
     * @param container the container that carries the game
     * @param width     the new width of the game surface
     * @param height    the new height of the game surface
     */
    void resize(@Nonnull GameContainer container, int width, int height);

    /**
     * This function is called during the update loop for the active game state.
     *
     * @param container the container that carries the game
     * @param delta     the time since the last update in milliseconds
     */
    void update(@Nonnull GameContainer container, int delta);

    /**
     * This function is called during the render loop for the active game state.
     *
     * @param container the container that carries the game
     */
    void render(@Nonnull GameContainer container);

    /**
     * This function is called once the game is requested to close. Its called for the active game state. This state
     * is allowed to choose if the game is supposed to quit or interrupt the exit progress.
     *
     * @return {@code true} if its okay that the game is closing
     */
    boolean isClosingGame();

    /**
     * This function is called once the game is entered
     *
     * @param nifty the active instance of the Nifty-GUI
     */
    void enterState(@Nonnull GameContainer container, @Nonnull Nifty nifty);

    /**
     * This function is called once a formerly active state is left.
     */
    void leaveState(@Nonnull GameContainer container);
}
