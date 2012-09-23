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
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.loaderv2.types.PanelType;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.Login;
import org.newdawn.slick.state.StateBasedGame;


public final class LoginScreenController implements ScreenController, KeyInputHandler {

    private Nifty nifty;
    private Screen screen;
    private PanelType panel;

    private TextField nameTxt;
    private TextField passwordTxt;
    private CheckBox savePassword;
    private Button loginBtn;
    private ListBox<?> charList;


    private boolean notifyResolutionChanged;
    private boolean firstStart = true;
    private Element popupError;

    private final StateBasedGame game;

    public LoginScreenController(StateBasedGame game) {
        this.game = game;
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        nameTxt = screen.findNiftyControl("nameTxt", TextField.class);
        passwordTxt = screen.findNiftyControl("passwordTxt", TextField.class);
        savePassword = screen.findNiftyControl("savePassword", CheckBox.class);
        loginBtn = screen.findNiftyControl("loginBtn", Button.class);
        charList = nifty.getScreen("charSelect").findNiftyControl("myListBox", ListBox.class);

        nameTxt.getElement().addInputHandler(this);
        passwordTxt.getElement().addInputHandler(this);

        final Login login = Login.getInstance();
        login.restoreLoginData();
        nameTxt.setText(login.getLoginName());
        passwordTxt.setText(login.getPassword());
        savePassword.setChecked(login.storePassword());

        popupError = nifty.createPopup("loginError");

//    	errorText = screen.findNiftyControl("errorText", Label.class);
//    	
//    	popupLogin = screen.findElementByName("popupLoggingIn");
//        popupError = screen.findElementByName("error");
    }

    @Override
    public void onStartScreen() {
        /* Element myLabel=screen.findElementByName("testHint");
       myLabel.setConstraintX(new SizeValue("100px"));
       myLabel.setConstraintY(new SizeValue("200px"));*/

        // For some reason the passwordTxt Text get changed to the dots when changing screens, so we need to reset the password
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

    public void login() {
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

    public void closeError() {
        nifty.closePopup(popupError.getId(), new EndNotify() {
            @Override
            public void perform() {
                nameTxt.getElement().setFocus();
            }
        });
    }

    public void createLabel() {
        PanelBuilder builder = new PanelBuilder() {{
            width("200px");
            height("200px");
            backgroundColor("#f00f");
            valignCenter();
            alignCenter();
        }};
        String myX = Integer.toString(nifty.getNiftyMouse().getX()) + "px";
        String myY = Integer.toString(nifty.getNiftyMouse().getY()) + "px";
        builder.x(myX);
        builder.y(myY);
        Element parent = screen.findElementByName("windows");
        builder.build(nifty, screen, parent);

    }

    public void cancelLogin() {
//        nifty.closePopup(popupLogin.getId());
//        nifty.closePopup(popupError.getId());
    }

    public void options() {
        firstStart = false;
        nifty.gotoScreen("options");
    }

    @Override
    public boolean keyEvent(NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyInputEvent.SubmitText) {
            login();
            return true;
        }
        return false;
    }
}
