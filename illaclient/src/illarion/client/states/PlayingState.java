/**
 * 
 */
package illarion.client.states;

import illarion.client.Game;
import illarion.client.world.World;

import org.illarion.nifty.slick.NiftyOverlayGameState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import de.lessvoid.nifty.input.keyboard.KeyboardInputEvent;

/**
 * @author Martin Karing
 *
 */
public class PlayingState extends NiftyOverlayGameState {

    /* (non-Javadoc)
     * @see de.lessvoid.nifty.NiftyInputConsumer#processMouseEvent(int, int, int, int, boolean)
     */
    @Override
    public boolean processMouseEvent(int mouseX, int mouseY, int mouseWheel,
        int button, boolean buttonDown) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.lessvoid.nifty.NiftyInputConsumer#processKeyboardEvent(de.lessvoid.nifty.input.keyboard.KeyboardInputEvent)
     */
    @Override
    public boolean processKeyboardEvent(KeyboardInputEvent keyEvent) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.newdawn.slick.state.GameState#init(org.newdawn.slick.GameContainer, org.newdawn.slick.state.StateBasedGame)
     */
    @Override
    public void init(GameContainer container, StateBasedGame game)
        throws SlickException {
         initNifty();
         loadXml("illarion/client/gui/xml/gamescreen.xml", "gamescreen");
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        World.getMapDisplay().render(g, container, lastDelta);
        super.render(container, game, g);
    }
    
    private int lastDelta;
    
    @Override
    public void update(final GameContainer container, final StateBasedGame game, final int delta) throws SlickException {
        lastDelta = delta;
        World.getAnimationManager().animate(delta);
        super.update(container, game, delta);
    }

    /* (non-Javadoc)
     * @see org.newdawn.slick.state.BasicGameState#getID()
     */
    @Override
    public int getID() {
        return Game.STATE_PLAYING;
    }

}
