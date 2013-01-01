/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with the Illarion Client. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package illarion.client;

import de.lessvoid.nifty.slick2d.NiftyStateBasedGame;
import illarion.client.states.EndState;
import illarion.client.states.LoadingState;
import illarion.client.states.LoginState;
import illarion.client.states.PlayingState;
import illarion.client.world.events.CloseGameEvent;
import org.bushe.swing.event.EventBus;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

/**
 * This is the game Illarion. This class takes care for actually building up Illarion. It will maintain the different
 * states of the game and allow switching them.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Game
        extends NiftyStateBasedGame {
    /**
     * The ID of the login state. This is one of the constants to use in order to switch the current state of the game.
     */
    public static final int STATE_LOGIN = 0;

    /**
     * The ID of the loading state. This state can be used in order to display the current loading progress.
     */
    public static final int STATE_LOADING = 1;

    /**
     * The ID of the playing state. This can be used in order to display the current game.
     */
    public static final int STATE_PLAYING = 2;

    /**
     * The ID of the ending state. This displays the last screen before the shutdown.
     */
    public static final int STATE_ENDING = 3;

    /**
     * Create the game with the fitting title, showing the name of the application and its version.
     */
    public Game() {
        super(IllaClient.APPLICATION + " " + IllaClient.VERSION, true);
    }

    /**
     * Prepare the list of the game states. Using this class all states of the game are load up.
     */
    @Override
    public void initStatesList(final GameContainer container)
            throws SlickException {
        addState(new LoginState());
        addState(new LoadingState());
        addState(new PlayingState());
        addState(new EndState());
    }

    /**
     * This function is used to fetch the close requests to the application.
     *
     * @return {@code true} in case the application is supposed to exit right now.
     */
    @Override
    public boolean closeRequested() {
        if (getCurrentStateID() == STATE_PLAYING) {
            EventBus.publish(new CloseGameEvent());
            return false;
        }
        return true;
    }
}
