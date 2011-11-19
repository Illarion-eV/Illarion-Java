package illarion.client.states;

import illarion.client.gui.controller.CharScreenController;
import illarion.client.gui.controller.LoginScreenController;
import illarion.client.resources.SongFactory;
import illarion.client.resources.SoundFactory;
import illarion.client.resources.loaders.SongLoader;
import illarion.client.resources.loaders.SoundLoader;
import illarion.client.world.World;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.slick2d.NiftyBasicGameState;

/**
 * This game state is used to display the login and character selection dialog.
 * Also the option dialog is displayed in this state.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class LoginState extends NiftyBasicGameState {
    private final int id;

    /**
     * Create the game state that handles the login with the identifier that is
     * needed to access it.
     * 
     * @param slickGameStateId the ID of this state
     */
    public LoginState(final int slickGameStateId) {
        super("login");
        id = slickGameStateId;
    }

    @Override
    protected void prepareNifty(Nifty nifty, StateBasedGame game) {
        nifty.registerScreenController(new LoginScreenController(game),
            new CharScreenController(game));
        nifty.addXml("illarion/client/gui/xml/login.xml");
        nifty.addXml("illarion/client/gui/xml/charselect.xml");
        nifty.addXml("illarion/client/gui/xml/options.xml");
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void enterState(final GameContainer container,
        final StateBasedGame game) throws SlickException {
        super.enterState(container, game);
    }
}
