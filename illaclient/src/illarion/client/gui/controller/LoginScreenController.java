package illarion.client.gui.controller;

import illarion.client.Login;
import illarion.common.util.LoadingManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public final class LoginScreenController implements ScreenController {

	private Nifty nifty;
	private TextField nameTxt;
	private TextField passwordTxt;
	private ListBox<?> charList;
	
    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.nifty = nifty;
    	nameTxt = screen.findNiftyControl("nameTxt", TextField.class);
    	passwordTxt = screen.findNiftyControl("passwordTxt", TextField.class);
    	charList = nifty.getScreen("charSelect").findNiftyControl("myListBox", ListBox.class);
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {

    }
    
    public void login() {
        final Login login = Login.getInstance();
        login.setLoginData(nameTxt.getText(), passwordTxt.getText());
        login.requestCharacterList();
        
        if (login.hasError()) {
            return;
        }
    	nifty.gotoScreen("charSelect");
    }
}
