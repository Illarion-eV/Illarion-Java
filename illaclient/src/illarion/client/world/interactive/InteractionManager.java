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
    private Element draggedGraphic;
    private boolean isDragging;
    private Nifty activeNifty;
    private Screen activeScreen;

    public void dropAt(final int x, final int y) {
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
        draggedObject = null;
        isDragging = false;
        cleanDraggedElement();
    }

    public void startDragging(final Draggable draggable) {
        draggedObject = draggable;
        isDragging = true;
    }

    public void setActiveNiftyEnv(final Nifty nifty, final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;
    }

    /**
     * @param oldx
     * @param oldy
     * @param newx
     * @param newy
     */
    public void notifyDragging(int oldx, int oldy, int newx, int newy) {
        if (!isDragging) {
            final InteractiveMapTile targetTile =
                World.getMap().getInteractive()
                    .getInteractiveTileOnScreenLoc(oldx, oldy);

            if (targetTile == null) {
                return;
            }
            
            if (!targetTile.canDrag()) {
                return;
            }

            startDragging(targetTile);
            cleanDraggedElement();

            if (activeScreen != null && activeNifty != null) {
                final Item movedItem = targetTile.getTopImage();
                final int width = movedItem.getWidth();
                final int height = movedItem.getHeight();
                
                final DraggableBuilder dragBuilder = new DraggableBuilder("mapDraggable");
                dragBuilder.childLayoutCenter();
                dragBuilder.width(Integer.toString(width) + "px");
                dragBuilder.height(Integer.toString(height) + "px");
                dragBuilder.x(Integer.toString(oldx - width / 2) + "px");
                dragBuilder.y(Integer.toString(oldy - height / 2) + "px");
                draggedGraphic = dragBuilder.build(activeNifty, activeScreen, activeScreen.findElementByName("gamePanel"));
                
                final ImageBuilder imgBuilder = new ImageBuilder();
                imgBuilder.width(Integer.toString(width) + "px");
                imgBuilder.height(Integer.toString(height) + "px");
                
                final Element imgElement = imgBuilder.build(activeNifty, activeScreen, draggedGraphic);
                draggedGraphic.getControl(DraggableControl.class).dragStart(oldx, oldy);
                draggedGraphic.getControl(DraggableControl.class).drag(newx, newy);
                
                final ImageRenderer imgRender = imgElement.getRenderer(ImageRenderer.class);
                imgRender.setImage(new NiftyImage(activeNifty.getRenderEngine(), new EntitySlickRenderImage(targetTile.getTopImage())));
            }
        }
    }
    
    private void cleanDraggedElement() {
        if (draggedGraphic != null) {
            draggedGraphic.markForRemoval();
            draggedGraphic = null;
        }
    }
}
