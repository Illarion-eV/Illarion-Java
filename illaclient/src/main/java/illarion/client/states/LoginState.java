/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.client.states;

import de.lessvoid.nifty.Nifty;
import illarion.client.Game;
import illarion.client.gui.controller.CharScreenController;
import illarion.client.gui.controller.CreditsStartScreenController;
import illarion.client.gui.controller.LoginScreenController;
import org.illarion.engine.GameContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This game state is used to display the login and character selection dialog. Also the option dialog is displayed in
 * this state.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class LoginState implements GameState {
    /**
     * The screen controller that takes care for the login screen.
     */
    private LoginScreenController loginScreenController;

    /**
     * The logger that is used for the logging output of this class.
     */
    private static final Logger log = LoggerFactory.getLogger(LoginState.class);

    @Override
    public void create(@Nonnull Game game, @Nonnull GameContainer container, @Nonnull Nifty nifty) {
        loginScreenController = new LoginScreenController(game, container.getEngine());
        nifty.registerScreenController(loginScreenController, new CharScreenController(game),
                                       new CreditsStartScreenController(container.getEngine()));

        nifty.loadStyleFile("nifty-illarion-style.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.loadControlFile("illarion-gamecontrols.xml");
        Util.loadXML(nifty, "illarion/client/gui/xml/login.xml");
        Util.loadXML(nifty, "illarion/client/gui/xml/charselect.xml");
        Util.loadXML(nifty, "illarion/client/gui/xml/options.xml");
        Util.loadXML(nifty, "illarion/client/gui/xml/credits.xml");
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(@Nonnull GameContainer container, int width, int height) {
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        if (loginScreenController != null) {
            loginScreenController.update();
        }
    }

    @Override
    public void render(@Nonnull GameContainer container) {
    }

    @Override
    public boolean isClosingGame() {
        return true;
    }

    @Override
    public void enterState(@Nonnull GameContainer container, @Nonnull Nifty nifty) {
        nifty.gotoScreen("login");
    }

    @Override
    public void leaveState(@Nonnull GameContainer container) {
    }
}
