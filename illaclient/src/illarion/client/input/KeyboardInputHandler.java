/**
 * 
 */
package illarion.client.input;

import illarion.client.ClientWindow;
import illarion.client.world.Game;
import illarion.input.KeyboardEvent;
import illarion.input.receiver.KeyboardEventReceiverComplex;

/**
 * @author Martin Karing
 *
 */
public class KeyboardInputHandler implements KeyboardEventReceiverComplex {

    /* (non-Javadoc)
     * @see illarion.input.receiver.KeyboardEventReceiverComplex#handleKeyboardEvent(illarion.input.KeyboardEvent)
     */
    @Override
    public boolean handleKeyboardEvent(KeyboardEvent event) {
        if (event.getEvent() == KeyboardEvent.EVENT_KEY_DOWN) {
            final int centerX = ClientWindow.getInstance().getScreenWidth() / 2;
            final int centerY = ClientWindow.getInstance().getScreenHeight() / 2;
            switch (event.getKey()) {
                case KeyboardEvent.VK_UP:
                    Game.getPlayer().getMovementHandler().walkTowards(centerX, centerY + 10);
                    break;
                case KeyboardEvent.VK_DOWN:
                    Game.getPlayer().getMovementHandler().walkTowards(centerX, centerY - 10);
                    break;
                case KeyboardEvent.VK_RIGHT:
                    Game.getPlayer().getMovementHandler().walkTowards(centerX + 10, centerY);
                    break;
                case KeyboardEvent.VK_LEFT:
                    Game.getPlayer().getMovementHandler().walkTowards(centerX - 10, centerY);
                    break;
            }
        } else if (event.getEvent() == KeyboardEvent.EVENT_KEY_UP) {
            switch (event.getKey()) {
                case KeyboardEvent.VK_UP:
                case KeyboardEvent.VK_DOWN:
                case KeyboardEvent.VK_RIGHT:
                case KeyboardEvent.VK_LEFT:
                    Game.getPlayer().getMovementHandler().stopWalkTowards();
                    break;
            }
        }
        
        return true;
    }

}
