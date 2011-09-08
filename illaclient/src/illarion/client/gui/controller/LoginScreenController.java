package illarion.client.gui.controller;

import illarion.common.util.LoadingManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public final class LoginScreenController implements ScreenController {

	private Nifty nifty;
	private TextField nameTxt;
	private TextField passwordTxt;
	private Button loginBtn;
	private ListBox<?> charList;
	
    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.nifty = nifty;
    	nameTxt = screen.findNiftyControl("nameTxt", TextField.class);
    	passwordTxt = screen.findNiftyControl("passwordTxt", TextField.class);
    	loginBtn = screen.findNiftyControl("loginBtn", Button.class);
    	charList = nifty.getScreen("charSelect").findNiftyControl("myListBox", ListBox.class);
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {

    }
    
    public void login() {
    	nifty.gotoScreen("charSelect");
    }
}
