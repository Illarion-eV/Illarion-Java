/**
 * 
 */
package org.illarion.nifty.controls;

import de.lessvoid.nifty.controls.NiftyControl;
import de.lessvoid.nifty.render.NiftyImage;

/**
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface InventorySlot extends NiftyControl {
    void setImage(NiftyImage image);
    void showLabel();
    void hideLabel();
    void setLabelText(String text);
    void retrieveDraggable();
}
