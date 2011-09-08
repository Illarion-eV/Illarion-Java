package illarion.client.gui.controller;


import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.controls.ListBox;

public class CharScreenController implements ScreenController {

	private Nifty nifty;;
	private Screen screen;
	
    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.nifty = nifty;
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
		ListBox listBox = screen.findNiftyControl("myListBox", ListBox.class);
		listBox.addItem("Character One");
		listBox.addItem("Character Two");
		listBox.addItem("Character Three");
		listBox.addItem("Character Four");
		listBox.addItem("Character Five");
	}
	
	public void play() {
		nifty.gotoScreen("loading");
	}
	
	public void logout() {
		nifty.gotoScreen("login");
	}
}
