
package illarion.client.states;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import illarion.client.gui.controller.LoadScreenController;
import illarion.client.loading.Loading;

import org.illarion.nifty.slick.NiftyGameState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.opengl.SlickCallable;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This game state is active while the game loads. It takes care for showing
 * the loading screen and to trigger the actual loading.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class LoadingState extends NiftyGameState {
    
    private LoadScreenController controller;

    public LoadingState(final int slickGameStateId) {
        super(slickGameStateId);
    }
    
    private final Logger log = Logger.getLogger(LoadingState.class.getName());
    
    /**
     * initialize.
     * 
     * @param container GameContainer
     * @param game StateBasedGame
     * @throws SlickException exception
     */
    public void init(final GameContainer container, final StateBasedGame game)
        throws SlickException {
        super.init(container, game);
        
        controller = new LoadScreenController(game);
        fromXml("illarion/client/gui/xml/loading.xml", controller);
        
        Loading.enlistMissingComponents();
    }

    /**
     * render.
     * 
     * @param container GameContainer
     * @param game StateBasedGame
     * @param g Graphics
     * @throws SlickException exception
     */
    @Override
    public void render(final GameContainer container,
        final StateBasedGame game, final Graphics g) throws SlickException {
        final int remaining = LoadingList.get().getRemainingResources();
        final int total = LoadingList.get().getTotalResources();
        
        if (remaining > 0) {
            controller.setProgress((float) (total - remaining) / total);
        } else {
            controller.setProgress(1.f);
        }
        
        super.render(container, game, g);
        
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
