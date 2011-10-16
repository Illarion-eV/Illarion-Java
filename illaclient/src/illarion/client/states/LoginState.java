package illarion.client.states;

import illarion.client.gui.controller.CharScreenController;
import illarion.client.gui.controller.LoginScreenController;

import org.illarion.nifty.slick.NiftyGameState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This game state is used to display the login and character selection dialog.
 * Also the option dialog is displayed in this state.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class LoginState extends NiftyGameState {
    /**
     * Create the game state that handles the login with the identifier that
     * is needed to access it.
     * 
     * @param slickGameStateId the ID of this state
     */
    public LoginState(final int slickGameStateId) {
        super(slickGameStateId);
    }
    
    /**
     * initialize.
     * 
     * @param container GameContainer
     * @param game StateBasedGame
     * @throws SlickException exception
     */
    public void init(final GameContainer container, final StateBasedGame game)
        throws SlickException {
        
        fromXml("illarion/client/gui/xml/login.xml", new LoginScreenController(game), new CharScreenController(game));
        getNifty().addXml("illarion/client/gui/xml/charselect.xml");
        getNifty().addXml("illarion/client/gui/xml/options.xml");
        
        getNifty().gotoScreen("login");
        
        super.init(container, game);
    }
}
