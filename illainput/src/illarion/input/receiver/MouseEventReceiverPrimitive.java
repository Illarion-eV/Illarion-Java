/**
 * 
 */
package illarion.input.receiver;

/**
 * @author Martin Karing
 *
 */
public interface MouseEventReceiverPrimitive extends MouseEventReceiver {
    boolean handleMouseEvent(int mouseX, int mouseY, int wheelDelta, int button, boolean buttonDown);
}
