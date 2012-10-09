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
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.Login;

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
     * This variable is set true in case the popup is visible.
     */
    private boolean popupIsVisible;

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
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }

    @NiftyEventSubscriber(id = "loginBtn")
    public void onLoginButtonClicked(final String topic, final ButtonClickedEvent event) {
        login();
    }

    @NiftyEventSubscriber(id = "optionBtn")
    public void onOptionsButtonClicked(final String topic, final ButtonClickedEvent event) {
        options();
    }

    @NiftyEventSubscriber(id = "errorButtonClose")
    public void onCloseErrorButtonClicked(final String topic, final ButtonClickedEvent event) {
        closeError();
    }

    private void login() {
        final Login login = Login.getInstance();
        login.setLoginData(nameTxt.getRealText(), passwordTxt.getRealText());

        login.storeData(savePassword.isChecked());

        login.requestCharacterList();

        if (login.hasError()) {
            final Label errorText = popupError.findNiftyControl("#errorText", Label.class);
            errorText.getElement().getRenderer(TextRenderer.class).setLineWrapping(true);
            errorText.setText(login.getErrorText());
            nifty.showPopup(screen, popupError.getId(), popupError.findElementByName("#closeButton"));
            popupIsVisible = true;

            return;
        }
        nifty.gotoScreen("charSelect");
    }

    private void closeError() {
        nifty.closePopup(popupError.getId(), new EndNotify() {
            @Override
            public void perform() {
                nameTxt.getElement().setFocus();
                popupIsVisible = false;
            }
        });
    }

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
