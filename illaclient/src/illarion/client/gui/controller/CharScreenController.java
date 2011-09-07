package illarion.client.gui.controller;


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
		ListBox listBox = screen.findNiftyControl("myListBox", ListBox.class);
		listBox.addItem("a");
		listBox.addItem("b");
		listBox.addItem("c");
	}
}
