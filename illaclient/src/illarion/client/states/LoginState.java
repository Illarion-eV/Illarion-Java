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
import illarion.client.gui.controller.CharScreenController;
import illarion.client.gui.controller.LoginScreenController;
import illarion.client.util.Lang;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This game state is used to display the login and character selection dialog. Also the option dialog is displayed in
 * this state.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class LoginState
        extends NiftyBasicGameState {
    /**
     * Create the game state that handles the login with the identifier that is needed to access it.
     */
    public LoginState() {
        super("login");
    }

    @Override
    protected void prepareNifty(Nifty nifty, StateBasedGame game) {
        nifty.setLocale(Lang.getInstance().getLocale());
        nifty.registerScreenController(new LoginScreenController(game), new CharScreenController(game));

        try {
            nifty.validateXml("illarion/client/gui/xml/login.xml");
            nifty.validateXml("illarion/client/gui/xml/charselect.xml");
            nifty.validateXml("illarion/client/gui/xml/options.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        nifty.addXml("illarion/client/gui/xml/login.xml");
        nifty.addXml("illarion/client/gui/xml/charselect.xml");
        nifty.addXml("illarion/client/gui/xml/options.xml");
    }

    @Override
    public int getID() {
        return Game.STATE_LOGIN;
    }
}
