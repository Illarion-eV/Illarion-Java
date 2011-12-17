package illarion.client.input;

import illarion.client.world.PlayerMovement;
import illarion.client.world.World;
import illarion.common.util.Location;

import org.newdawn.slick.Input;
import org.newdawn.slick.util.InputAdapter;

/**
 * This class is used to receive and forward all user input.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InputReceiver extends InputAdapter {
    /**
     * @see org.newdawn.slick.InputListener#keyPressed(int, char)
     */
    public void keyPressed(final int key, final char c) {
        switch (key) {
            case Input.KEY_DOWN:
                World.getPlayer().getMovementHandler().requestMove(Location.DIR_SOUTH, PlayerMovement.MOVE_MODE_WALK);
                break;
            case Input.KEY_UP:
                World.getPlayer().getMovementHandler().requestMove(Location.DIR_NORTH, PlayerMovement.MOVE_MODE_WALK);
                break;
            case Input.KEY_LEFT:
                World.getPlayer().getMovementHandler().requestMove(Location.DIR_WEST, PlayerMovement.MOVE_MODE_WALK);
                break;
            case Input.KEY_RIGHT:
                World.getPlayer().getMovementHandler().requestMove(Location.DIR_EAST, PlayerMovement.MOVE_MODE_WALK);
                break;
        }
    }
    
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
        World.getInteractionManager().notifyDraggingMap(oldx, oldy, newx, newy);
    }
}
