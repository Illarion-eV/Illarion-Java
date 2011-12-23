package illarion.client.states;

import illarion.client.Game;
import illarion.client.input.InputReceiver;
import illarion.client.world.World;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.slick2d.NiftyOverlayBasicGameState;
import de.lessvoid.nifty.slick2d.input.SlickSlickInputSystem;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 *
 */
public class PlayingState extends NiftyOverlayBasicGameState {    
    private int lastDelta;
    /* (non-Javadoc)
     * @see org.newdawn.slick.state.BasicGameState#getID()
     */
    @Override
    public int getID() {
        return Game.STATE_PLAYING;
    }

    @Override
    protected void initGameAndGUI(GameContainer container, StateBasedGame game)
        throws SlickException {
        initNifty(container, game, new SlickSlickInputSystem(new InputReceiver()));
    }

    @Override
    protected void prepareNifty(Nifty nifty, StateBasedGame game) {
        nifty.fromXml("illarion/client/gui/xml/gamescreen.xml", "gamescreen");
    }

    @Override
    protected void renderGame(GameContainer container, StateBasedGame game,
        Graphics g) throws SlickException {
        World.getMapDisplay().render(g, container, lastDelta);
    }

    @Override
    protected void updateGame(GameContainer container, StateBasedGame game,
        int delta) throws SlickException {
        lastDelta = delta;
        World.getAnimationManager().animate(delta);
    }

    @Override
    protected void enterState(GameContainer container, StateBasedGame game)
        throws SlickException {
        getNifty().gotoScreen("gamescreen");
    }

    @Override
    protected void leaveState(GameContainer container, StateBasedGame game)
        throws SlickException {
        // TODO Auto-generated method stub
        
    }

}
