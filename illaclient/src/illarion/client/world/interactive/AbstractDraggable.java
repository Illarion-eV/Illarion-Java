/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world.interactive;

/**
 * This abstract implementation of the draggable interface implements only the
 * general dragTo instance. This implementation forwards the call to the fitting
 * specialized dragTo implementations.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractDraggable implements Draggable {
    /**
     * Drag this object to a general drop target.
     * 
     * @param dropTarget the target of the dropping operation
     */
    public void dragTo(final DropTarget dropTarget) {
        if (dropTarget instanceof InteractiveChar) {
            dragTo((InteractiveChar) dropTarget);
            return;
        } else if (dropTarget instanceof InteractiveInventorySlot) {
            dragTo((InteractiveInventorySlot) dropTarget);
            return;
        }  else if (dropTarget instanceof InteractiveMapTile) {
            dragTo((InteractiveMapTile) dropTarget);
            return;
        } 
    }
}
