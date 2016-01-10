/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;
import illarion.client.resources.SongFactory;
import illarion.client.util.AudioPlayer;
import illarion.client.util.Lang;
import illarion.client.util.account.AccountSystem;
import illarion.client.util.account.AccountSystemEndpoint;
import illarion.client.util.account.Credentials;
import illarion.client.util.account.response.AccountGetResponse;
import org.illarion.engine.Engine;
import org.illarion.engine.sound.Music;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * This is the screen controller that takes care of displaying the login screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LoginScreenController implements ScreenController, KeyInputHandler {
    /**
     * The engine that is used in this game instance.
     */
    @Nonnull
    private final Engine engine;

    /**
     * The reference to the account system.
     */
    @Nonnull
    private final AccountSystem accountSystem;

    /**
     * The text field that contains the login name.
     */
    @Nullable
    private TextField nameTxt;

    /**
     * The instance of the Nifty-GUI that was bound to this controller.
     */
    @Nullable
    private Nifty nifty;

    /**
     * The text field that contains the password.
     */
    @Nullable
    private TextField passwordTxt;

    /**
     * The generated popup that is shown in case a error occurred during the login.
     */
    @Nullable
    private Element popupError;

    /**
     * This variable is set true in case the popup is visible.
     */
    private boolean popupIsVisible;

    /**
     * The generated popup that is shown while the client is busy fetching the characters from the server.
     */
    @Nullable
    private Element popupReceiveChars;

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
    @Nullable
    private Credentials credentials;

    @Nullable
    private String errorMessage;

    public LoginScreenController(@Nonnull Engine engine, @Nonnull AccountSystem accountSystem) {
        this.engine = engine;
        this.accountSystem = accountSystem;
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

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        nameTxt = screen.findNiftyControl("nameTxt", TextField.class);
        passwordTxt = screen.findNiftyControl("passwordTxt", TextField.class);
        savePassword = screen.findNiftyControl("savePassword", CheckBox.class);

        assert nameTxt != null: "The login name text field was not found.";
        assert passwordTxt != null: "The password text field was not found.";
        assert savePassword != null: "The save password checkbox was not found.";

        assert nameTxt.getElement() != null: "Binding the login text field is not done.";
        assert passwordTxt.getElement() != null: "Binding the password text field is not done.";

        nameTxt.getElement().addInputHandler(this);
        passwordTxt.getElement().addInputHandler(this);

        List<AccountSystemEndpoint> endpointList = accountSystem.getEndPoints();

        if (endpointList.size() == 1) {
            Element serverPanel = screen.findElementById("serverPanel");
            assert serverPanel != null: "Failed to locate the server planel on the login screen.";
            serverPanel.hide();
        }

        //noinspection unchecked
        server = screen.findNiftyControl("server", DropDown.class);
        assert server != null: "Failed to locate server selection drop down.";

        for (AccountSystemEndpoint endpoint : endpointList) {
            server.addItem(endpoint.getName());
        }
        server.selectItemByIndex(IllaClient.getCfg().getInteger("server"));
        credentials = new Credentials(endpointList.get(server.getSelectedIndex()), IllaClient.getCfg());
        restoreLoginData();

        popupError = nifty.createPopup("loginError");
        popupReceiveChars = nifty.createPopup("receivingCharacters");

        nifty.subscribeAnnotations(this);
    }

    @Override
    public void onStartScreen() {
        assert nameTxt != null;
        assert passwordTxt != null;

        AudioPlayer audioPlayer = AudioPlayer.getInstance();
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

        if (errorMessage == null) {
            closeError();
        } else {
            String msg = errorMessage;
            errorMessage = null;
            showError(msg);
        }
    }

    @Override
    public void onEndScreen() {
    }

    private void restoreLoginData() {
        assert nameTxt != null: "Binding the login field seems to have failed.";
        assert passwordTxt != null: "Binding the password field seems to have failed.";
        assert savePassword != null: "Binding the store check box seems to have failed.";
        assert credentials != null: "The credentials are not set to a valid object.";

        nameTxt.setText(credentials.getUserName());
        passwordTxt.setText(credentials.getPassword());
        savePassword.setChecked(credentials.isStorePassword());
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
        assert nifty != null;
        assert popupError != null;
        assert popupError.getId() != null;
        assert nameTxt != null;
        assert nameTxt.getElement() != null;

        if (popupIsVisible) {
            popupIsVisible = false;
            nifty.closePopup(popupError.getId(), () -> nameTxt.getElement().setFocus());
        }
    }

    public void showError(@Nonnull String message) {
        assert nifty != null;
        assert popupError != null;
        assert popupError.getId() != null;

        if ((screen == null) || !screen.equals(nifty.getCurrentScreen())) {
            errorMessage = message;
        } else {
            Label errorText = popupError.findNiftyControl("#errorText", Label.class);
            assert errorText != null;

            errorText.setText(message);
            if (!popupIsVisible) {
                nifty.showPopup(screen, popupError.getId(), popupError.findElementById("#closeButton"));
                popupIsVisible = true;
            }
        }
    }

    /**
     * This function is called in case the credits button is clicked.
     *
     * @param topic the topic of the event
     * @param event the data of the event
     */
    @NiftyEventSubscriber(id = "creditsBtn")
    public void onCreditsButtonClicked(String topic, ButtonClickedEvent event) {
        assert nifty != null;

        nifty.gotoScreen("creditsStart");
    }

    @NiftyEventSubscriber(id = "registerBtn")
    public void onRegisterButtonClicked(String topic, ButtonClickedEvent event) {
        assert nifty != null;

        nifty.gotoScreen("register");
    }

    @NiftyEventSubscriber(id = "nameTxt")
    public void onNameTxtFocusGained(String topic, FocusGainedEvent event) {
        assert nameTxt != null;

        Element nameTxtElement = nameTxt.getElement();
        if (nameTxtElement != null) {
            TextRenderer renderer = nameTxtElement.getRenderer(TextRenderer.class);
            if (renderer != null) {
                renderer.setSelection(0, nameTxt.getDisplayedText().length() - 1);
            }
        }
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
        Nifty nifty = Objects.requireNonNull(this.nifty);
        Screen screen = Objects.requireNonNull(this.screen);
        TextField nameTxt = Objects.requireNonNull(this.nameTxt);
        TextField passwordTxt = Objects.requireNonNull(this.passwordTxt);
        CheckBox savePassword = Objects.requireNonNull(this.savePassword);
        Credentials credentials = Objects.requireNonNull(this.credentials);
        Element popupReceiveChars = Objects.requireNonNull(this.popupReceiveChars);
        assert popupReceiveChars.getId() != null: "ID of the receiving characters popup is not set.";

        credentials.setUserName(nameTxt.getRealText());
        credentials.setPassword(passwordTxt.getRealText());
        credentials.setStorePassword(savePassword.isChecked());
        credentials.storeCredentials();

        if (credentials.getEndpoint().isUseConfigParameters()) {
            if (!IllaClient.getCfg().getBoolean("customServer.accountSystem")) {
                Screen enteringScreen = nifty.getScreen("entering");
                assert enteringScreen != null;
                EnteringScreenController controller = (EnteringScreenController) enteringScreen.getScreenController();
                controller.setLoginInformation(credentials.getEndpoint(), "customserver",
                        credentials.getUserName(), credentials.getPassword());
                nifty.gotoScreen(enteringScreen.getScreenId());
                return;
            }
        }

        nifty.showPopup(screen, popupReceiveChars.getId(), null);

        accountSystem.setAuthentication(credentials);

        ListenableFuture<AccountGetResponse> response = accountSystem.getAccountInformation();
        Futures.addCallback(response, new FutureCallback<AccountGetResponse>() {
            @Override
            public void onSuccess(@Nullable AccountGetResponse result) {
                nifty.closePopup(popupReceiveChars.getId());
                if (result == null) {
                    return;
                }

                @Nullable Screen charSelectScreen = nifty.getScreen("charSelect");
                if (charSelectScreen == null) {
                    throw new IllegalStateException("The character select screen was not found! This is bad.");
                }
                @Nonnull ScreenController charScreenController = charSelectScreen.getScreenController();
                if (charScreenController instanceof CharScreenController) {
                    ((CharScreenController) charScreenController).applyAccountData(credentials, result);
                }
                nifty.gotoScreen(charSelectScreen.getScreenId());
            }

            @Override
            public void onFailure(@Nonnull Throwable t) {
                assert popupError != null;
                assert popupError.getId() != null;

                nifty.closePopup(popupReceiveChars.getId());
                nifty.scheduleEndOfFrameElementAction(() -> showError(t.getLocalizedMessage()), null);
            }
        }, new NiftyExecutor(nifty));
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
        List<AccountSystemEndpoint> endpoints = accountSystem.getEndPoints();
        int selectedIndex = data.getSelectionItemIndex();
        if ((selectedIndex >= 0) && (selectedIndex < endpoints.size())) {
            AccountSystemEndpoint endpoint = endpoints.get(selectedIndex);
            assert endpoint != null;

            accountSystem.setEndpoint(endpoint);
            credentials = new Credentials(endpoint, IllaClient.getCfg());
            restoreLoginData();
        }
    }
}
