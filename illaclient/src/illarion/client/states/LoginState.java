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
import illarion.client.IllaClient;
import illarion.client.gui.controller.CharScreenController;
import illarion.client.gui.controller.LoginScreenController;
import illarion.client.util.Lang;
import illarion.common.config.ConfigChangedEvent;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import javax.annotation.Nonnull;

/**
 * This game state is used to display the login and character selection dialog. Also the option dialog is displayed in
 * this state.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class LoginState
        extends NiftyBasicGameState implements EventTopicSubscriber<ConfigChangedEvent> {
    /**
     * The screen controller that takes care for the login screen.
     */
    private LoginScreenController loginScreenController;

    /**
     * The logger that is used for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(LoginState.class);

    /**
     * Create the game state that handles the login with the identifier that is needed to access it.
     */
    public LoginState() {
        super("login");
        EventBus.subscribe(IllaClient.CFG_RESOLUTION, this);
    }

    @Override
    protected void prepareNifty(@Nonnull final Nifty nifty, final StateBasedGame game) {
        nifty.setLocale(Lang.getInstance().getLocale());

        loginScreenController = new LoginScreenController(game);
        nifty.registerScreenController(loginScreenController, new CharScreenController(game));

        loadXML(nifty, "illarion/client/gui/xml/login.xml");
        loadXML(nifty, "illarion/client/gui/xml/charselect.xml");
        loadXML(nifty, "illarion/client/gui/xml/options.xml");
        loadXML(nifty, "illarion/client/gui/xml/credits.xml");
    }

    /**
     * Load the XML file after validating its contents.
     *
     * @param nifty   the instance of Nifty the files are supposed to be applied to
     * @param xmlFile the XML file that is supposed to be load
     */
    private static void loadXML(@Nonnull final Nifty nifty, final String xmlFile) {
        try {
            nifty.validateXml(xmlFile);
        } catch (Exception e) {
            LOGGER.error("Validation of the XML file \"" + xmlFile + "\" failed.", e);
        }
        nifty.addXml(xmlFile);
    }

    @Override
    public int getID() {
        return Game.STATE_LOGIN;
    }

    /**
     * Updating the game is not needed in this implementation as only the GUI is displayed.
     */
    @Override
    protected void updateGame(final GameContainer container, final StateBasedGame game, final int delta) {
        if (loginScreenController != null) {
            loginScreenController.update();
        }
    }

    @Override
    public void onEvent(final String topic, final ConfigChangedEvent data) {
        if (IllaClient.CFG_RESOLUTION.equals(topic) && (getNifty() != null)) {
            getNifty().resolutionChanged();
        }
    }
}
