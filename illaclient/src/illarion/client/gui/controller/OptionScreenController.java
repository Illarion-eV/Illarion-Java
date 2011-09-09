package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public final class OptionScreenController implements ScreenController {

	private Nifty nifty;
	
    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.nifty = nifty;
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {

    }
    
    public void save() {
    	nifty.gotoScreen("login");
    }
    
    public void cancel() {
    	nifty.gotoScreen("login");
    }
}
