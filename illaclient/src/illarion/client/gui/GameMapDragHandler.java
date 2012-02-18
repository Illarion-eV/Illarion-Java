/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.input.NiftyMouseInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.slick2d.input.ForwardingInputSystem;
import de.lessvoid.nifty.tools.SizeValue;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;

import illarion.client.graphics.Item;
import illarion.client.input.DragOnMapEvent;
import illarion.client.input.InputReceiver;
import illarion.client.world.World;
import illarion.client.world.interactive.InteractiveMapTile;

/**
 * This class is used to monitor all dropping operations on the droppable area over the game map and notify the
 * interaction manager about a drop in case one happens.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GameMapDragHandler
        implements EventTopicSubscriber<DragOnMapEvent> {
    /**
     * This class is used as end operation to the dragging that started on the map. It takes care that the elements
     * used
     * to perform the dragging and cleaned up properly.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class GameMapDragEndOperation
            implements Runnable {
        /**
         * The element that is dragged around.
         */
        private final Element drag;

        /**
         * The element the dragged element needs to return to.
         */
        private final Element returnTo;

        /**
         * Create a new end of drag operation.
         *
         * @param draggedElement the dragged element
         * @param returnToElement the element the dragged element needs to return to
         */
        public GameMapDragEndOperation(final Element draggedElement, final Element returnToElement) {
            drag = draggedElement;
            returnTo = returnToElement;
        }

        /**
         * Execute this operation.
         */
        @Override
        public void run() {
            drag.setVisible(false);
            drag.markForMove(returnTo);
        }

    }

    /**
     * The Nifty-GUI instance that is handling the GUI display currently.
     */
    private Nifty activeNifty;

    /**
     * The screen that takes care for the display currently.
     */
    private Screen activeScreen;

    /**
     * This mouse event instance is used to initiate the dragging event.
     */
    private final NiftyMouseInputEvent mouseEvent;

    /**
     * The panel that is located on top of the game map. So this is the lowest located panel and the intended parent
     * for
     * the original location of the dragged object.
     */
    private Element gamePanel;

    /**
     * The element that is dragged around.
     */
    private Element draggedGraphic;

    /**
     * The element that displays the image that is dragged around.
     */
    private Element draggedImage;

    /**
     * This operation is executed once a dragging operation is done.
     */
    private Runnable endOfDragOp;

    /**
     * Default constructor that takes care to initialize the variables required for this class to work.
     */
    public GameMapDragHandler() {
        mouseEvent = new NiftyMouseInputEvent();
    }

    public void bind(final Nifty nifty, final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;
        gamePanel = screen.findElementByName("gamePanel");
        draggedGraphic = gamePanel.findElementByName("mapDragObject");
        draggedImage = draggedGraphic.findElementByName("mapDragImage");
        endOfDragOp = new GameMapDragEndOperation(draggedGraphic, gamePanel);
        EventBus.subscribe(InputReceiver.EB_TOPIC, this);
    }

    /**
     * Called in case something is dropped on the game map.
     */
    @NiftyEventSubscriber(id = "mapDropTarget")
    public void dropOnMap(final String topic, final DroppableDroppedEvent data) {
        final Element droppedElement = data.getDraggable().getElement();
        final int dropSpotX = droppedElement.getX() + (droppedElement.getWidth() / 2);
        final int dropSpotY = droppedElement.getY() + (droppedElement.getHeight() / 2);

        droppedElement.setVisible(false);

        World.getInteractionManager().dropAtMap(dropSpotX, dropSpotY);
    }

    public boolean handleDragOnMap(final int oldx, final int oldy, final int newx, final int newy,
                                   final ForwardingInputSystem forwardingControl) {
        final InteractiveMapTile targetTile = World.getMap().getInteractive().getInteractiveTileOnScreenLoc(oldx,
                                                                                                            oldy);

        if (targetTile == null) {
            return false;
        }

        if (!targetTile.canDrag()) {
            return false;
        }

        if (activeScreen != null && activeNifty != null) {
            final Item movedItem = targetTile.getTopImage();
            final int width = movedItem.getWidth();
            final int height = movedItem.getHeight();

            draggedGraphic.resetLayout();
            draggedGraphic.setConstraintWidth(new SizeValue(Integer.toString(width) + "px"));
            draggedGraphic.setConstraintHeight(new SizeValue(Integer.toString(height) + "px"));
            draggedGraphic.setConstraintX(new SizeValue(Integer.toString(oldx - width / 2) + "px"));
            draggedGraphic.setConstraintY(new SizeValue(Integer.toString(oldy - height / 2) + "px"));
            draggedGraphic.setVisible(true);
            draggedGraphic.reactivate();

            draggedImage.setWidth(width);
            draggedImage.setHeight(height);

            final ImageRenderer imgRender = draggedImage.getRenderer(ImageRenderer.class);
            imgRender.setImage(new NiftyImage(activeNifty.getRenderEngine(), new EntitySlickRenderImage(movedItem)));

            gamePanel.layoutElements();
            forwardingControl.releaseExclusiveMouse();

            mouseEvent.initialize(oldx, oldy, 0, true, false, false);
            mouseEvent.setButton0InitialDown(true);
            activeScreen.mouseEvent(mouseEvent);

            mouseEvent.initialize(newx, newy, 0, true, false, false);
            activeScreen.mouseEvent(mouseEvent);
        }

        World.getInteractionManager().notifyDraggingMap(targetTile, endOfDragOp);

        return true;
    }

    private boolean moveToMouse(final int targetX, final int targetY, final ForwardingInputSystem forwardingControl) {
        World.getPlayer().getMovementHandler().walkTowards(targetX, targetY);
        forwardingControl.requestExclusiveMouse();

        return true;
    }

    /**
     * Handle a input event that was published.
     */
    @Override
    public void onEvent(final String topic, final DragOnMapEvent data) {
        if (topic.equals(InputReceiver.EB_TOPIC)) {
            if (handleDragOnMap(data.getOldX(), data.getOldY(), data.getNewX(), data.getNewY(),
                                data.getForwardingControl())) {
                return;
            }

            moveToMouse(data.getNewX(), data.getNewY(), data.getForwardingControl());
        }
    }
}
