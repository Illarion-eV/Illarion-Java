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

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.controls.dragndrop.DraggableControl;
import de.lessvoid.nifty.controls.dragndrop.builder.DraggableBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.input.NiftyMouseInputEvent;
import de.lessvoid.nifty.loaderv2.types.ImageType;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.graphics.Item;
import illarion.client.gui.EntitySlickRenderImage;
import illarion.client.world.World;

/**
 * Main purpose of this class is to interconnect the GUI environment and the map
 * environment to exchange informations between both.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InteractionManager {
    private Draggable draggedObject;
    private boolean isDragging;
    private Runnable endOfDragAction;

    public void dropAtMap(final int x, final int y) {
        if (draggedObject == null) {
            return;
        }
        final InteractiveMapTile targetTile =
            World.getMap().getInteractive()
                .getInteractiveTileOnScreenLoc(x, y);

        if (targetTile == null) {
            return;
        }

        draggedObject.dragTo(targetTile);
        cancelDragging();
    }
    
    public void dropAtInventory(final int slot) {
        if (draggedObject == null) {
            return;
        }
        
        final InteractiveInventorySlot targetSlot = World.getPlayer().getInventory().getItem(slot).getInteractive();
        
        if (targetSlot == null) {
            return;
        }
        
        draggedObject.dragTo(targetSlot);
        cancelDragging();
    }
    
    public void cancelDragging() {
        draggedObject = null;
        isDragging = false;
        if (endOfDragAction != null) {
            endOfDragAction.run();
            endOfDragAction = null;
        }
    }

    public void startDragging(final Draggable draggable) {
        draggedObject = draggable;
        isDragging = true;
    }
    
    public void notifyDraggingInventory(final int slot, final Runnable endOfDragOp) {
        if (!isDragging) {
            final InteractiveInventorySlot sourceSlot = World.getPlayer().getInventory().getItem(slot).getInteractive();
            
            if (sourceSlot == null) {
                return;
            }
            
            if (!sourceSlot.isValidItem()) {
                return;
            }

            startDragging(sourceSlot);
            endOfDragAction = endOfDragOp;
        }
    }
    
    public void notifyDraggingMap(final InteractiveMapTile targetTile, final Runnable endOfDragOp) {
        if (!isDragging) {
            if (targetTile == null) {
                return;
            }
            
            if (!targetTile.canDrag()) {
                return;
            }

            startDragging(targetTile);
            endOfDragAction = endOfDragOp;
        }
    }
}
