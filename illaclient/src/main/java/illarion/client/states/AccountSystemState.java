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
import de.lessvoid.nifty.screen.Screen;
import illarion.client.Game;
import illarion.client.gui.controller.CharScreenController;
import illarion.client.gui.controller.CreditsStartScreenController;
import illarion.client.gui.controller.EnteringScreenController;
import illarion.client.gui.controller.LoginScreenController;
import illarion.client.util.account.AccountSystem;
import org.illarion.engine.GameContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This state contains the login, options, character creation, account creation, character selection and character
 * information editing.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AccountSystemState implements GameState {
    /**
     * The logger that is used for the logging output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(AccountSystemState.class);

    @Nullable
    private Nifty nifty;

    @Nullable
    private String switchToScreen;

    @Override
    public void create(@Nonnull Game game, @Nonnull GameContainer container, @Nonnull Nifty nifty) {
        this.nifty = nifty;
        switchToScreen = null;

        AccountSystem accountSystem = new AccountSystem();
        nifty.registerScreenController(
                new LoginScreenController(container.getEngine(), accountSystem),
                new CharScreenController(),
                new CreditsStartScreenController(container.getEngine()),
                new EnteringScreenController(game, container));

        Util.loadXML(nifty, "illarion/client/gui/xml/login.xml");
        Util.loadXML(nifty, "illarion/client/gui/xml/charselect.xml");
        Util.loadXML(nifty, "illarion/client/gui/xml/entering.xml");
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
        if (switchToScreen != null && nifty != null) {
            Screen current = nifty.getCurrentScreen();
            if (current == null || !current.getScreenId().equals(switchToScreen)) {
                nifty.gotoScreen(switchToScreen);
            } else {
                switchToScreen = null;
            }
        }
    }

    @Override
    public void render(@Nonnull GameContainer container) {
    }

    @Override
    public boolean isClosingGame() {
        return true;
    }

    public void setErrorMessage(@Nonnull String errorMessage) {
        if (nifty != null) {
            Screen loginScreen = nifty.getScreen("login");
            if (loginScreen != null) {
                LoginScreenController controller = (LoginScreenController) loginScreen.getScreenController();
                controller.showError(errorMessage);
            }
        }
    }

    @Override
    public void enterState(@Nonnull GameContainer container, @Nonnull Nifty nifty) {
        nifty.gotoScreen("login");
        switchToScreen = "login";
    }

    @Override
    public void leaveState(@Nonnull GameContainer container) {
    }
}
