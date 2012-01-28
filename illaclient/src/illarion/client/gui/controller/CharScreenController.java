package illarion.client.gui.controller;


import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
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
        
        game.enterState(Game.STATE_LOADING, new FadeOutTransition(), new FadeInTransition());
	}
	
	public void logout() {
		nifty.gotoScreen("login");
	}
}
