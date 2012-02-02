package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.Login;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public final class LoadScreenController implements ScreenController {

	private Nifty nifty;
	private ProgressbarControl progress;
	
	private final StateBasedGame game;
	
    public LoadScreenController(StateBasedGame game) {
        this.game = game;
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.nifty = nifty;
    	progress = screen.findControl("loading", ProgressbarControl.class);
    }

    @Override
    public void onStartScreen() {

    }
    
    private boolean loadingDoneCalled = false;
    
    public void loadingDone() {
        if (loadingDoneCalled) {
            return;
        }
        loadingDoneCalled = true;
        
        Login.getInstance().login();

        game.enterState(illarion.client.Game.STATE_PLAYING, new FadeOutTransition(), null);
    }
    
    public void setProgress(final float progressValue) {
        if(progress != null)
			progress.setProgress(progressValue);
    }

    @Override
    public void onEndScreen() {
        // nothing to do
    }
}
