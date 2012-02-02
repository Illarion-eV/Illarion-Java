package illarion.client.states;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.slick2d.NiftyBasicGameState;
import de.lessvoid.nifty.slick2d.NiftyOverlayBasicGameState;
import de.lessvoid.nifty.slick2d.input.SlickSlickInputSystem;
import illarion.client.gui.controller.LoadScreenController;
import illarion.client.input.InputReceiver;
import illarion.client.loading.Loading;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This game state is active while the game loads. It takes care for showing the
 * loading screen and to trigger the actual loading.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LoadingState extends NiftyOverlayBasicGameState {

    private LoadScreenController controller;

    private final int id;

    public LoadingState(final int slickGameStateId) {
        id = slickGameStateId;
    }

    private final Logger log = Logger.getLogger(LoadingState.class.getName());

    @Override
    protected void prepareNifty(final Nifty nifty, final StateBasedGame game) {
        controller = new LoadScreenController(game);
        nifty.registerScreenController(controller);
        nifty.fromXmlWithoutStartScreen("illarion/client/gui/xml/loading.xml");
    }
    
    public void enterState(final GameContainer container,
        final StateBasedGame game) throws SlickException {
        Loading.enlistMissingComponents();
        getNifty().gotoScreen("loading");
    }

    @Override
    protected void initGameAndGUI(GameContainer container, StateBasedGame game) throws SlickException {
        initNifty(container, game, new SlickSlickInputSystem(new InputReceiver()));
    }

    @Override
    protected void leaveState(GameContainer container, StateBasedGame game) throws SlickException {

    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    protected void renderGame(GameContainer container, StateBasedGame game,
        Graphics g) throws SlickException {

        g.clear();
        
        final int remaining = LoadingList.get().getRemainingResources();
        final int total = LoadingList.get().getTotalResources();

        if (remaining > 0) {
            controller.setProgress((float) (total - remaining) / total);
        } else {
            controller.loadingDone();
        }
        
        if (remaining == 0) {
            return;
        }

        SlickCallable.enterSafeBlock();
        try {
            LoadingList.get().getNext().load();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to load resource.", e);
        } finally {
            SlickCallable.leaveSafeBlock();
        }
    }

    @Override
    protected void updateGame(GameContainer container, StateBasedGame game, int delta) throws SlickException {

    }
}
