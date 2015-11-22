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
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.Game;
import illarion.client.IllaClient;
import illarion.client.Login;
import illarion.client.Servers;
import illarion.client.resources.SongFactory;
import illarion.client.util.AudioPlayer;
import illarion.client.util.Lang;
import org.illarion.engine.Engine;
import org.illarion.engine.sound.Music;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the screen controller that takes care of displaying the login screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LoginScreenController implements ScreenController, KeyInputHandler {
    /**
     * This is the logging instance for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginScreenController.class);
    /**
     * The engine that is used in this game instance.
     */
    @Nonnull
    private final Engine engine;
    /**
     * The game that is the parent of this class.
     */
    @Nonnull
    private final Game game;
    /**
     * The last error code that was received from the server. This will be {@code 0} in case there was no error or
     * any larger value in case there is a error.
     */
    private int lastErrorCode;
    /**
     * The text field that contains the login name.
     */
    @Nonnull
    private TextField nameTxt;
    /**
     * The instance of the Nifty-GUI that was bound to this controller.
     */
    private Nifty nifty;
    /**
     * The text field that contains the password.
     */
    @Nonnull
    private TextField passwordTxt;
    /**
     * The generated popup that is shown in case a error occurred during the login.
     */
    private Element popupError;
    /**
     * This variable is set true in case the popup is visible.
     */
    private boolean popupIsVisible;
    /**
     * The generated popup that is shown while the client is busy fetching the characters from the server.
     */
    private Element popupReceiveChars;
    /**
     * This value is set {@code true} in case a new response was received from the server that needs to be processed
     * upon the next update loop.
     */
    private boolean receivedLoginResponse;
    /**
     * The checkbox that is ticked in case the password is supposed to be saved.
     */
    @Nullable
    private CheckBox savePassword;
    /**
     * The screen this controller is a part of.
     */
    private Screen screen;
    /**
     * The drop down box is used to select a server.
     */
    @Nullable
    private DropDown<String> server;

    public LoginScreenController(@Nonnull Game game, @Nonnull Engine engine) {
        this.game = game;
        this.engine = engine;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        nameTxt = screen.findNiftyControl("nameTxt", TextField.class);
        passwordTxt = screen.findNiftyControl("passwordTxt", TextField.class);
        savePassword = screen.findNiftyControl("savePassword", CheckBox.class);

        nameTxt.getElement().addInputHandler(this);
        passwordTxt.getElement().addInputHandler(this);

        Login login = Login.getInstance();
        login.restoreServer();
        restoreLoginData();

        if (IllaClient.DEFAULT_SERVER == Servers.Illarionserver) {
            @Nullable Element serverPanel = screen.findElementById("serverPanel");
            if (serverPanel != null) {
                serverPanel.hide();
            } else {
                LOGGER.error("Failed to find server panel on the screen.");
            }
        } else {
            //noinspection unchecked
            server = screen.findNiftyControl("server", DropDown.class);
            if (server != null) {
                server.addItem("${login-bundle.server.develop}");
                server.addItem("${login-bundle.server.test}");
                server.addItem("${login-bundle.server.game}");
                server.addItem("${login-bundle.server.custom}");
                server.selectItemByIndex(IllaClient.getCfg().getInteger("server"));
            } else {
                LOGGER.error("Failed to find server drop down on the login screen.");
            }
        }

        popupError = nifty.createPopup("loginError");
        popupReceiveChars = nifty.createPopup("receivingCharacters");

        nifty.subscribeAnnotations(this);
    }

    private void restoreLoginData() {
        Login login = Login.getInstance();
        login.restoreLoginData();
        nameTxt.setText(login.getLoginName());
        passwordTxt.setText(login.getPassword());
        savePassword.setChecked(login.getStorePassword());
    }

    @Override
    public void onStartScreen() {
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        audioPlayer.initAudioPlayer(engine.getSounds());
        Music illarionTheme = SongFactory.getInstance().getSong(2, engine.getAssets().getSoundsManager());
        audioPlayer.setLastMusic(illarionTheme);
        if (IllaClient.getCfg().getBoolean("musicOn")) {
            if (illarionTheme != null) {
                if (!audioPlayer.isCurrentMusic(illarionTheme)) {
                    // may be null in case OpenAL is not working
                    audioPlayer.playMusic(illarionTheme);
                }
            }
        }
        if (nameTxt.getDisplayedText().isEmpty()) {
            nameTxt.setFocus();
        } else {
            passwordTxt.setFocus();
        }
    }

    @Override
    public void onEndScreen() {
    }

    @Override
    public boolean keyEvent(@Nonnull NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyStandardInputEvent.SubmitText) {
            if (popupIsVisible) {
                closeError();
            } else {
                login();
            }
            return true;
        }
        return false;
    }

    /**
     * This function is called in case the close button in the error popup is clicked.
     *
     * @param topic the topic of the event
     * @param event the data of the event
     */
    @NiftyEventSubscriber(id = "errorButtonClose")
    public void onCloseErrorButtonClicked(String topic, ButtonClickedEvent event) {
        closeError();
    }

    /**
     * This function closes the error popup that is displayed in case the login to the server failed.
     */
    private void closeError() {
        popupIsVisible = false;
        nifty.closePopup(popupError.getId(), () -> nameTxt.getElement().setFocus());
    }

    /**
     * This function is called in case the credits button is clicked.
     *
     * @param topic the topic of the event
     * @param event the data of the event
     */
    @NiftyEventSubscriber(id = "creditsBtn")
    public void onCreditsButtonClicked(String topic, ButtonClickedEvent event) {
        nifty.gotoScreen("creditsStart");
    }

    /**
     * This function is called in case the exit button is clicked.
     *
     * @param topic the topic of the event
     * @param event the data of the event
     */
    @NiftyEventSubscriber(id = "exitBtn")
    public void onExitButtonClicked(String topic, ButtonClickedEvent event) {
        IllaClient.ensureExit();
    }

    /**
     * This function is called in case the login button is clicked.
     *
     * @param topic the topic of the event
     * @param event the data of the event
     */
    @NiftyEventSubscriber(id = "loginBtn")
    public void onLoginButtonClicked(String topic, ButtonClickedEvent event) {
        login();
    }

    /**
     * This function triggers the login process. It will request the character list of the player from the server.
     */
    private void login() {
        nifty.showPopup(screen, popupReceiveChars.getId(), null);
        Login login = Login.getInstance();
        login.setLoginData(nameTxt.getRealText(), passwordTxt.getRealText());

        if (server != null) {
            login.applyServerByKey(server.getSelectedIndex());
        } else {
            login.setServer(Servers.Illarionserver);
        }

        login.storeData(savePassword.isChecked());

        if (login.isCharacterListRequired()) {
            //AccountSystem system = new AccountSystem(AccountSystem.LOCAL, nameTxt.getRealText(), passwordTxt.getRealText());
            //AccountInfo info = system.getAccountInformation();
            login.requestCharacterList(errorCode -> {
                lastErrorCode = errorCode;
                receivedLoginResponse = true;

                nifty.closePopup(popupReceiveChars.getId());
            });
        } else {
            engine.getSounds().stopMusic(15);
            game.enterState(Game.STATE_LOADING);
        }
    }

    /**
     * This function is called in case the option button is clicked.
     *
     * @param topic the topic of the event
     * @param event the data of the event
     */
    @NiftyEventSubscriber(id = "optionBtn")
    public void onOptionsButtonClicked(String topic, ButtonClickedEvent event) {
        options();
    }

    /**
     * This function switches the screen to the option screen.
     */
    private void options() {
        nifty.gotoScreen("options");
    }

    @NiftyEventSubscriber(id = "server")
    public void onServerChanged(@Nonnull String topic, @Nonnull DropDownSelectionChangedEvent<String> data) {
        Login.getInstance().applyServerByKey(server.getSelectedIndex());
        restoreLoginData();
    }

    @NiftyEventSubscriber(id = "server")
    public void onServerChangedEvent(String topic, @Nonnull DropDownSelectionChangedEvent<String> event) {
        if (event.getSelectionItemIndex() == 4) {
            nameTxt.setText(IllaClient.getCfg().getString("testserverLogin"));
            passwordTxt.setText(IllaClient.getCfg().getString("testserverPass"));
        } else {
            Login login = Login.getInstance();
            nameTxt.setText(login.getLoginName());
            passwordTxt.setText(login.getPassword());
        }
    }

    /**
     * This function has to be called at every update loop of Nifty. It will ensure that all updates are processed
     * synchronized to the Nifty-GUI update loop.
     */
    public void update() {
        if (!receivedLoginResponse || (nifty.getCurrentScreen() != screen)) {
            return;
        }

        receivedLoginResponse = false;

        if (lastErrorCode > 0) {
            Label errorText = popupError.findNiftyControl("#errorText", Label.class);
            errorText.setText(getErrorText(lastErrorCode));
            nifty.showPopup(screen, popupError.getId(), popupError.findElementById("#closeButton"));
            popupIsVisible = true;
        } else {
            @Nullable Screen charSelectScreen = nifty.getScreen("charSelect");
            if (charSelectScreen == null) {
                throw new IllegalStateException("The character select screen was not found! This is bad.");
            }
            @Nonnull ScreenController charScreenController = charSelectScreen.getScreenController();
            if (charScreenController instanceof CharScreenController) {
                ((CharScreenController) charScreenController).fillMyListBox();
            }
            nifty.gotoScreen(charSelectScreen.getScreenId());
        }
    }

    /**
     * Get the text that describes a error code.
     *
     * @param error the error code
     * @return the localized text that describes the error for the player
     */
    public static String getErrorText(int error) {
        return Lang.getMsg("login.error." + Integer.toString(error));
    }
}
