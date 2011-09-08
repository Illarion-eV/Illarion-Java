package illarion.client.gui.controller;

import illarion.client.Login;
import illarion.client.util.SessionManager;
import illarion.client.world.Game;
import illarion.common.util.LoadingManager;
import illarion.common.util.LoadingManager.LoadingMonitor;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public final class LoadScreenController implements ScreenController, LoadingMonitor {

	private Nifty nifty;
	private ProgressbarControl progress;
	
    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.nifty = nifty;
    	progress = screen.findControl("loading", ProgressbarControl.class); 
    }

    @Override
    public void onStartScreen() {
        LoadingManager.getInstance().setMonitor(this);

    }

    @Override
    public void onEndScreen() {
        LoadingManager.getInstance().setMonitor(null);
    }
    
    public void setProgress(final float progressValue) {
    	progress.setProgress(progressValue);
    	if (progressValue >= 0.99f) {

    	    if (SessionManager.getInstance().isStarted()) {
                SessionManager.getInstance().startSession();
                Login.getInstance().login();
                
                if (Game.getDisplay() != null) {
                    nifty.gotoScreen("gamescreen");
                } else {
                    System.out.println("Display not ready");
                }
    	    }
    	}
    }

    @Override
    public void updateState(float state) {
        setProgress(state);
    }
}
