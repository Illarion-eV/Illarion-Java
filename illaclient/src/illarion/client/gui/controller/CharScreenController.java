package illarion.client.gui.controller;


import illarion.client.Login;
import illarion.client.util.SessionManager;
import illarion.client.world.Game;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.controls.ListBox;

public class CharScreenController implements ScreenController {

    private Nifty nifty;
	private Screen screen;
	
	private ListBox<String> listBox;
	
    @SuppressWarnings("unchecked")
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
    	this.screen = screen;
        listBox = (ListBox<String>) screen.findNiftyControl("myListBox", ListBox.class);
    	fillMyListBox();
    }

    @Override
    public void onStartScreen() {
    	
    }

    @Override
    public void onEndScreen() {

    }
	
	private void fillMyListBox() {
		
		final Login login = Login.getInstance();
		for (int i = 0; i < login.getCharacterCount(); i++) {
		    listBox.addItem(login.getCharacterName(i));
		}
	}
	
	public void play() {
        Login.getInstance().selectCharacter(listBox.getFocusItemIndex());
        
        nifty.gotoScreen("loading");
        SessionManager.getInstance().loadSession();
	}
	
	public void logout() {
		nifty.gotoScreen("login");
	}
}
