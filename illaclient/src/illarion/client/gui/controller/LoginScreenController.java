package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public final class LoginScreenController implements ScreenController {

	private Nifty nifty;
	private TextField nameTxt;
	private TextField passwordTxt;
	private ListBox charList;
	
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
    	//updateCharacters();
    	nifty.gotoScreen("charSelect");
    }
}
