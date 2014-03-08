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
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.graphics.Camera;
import illarion.client.graphics.Item;
import illarion.client.gui.EntitySlickRenderImage;
import illarion.client.gui.GameMapGui;
import illarion.client.gui.Tooltip;
import illarion.client.input.*;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.client.world.interactive.InteractionManager;
import illarion.client.world.interactive.InteractiveMapTile;
import illarion.common.types.ItemCount;
import illarion.common.types.Location;
import illarion.common.types.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.engine.graphic.SceneEvent;
import org.illarion.engine.input.ForwardingTarget;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is used to monitor all dropping operations on the droppable area over the game map and notify the
 * interaction manager about a drop in case one happens.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GameMapHandler implements GameMapGui, ScreenController {
    /**
     * This class is used as end operation to the dragging that started on the map. It takes care that the elements
     * used
     * to perform the dragging and cleaned up properly.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class GameMapDragEndOperation implements Runnable {
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
        GameMapDragEndOperation(final Element draggedElement, final Element returnToElement) {
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
     * The logging instance that handles the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMapHandler.class);
    @Nonnull
    private final Input input;

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
    @Nonnull
    private final NiftyMouseInputEvent mouseEvent;

    /**
     * The panel that is located on top of the game map. So this is the lowest located panel and the intended parent
     * for
     * the original location of the dragged object.
     */
    @Nullable
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
     * The handler for the number selection popup.
     */
    private final NumberSelectPopupHandler numberSelect;

    private final TooltipHandler tooltipHandler;

    /**
     * Default constructor that takes care to initialize the variables required for this class to work.
     */
    public GameMapHandler(
            @Nonnull final Input input,
            final NumberSelectPopupHandler numberSelectPopupHandler,
            final TooltipHandler tooltip) {
        mouseEvent = new NiftyMouseInputEvent();
        numberSelect = numberSelectPopupHandler;
        tooltipHandler = tooltip;
        this.input = input;
    }

    /**
     * Handle a input event that was published.
     */
    @EventSubscriber(eventClass = ClickOnMapEvent.class)
    public void handleClickEvent(@Nonnull final SceneEvent event) {
        World.getMapDisplay().getGameScene().publishEvent(event);
    }

    /**
     * Handle a input event that was published.
     */
    @EventSubscriber(eventClass = DoubleClickOnMapEvent.class)
    public void handleDoubleClick(@Nonnull final SceneEvent event) {
        World.getMapDisplay().getGameScene().publishEvent(event);
    }

    /**
     * Handle a input event that was published.
     */
    @EventSubscriber(eventClass = DragOnMapEvent.class)
    public void handleDragging(@Nonnull final DragOnMapEvent data) {
        if (World.getInteractionManager().isDragging()) {
            return;
        }

        switch (data.getKey()) {
            case Left:
                handlePrimaryKeyDrag(data);
                break;
        }
    }

    /**
     * Handle dragging events from the primary mouse key.
     *
     * @param data the event data
     */
    private void handlePrimaryKeyDrag(@Nonnull final DragOnMapEvent data) {
        if (!World.getPlayer().getMovementHandler().isMouseMovementActive()) {
            final SceneEvent newEvent = new PrimaryKeyMapDrag(data, new PrimaryKeyMapDrag.PrimaryKeyMapDragCallback() {
                @Override
                public boolean startDraggingItemFromTile(@Nonnull final PrimaryKeyMapDrag event, final MapTile tile) {
                    return handleDragOnMap(event, tile);
                }

                @Override
                public void notHandled() {
                    moveTowardsMouse(data);
                }
            });
            World.getMapDisplay().getGameScene().publishEvent(newEvent);
            return;
        }

        moveTowardsMouse(data);
    }

    private boolean handleDragOnMap(@Nonnull final PrimaryKeyMapDrag event, @Nullable final MapTile mapTile) {
        if (mapTile == null) {
            return false;
        }
        final InteractiveMapTile targetTile = mapTile.getInteractive();

        if (!targetTile.canDrag()) {
            return false;
        }

        if ((activeScreen != null) && (activeNifty != null)) {
            final Item movedItem = targetTile.getTopItem();
            assert movedItem != null;
            final int width = movedItem.getTemplate().getGuiTexture().getWidth();
            final int height = movedItem.getTemplate().getGuiTexture().getHeight();

            draggedGraphic.resetLayout();
            draggedGraphic.setConstraintWidth(SizeValue.px(width));
            draggedGraphic.setConstraintHeight(SizeValue.px(height));
            draggedGraphic.setConstraintX(SizeValue.px(event.getOldX() - (width / 2)));
            draggedGraphic.setConstraintY(SizeValue.px(event.getOldY() - (height / 2)));
            draggedGraphic.setVisible(true);
            draggedGraphic.reactivate();

            draggedImage.setWidth(width);
            draggedImage.setHeight(height);

            final ImageRenderer imgRender = draggedImage.getRenderer(ImageRenderer.class);
            imgRender.setImage(
                    new NiftyImage(activeNifty.getRenderEngine(), new EntitySlickRenderImage(movedItem.getTemplate())));

            gamePanel.layoutElements();
            input.disableForwarding(ForwardingTarget.Mouse);

            mouseEvent.initialize(event.getOldX(), event.getOldY(), 0, true, false, false);
            mouseEvent.setButton0InitialDown(true);
            activeScreen.mouseEvent(mouseEvent);

            mouseEvent.initialize(event.getNewX(), event.getNewY(), 0, true, false, false);
            activeScreen.mouseEvent(mouseEvent);
        }

        World.getInteractionManager().notifyDraggingMap(mapTile, endOfDragOp);

        return true;
    }

    /**
     * Calling this function causes the character to walk towards the mouse.
     *
     * @param event the event that contains the data for the move
     */
    private void moveTowardsMouse(@Nonnull final DragOnMapEvent event) {
        World.getPlayer().getMovementHandler().walkTowards(event.getNewX(), event.getNewY());
        input.enableForwarding(ForwardingTarget.Mouse);
    }

    @EventSubscriber(eventClass = MoveOnMapEvent.class)
    public void handleMouseMove(@Nonnull final SceneEvent event) {
        if (World.getInteractionManager().isDragging()) {
            return;
        }
        if (World.getPlayer().getMovementHandler().isMouseMovementActive()) {
            return;
        }

        World.getMapDisplay().getGameScene().publishEvent(event);
    }

    @EventSubscriber(eventClass = PointOnMapEvent.class)
    public void handlePointAt(@Nonnull final SceneEvent event) {
        if (World.getInteractionManager().isDragging()) {
            return;
        }
        if (World.getPlayer().getMovementHandler().isMouseMovementActive()) {
            return;
        }

        World.getMapDisplay().getGameScene().publishEvent(event);
    }

    /**
     * Called in case something is dropped on the game map.
     */
    @NiftyEventSubscriber(id = "mapDropTarget")
    public void dropOnMap(final String topic, @Nonnull final DroppableDroppedEvent data) {
        final Element droppedElement = data.getDraggable().getElement();
        final int dropSpotX = droppedElement.getX() + (droppedElement.getWidth() / 2);
        final int dropSpotY = droppedElement.getY() + (droppedElement.getHeight() / 2);

        final ItemCount amount = World.getInteractionManager().getMovedAmount();
        final InteractionManager iManager = World.getInteractionManager();
        if (amount == null) {
            LOGGER.error("Corrupted drag detected!");
            iManager.cancelDragging();
            return;
        }
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
            iManager.dropAtMap(dropSpotX, dropSpotY, amount);
        }
    }

    private boolean isShiftPressed() {
        return input.isAnyKeyDown(Key.LeftShift, Key.RightShift);
    }

    @Override
    public void bind(@Nonnull final Nifty nifty, @Nonnull final Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;
        gamePanel = screen.findElementById("gamePanel");
        draggedGraphic = gamePanel.findElementById("mapDragObject");
        draggedImage = draggedGraphic.findElementById("mapDragImage");
        endOfDragOp = new GameMapDragEndOperation(draggedGraphic, gamePanel);
    }

    @Override
    public void onEndScreen() {
        activeNifty.unsubscribeAnnotations(this);
        AnnotationProcessor.unprocess(this);
    }

    @Override
    public void onStartScreen() {
        activeNifty.subscribeAnnotations(this);
        AnnotationProcessor.process(this);
    }

    @Override
    public void showItemTooltip(@Nonnull final Location location, @Nonnull final Tooltip tooltip) {
        final MapTile targetTile = World.getMap().getMapAt(location);
        if (targetTile == null) {
            return;
        }

        final Item targetItem = targetTile.getTopItem();
        if (targetItem == null) {
            return;
        }

        final Rectangle originalDisplayRect = targetItem.getInteractionRect();
        final Rectangle fixedRectangle = new Rectangle(originalDisplayRect);
        fixedRectangle.move(-Camera.getInstance().getViewportOffsetX(), -Camera.getInstance().getViewportOffsetY());
        tooltipHandler.showToolTip(fixedRectangle, tooltip);
    }
}
