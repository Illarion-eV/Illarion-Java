package illarion.client.gui.controller;


import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.Game;
import illarion.client.Login;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class CharScreenController implements ScreenController {

    private Nifty nifty;
	private Screen screen;
	
	private ListBox<String> listBox;
	
	private final StateBasedGame game;
	private Label statusLabel;

    private boolean notifyResolutionChanged;
	
    public CharScreenController(StateBasedGame game) {
        this.game = game;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
    	this.screen = screen;
        listBox = (ListBox<String>) screen.findNiftyControl("myListBox", ListBox.class);
    	fillMyListBox();
    	statusLabel = screen.findNiftyControl("statusText", Label.class);
    	statusLabel.setHeight(new SizeValue("20" + SizeValue.PIXEL));
    	statusLabel.setWidth(new SizeValue("180" + SizeValue.PIXEL));
    }

    @Override
    public void onStartScreen() {
        if(notifyResolutionChanged)
        {
            nifty.resolutionChanged();
            notifyResolutionChanged = false;
        }
    }

    public void resolutionChanged()
    {
        notifyResolutionChanged = true;
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
        boolean found = Login.getInstance().selectCharacter(listBox.getFocusItemIndex());
        
        if(!found)
        {       	
        	statusLabel.setText("No character selected");
        	statusLabel.getElement().getParent().layoutElements();
        	return;
        }

        game.enterState(Game.STATE_LOADING, new FadeOutTransition(), null);
	}
	
	public void logout() {
		statusLabel.setText("");
		nifty.gotoScreen("login");
	}
}
