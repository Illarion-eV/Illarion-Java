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
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.Login;
import org.newdawn.slick.state.StateBasedGame;


public final class LoginScreenController implements ScreenController, KeyInputHandler {

    private Nifty nifty;
    private Screen screen;

    private TextField nameTxt;
    private TextField passwordTxt;
    private CheckBox savePassword;


    private boolean notifyResolutionChanged;
    private boolean firstStart = true;
    private Element popupError;

    public LoginScreenController(final StateBasedGame game) {
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
    }

    @Override
    public void onStartScreen() {
        if (!firstStart) {
            final Login login = Login.getInstance();
            passwordTxt.setText(login.getPassword());
        }

        if (notifyResolutionChanged) {
            nifty.resolutionChanged();
            notifyResolutionChanged = false;
        }
    }


    public void resolutionChanged() {
        notifyResolutionChanged = true;
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
        login.setLoginData(nameTxt.getText(), passwordTxt.getText());

        login.storeData(savePassword.isChecked());

        login.requestCharacterList();

        if (login.hasError()) {
            final Label errorText = popupError.findNiftyControl("#errorText", Label.class);
            errorText.getElement().getRenderer(TextRenderer.class).setLineWrapping(true);
            errorText.setText(login.getErrorText());
            nifty.showPopup(screen, popupError.getId(), popupError.findElementByName("#closeButton"));

            return;
        }
        firstStart = false;
        nifty.gotoScreen("charSelect");
    }

    private void closeError() {
        nifty.closePopup(popupError.getId(), new EndNotify() {
            @Override
            public void perform() {
                nameTxt.getElement().setFocus();
            }
        });
    }

    public void createLabel() {
        final PanelBuilder builder = new PanelBuilder() {{
            width("200px");
            height("200px");
            backgroundColor("#f00f");
            valignCenter();
            alignCenter();
        }};
        final String myX = Integer.toString(nifty.getNiftyMouse().getX()) + "px";
        final String myY = Integer.toString(nifty.getNiftyMouse().getY()) + "px";
        builder.x(myX);
        builder.y(myY);
        final Element parent = screen.findElementByName("windows");
        builder.build(nifty, screen, parent);

    }

    private void options() {
        firstStart = false;
        nifty.gotoScreen("options");
    }

    @Override
    public boolean keyEvent(final NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyInputEvent.SubmitText) {
            login();
            return true;
        }
        return false;
    }
}
