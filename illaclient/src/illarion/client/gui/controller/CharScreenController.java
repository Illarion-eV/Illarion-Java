package illarion.client.gui.controller;


import illarion.client.Login;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.controls.ListBox;

public class CharScreenController implements ScreenController {

	private Screen screen;
	
    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.screen = screen;
    	fillMyListBox();
    }

    @Override
    public void onStartScreen() {
    	
    }

    @Override
    public void onEndScreen() {

    }
	
	public void fillMyListBox() {
		@SuppressWarnings("unchecked")
        ListBox<String> listBox = (ListBox<String>) screen.findNiftyControl("myListBox", ListBox.class);
		
		final Login login = Login.getInstance();
		for (int i = 0; i < login.getCharacterCount(); i++) {
		    listBox.addItem(login.getCharacterName(i));
		}
	}
}
