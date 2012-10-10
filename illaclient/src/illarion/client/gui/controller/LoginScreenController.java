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
package illarion.client.gui.controller;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.Login;
import illarion.client.util.Lang;

/**
 * This is the screen controller that takes care of displaying the login screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LoginScreenController implements ScreenController, KeyInputHandler {
    /**
     * The instance of the Nifty-GUI that was bound to this controller.
     */
    private Nifty nifty;

    /**
     * The screen this controller is a part of.
     */
    private Screen screen;

    /**
     * The text field that contains the login name.
     */
    private TextField nameTxt;

    /**
     * The text field that contains the password.
     */
    private TextField passwordTxt;

    /**
     * The checkbox that is ticked in case the password is supposed to be saved.
     */
    private CheckBox savePassword;

    /**
     * The generated popup that is shown in case a error occurred during the login.
     */
    private Element popupError;

    /**
     * The generated popup that is shown while the client is busy fetching the characters from the server.
     */
    private Element popupReceiveChars;

    /**
     * This variable is set true in case the popup is visible.
     */
    private boolean popupIsVisible;

    /**
     * This value is set {@code true} in case a new response was received from the server that needs to be processed
     * upon the next update loop.
     */
    private boolean receivedLoginResponse;

    /**
     * The last error code that was received from the server. This will be {@code 0} in case there was no error or
     * any larger value in case there is a error.
     */
    private int lastErrorCode;

    /**
     * Get the text that describes a error code.
     *
     * @param error the error code
     * @return the localized text that describes the error for the player
     */
    public static String getErrorText(final int error) {
        return Lang.getMsg("login.error." + Integer.toString(error));
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        nameTxt = screen.findNiftyControl("nameTxt", TextField.class);
        passwordTxt = screen.findNiftyControl("passwordTxt", TextField.class);
        savePassword = screen.findNiftyControl("savePassword", CheckBox.class);

        nameTxt.getElement().addInputHandler(this);
        passwordTxt.getElement().addInputHandler(this);

        final Login login = Login.getInstance();
        login.restoreLoginData();
        nameTxt.setText(login.getLoginName());
        passwordTxt.setText(login.getPassword());
        savePassword.setChecked(login.storePassword());

        popupError = nifty.createPopup("loginError");
        popupReceiveChars = nifty.createPopup("receivingCharacters");
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    /**
     * This function is called in case the login button is clicked.
     *
     * @param topic the topic of the event
     * @param event the data of the event
     */
    @NiftyEventSubscriber(id = "loginBtn")
    public void onLoginButtonClicked(final String topic, final ButtonClickedEvent event) {
        login();
    }

    /**
     * This function is called in case the option button is clicked.
     *
     * @param topic the topic of the event
     * @param event the data of the event
     */
    @NiftyEventSubscriber(id = "optionBtn")
    public void onOptionsButtonClicked(final String topic, final ButtonClickedEvent event) {
        options();
    }

    /**
     * This function is called in case the close button in the error popup is clicked.
     *
     * @param topic the topic of the event
     * @param event the data of the event
     */
    @NiftyEventSubscriber(id = "errorButtonClose")
    public void onCloseErrorButtonClicked(final String topic, final ButtonClickedEvent event) {
        closeError();
    }

    /**
     * This function triggers the login process. It will request the character list of the player from the server.
     */
    private void login() {
        nifty.showPopup(screen, popupReceiveChars.getId(), null);
        final Login login = Login.getInstance();
        login.setLoginData(nameTxt.getRealText(), passwordTxt.getRealText());

        login.storeData(savePassword.isChecked());

        login.requestCharacterList(new Login.RequestCharListCallback() {
            @Override
            public void finishedRequest(final int errorCode) {
                lastErrorCode = errorCode;
                receivedLoginResponse = true;

                nifty.closePopup(popupReceiveChars.getId());
            }
        });
    }

    /**
     * This function has to be called at every update loop of Nifty. It will ensure that all updates are processed
     * synchronized to the Nifty-GUI update loop.
     */
    public void update() {
        if (!receivedLoginResponse) {
            return;
        }

        receivedLoginResponse = false;

        if (lastErrorCode > 0) {
            final Label errorText = popupError.findNiftyControl("#errorText", Label.class);
            errorText.setText(getErrorText(lastErrorCode));
            nifty.showPopup(screen, popupError.getId(), popupError.findElementByName("#closeButton"));
            popupIsVisible = true;
        } else {
            nifty.gotoScreen("charSelect");
        }
    }

    /**
     * This function closes the error popup that is displayed in case the login to the server failed.
     */
    private void closeError() {
        popupIsVisible = false;
        nifty.closePopup(popupError.getId(), new EndNotify() {
            @Override
            public void perform() {
                nameTxt.getElement().setFocus();
            }
        });
    }

    /**
     * This function switches the screen to the option screen.
     */
    private void options() {
        nifty.gotoScreen("options");
    }

    @Override
    public boolean keyEvent(final NiftyInputEvent inputEvent) {
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
}
