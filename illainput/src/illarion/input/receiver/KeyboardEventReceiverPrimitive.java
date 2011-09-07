/**
 * 
 */
package illarion.input.receiver;

/**
 * @author Martin Karing
 *
 */
public interface KeyboardEventReceiverPrimitive extends KeyboardEventReceiver {
    boolean handleKeyboardEvent(int key, char character, boolean down);
}
