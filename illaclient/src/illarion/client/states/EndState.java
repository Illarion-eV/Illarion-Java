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
package illarion.client.states;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.slick2d.NiftyBasicGameState;
import illarion.client.Game;
import illarion.client.gui.controller.LoadScreenController;
import illarion.client.util.Lang;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.util.logging.Logger;

/**
 * This game state is active while the game loads. It takes care for showing the loading screen and to trigger the
 * actual loading.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class EndState extends NiftyBasicGameState {

    private LoadScreenController controller;

    private final Logger log = Logger.getLogger(EndState.class.getName());

    @Override
    protected void prepareNifty(final Nifty nifty, final StateBasedGame game) {
        nifty.setLocale(Lang.getInstance().getLocale());
        controller = new LoadScreenController(game);
        nifty.registerScreenController(controller);

        try {
            nifty.validateXml("illarion/client/gui/xml/shutdown.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        nifty.addXml("illarion/client/gui/xml/shutdown.xml");
    }

    @Override
    public void enterState(final GameContainer container, final StateBasedGame game)
            throws SlickException {
        getNifty().gotoScreen("clientEnd");
    }

    @Override
    public void leaveState(final GameContainer container, final StateBasedGame game)
            throws SlickException {

    }

    @Override
    public int getID() {
        return Game.STATE_ENDING;
    }
}
