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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.input.NiftyMouseInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.slick2d.input.ForwardingInputSystem;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.graphics.Camera;
import illarion.client.graphics.Item;
import illarion.client.graphics.Tile;
import illarion.client.gui.EntitySlickRenderImage;
import illarion.client.input.ClickOnMapEvent;
import illarion.client.input.DoubleClickOnMapEvent;
import illarion.client.input.DragOnMapEvent;
import illarion.client.input.MoveOnMapEvent;
import illarion.client.net.server.events.MapItemLookAtEvent;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.client.world.interactive.InteractionManager;
import illarion.client.world.interactive.InteractiveMapTile;
import illarion.common.types.ItemCount;
import illarion.common.util.Rectangle;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

/**
 * This class is used to monitor all dropping operations on the droppable area over the game map and notify the
 * interaction manager about a drop in case one happens.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GameMapHandler
        implements ScreenController, UpdatableHandler {

    private Input input;

    @Override
    public void update(final GameContainer container, final int delta) {
        input = container.getInput();
    }

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
         * @param draggedElement  the dragged element
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
    private final NumberSelectPopupHandler numberSelect;

    /**
     * Default constructor that takes care to initialize the variables required for this class to work.
     */
    public GameMapHandler(final NumberSelectPopupHandler numberSelectPopupHandler, final TooltipHandler tooltip) {
        mouseEvent = new NiftyMouseInputEvent();
        numberSelect = numberSelectPopupHandler;
        tooltipHandler = tooltip;
    }

    public void bind(final Nifty nifty, final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;
        gamePanel = screen.findElementByName("gamePanel");
        draggedGraphic = gamePanel.findElementByName("mapDragObject");
        draggedImage = draggedGraphic.findElementByName("mapDragImage");
        endOfDragOp = new GameMapDragEndOperation(draggedGraphic, gamePanel);
    }

    @Override
    public void onStartScreen() {
        activeNifty.subscribeAnnotations(this);
        AnnotationProcessor.process(this);
    }

    @Override
    public void onEndScreen() {
        activeNifty.unsubscribeAnnotations(this);
        AnnotationProcessor.unprocess(this);
    }

    private boolean isShiftPressed() {
        return (input != null) && (input.isKeyDown(Input.KEY_LSHIFT) || input.isKeyDown(Input.KEY_RSHIFT));
    }

    /**
     * Called in case something is dropped on the game map.
     */
    @NiftyEventSubscriber(id = "mapDropTarget")
    public void dropOnMap(final String topic, final DroppableDroppedEvent data) {
        final Element droppedElement = data.getDraggable().getElement();
        final int dropSpotX = droppedElement.getX() + (droppedElement.getWidth() / 2);
        final int dropSpotY = droppedElement.getY() + (droppedElement.getHeight() / 2);

        final ItemCount amount = World.getInteractionManager().getMovedAmount();
        final InteractionManager iManager = World.getInteractionManager();
        if (ItemCount.isGreaterOne(amount) && isShiftPressed()) {
            numberSelect.requestNewPopup(1, amount.getValue(), new NumberSelectPopupHandler.Callback() {
                @Override
                public void popupCanceled() {
                    // nothing
                }

                @Override
                public void popupConfirmed(final int value) {
                    iManager.dropAtMap(dropSpotX, dropSpotY, ItemCount.getInstance(value));
                }
            });
        } else {
            iManager.dropAtMap(dropSpotX, dropSpotY, World.getInteractionManager().getMovedAmount());
        }
    }

    public boolean handleDragOnMap(final int oldx, final int oldy, final int newx, final int newy,
                                   final ForwardingInputSystem forwardingControl) {
        final MapTile mapTile = World.getMap().getInteractive().getTileOnScreenLoc(oldx, oldy);
        if (mapTile == null) {
            return false;
        }
        final InteractiveMapTile targetTile = mapTile.getInteractive();

        if (targetTile == null) {
            return false;
        }

        if (!targetTile.canDrag()) {
            return false;
        }

        if ((activeScreen != null) && (activeNifty != null)) {
            final Item movedItem = targetTile.getTopImage();
            final int width = movedItem.getWidth();
            final int height = movedItem.getHeight();

            draggedGraphic.resetLayout();
            draggedGraphic.setConstraintWidth(new SizeValue(Integer.toString(width) + "px"));
            draggedGraphic.setConstraintHeight(new SizeValue(Integer.toString(height) + "px"));
            draggedGraphic.setConstraintX(new SizeValue(Integer.toString(oldx - (width / 2)) + "px"));
            draggedGraphic.setConstraintY(new SizeValue(Integer.toString(oldy - (height / 2)) + "px"));
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

        World.getInteractionManager().notifyDraggingMap(mapTile, endOfDragOp);

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
    @EventSubscriber
    public void handleDragging(final DragOnMapEvent data) {
        if (World.getInteractionManager().isDragging()) {
            return;
        }

        if ((data.getKey() == 0) && handleDragOnMap(data.getOldX(), data.getOldY(), data.getNewX(), data.getNewY(),
                data.getForwardingControl())) {
            return;
        }

        if (data.getKey() == 0) {
            moveToMouse(data.getNewX(), data.getNewY(), data.getForwardingControl());
        }
    }

    /**
     * Handle a input event that was published.
     */
    @EventSubscriber
    public void handleClickEvent(final ClickOnMapEvent data) {
        if (data.getKey() != 0) {
            return;
        }

        final InteractiveMapTile tile = World.getMap().getInteractive().getInteractiveTileOnScreenLoc(data.getX(),
                data.getY());

        if (tile == null) {
            return;
        }

        if ((activeScreen != null) && (activeNifty != null)) {
            data.getForwardingControl().releaseExclusiveMouse();

            mouseEvent.initialize(data.getX(), data.getY(), data.getKey(), true, false, false);
            mouseEvent.setButton0InitialDown(true);
            activeScreen.mouseEvent(mouseEvent);
        }

        tile.lookAt();
    }

    @EventSubscriber
    public void handleMouseMove(final MoveOnMapEvent event) {

    }

    /**
     * Handle a input event that was published.
     */
    @EventSubscriber
    public void handleDoubleClick(final DoubleClickOnMapEvent data) {
        if (data.getKey() != 0) {
            return;
        }

        final InteractiveMapTile tile = World.getMap().getInteractive().getInteractiveTileOnScreenLoc(data.getX(),
                data.getY());

        if (tile == null) {
            return;
        }

        if (!tile.isInUseRange()) {
            return;
        }

        if ((activeScreen != null) && (activeNifty != null)) {
            data.getForwardingControl().releaseExclusiveMouse();

            mouseEvent.initialize(data.getX(), data.getY(), data.getKey(), true, false, false);
            mouseEvent.setButton0InitialDown(true);
            activeScreen.mouseEvent(mouseEvent);
        }

        tile.use();
    }

    private final TooltipHandler tooltipHandler;

    @EventSubscriber
    public void onMapItemLookAtEvent(final MapItemLookAtEvent event) {
        final MapTile tile = World.getMap().getMapAt(event.getLocation());
        if (tile == null) {
            return;
        }

        final Rectangle rect = new Rectangle();
        final Item item = tile.getTopItem();
        final int offsetX = Camera.getInstance().getViewportOffsetX();
        final int offsetY = Camera.getInstance().getViewportOffsetY();
        if (item != null) {
            final Rectangle displayRect = item.getDisplayRect();
            rect.set(displayRect.getX() + offsetX, displayRect.getY() + offsetX, displayRect.getWidth(),
                    displayRect.getHeight());
        } else {
            final Tile graphicalTile = tile.getTile();
            final Rectangle displayRect = graphicalTile.getDisplayRect();
            rect.set(displayRect.getX() + offsetX, displayRect.getY() + offsetX, displayRect.getWidth(),
                    displayRect.getHeight());

        }
        tooltipHandler.showToolTip(rect, event);
    }
}
