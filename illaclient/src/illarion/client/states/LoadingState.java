package illarion.client.states;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import illarion.client.gui.controller.LoadScreenController;
import illarion.client.loading.Loading;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.state.StateBasedGame;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.slick2d.NiftyBasicGameState;

/**
 * This game state is active while the game loads. It takes care for showing the
 * loading screen and to trigger the actual loading.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LoadingState extends NiftyBasicGameState {

    private LoadScreenController controller;

    private final int id;

    public LoadingState(final int slickGameStateId) {
        super("loading");
        id = slickGameStateId;
    }

    private final Logger log = Logger.getLogger(LoadingState.class.getName());

    @Override
    protected void prepareNifty(final Nifty nifty, final StateBasedGame game) {
        controller = new LoadScreenController(game);
        nifty.fromXml("illarion/client/gui/xml/loading.xml", "loading",
            controller);
        
        Loading.enlistMissingComponents();
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    protected void renderGame(GameContainer container, StateBasedGame game,
        Graphics g) throws SlickException {
        super.renderGame(container, game, g);
        
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
}
