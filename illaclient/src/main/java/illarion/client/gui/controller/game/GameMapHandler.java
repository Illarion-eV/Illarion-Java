/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.DroppableDroppedEvent;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.input.NiftyMouseInputEvent;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.IllaClient;
import illarion.client.graphics.Camera;
import illarion.client.graphics.Item;
import illarion.client.gui.GameMapGui;
import illarion.client.gui.TextureRenderImage;
import illarion.client.gui.Tooltip;
import illarion.client.gui.controller.game.NumberSelectPopupHandler.Callback;
import illarion.client.input.*;
import illarion.client.input.PrimaryKeyMapDrag.PrimaryKeyMapDragCallback;
import illarion.client.world.CharMovementMode;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.client.world.interactive.InteractionManager;
import illarion.client.world.interactive.InteractiveMapTile;
import illarion.client.world.movement.MouseMovementHandler;
import illarion.client.world.movement.Movement;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.types.ItemCount;
import illarion.common.types.Rectangle;
import illarion.common.types.ServerCoordinate;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.illarion.engine.graphic.SceneEvent;
import org.illarion.engine.input.ForwardingTarget;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * The logging instance that handles the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMapHandler.class);
    @Nonnull
    private final Input input;
    /**
     * This mouse event instance is used to initiate the dragging event.
     */
    @Nonnull
    private final NiftyMouseInputEvent mouseEvent;
    /**
     * The handler for the number selection popup.
     */
    private final NumberSelectPopupHandler numberSelect;
    private final TooltipHandler tooltipHandler;
    /**
     * The Nifty-GUI instance that is handling the GUI display currently.
     */
    private Nifty activeNifty;
    /**
     * The screen that takes care for the display currently.
     */
    private Screen activeScreen;
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
    @Nullable
    private Element draggedGraphic;
    /**
     * The element that displays the image that is dragged around.
     */
    @Nullable
    private Element draggedImage;
    /**
     * This operation is executed once a dragging operation is done.
     */
    @Nullable
    private Runnable endOfDragOp;
    private boolean followMouseWithPathFinding;

    /**
     * Default constructor that takes care to initialize the variables required for this class to work.
     */
    public GameMapHandler(
            @Nonnull Input input, NumberSelectPopupHandler numberSelectPopupHandler, TooltipHandler tooltip) {
        mouseEvent = new NiftyMouseInputEvent();
        numberSelect = numberSelectPopupHandler;
        tooltipHandler = tooltip;
        this.input = input;

        AnnotationProcessor.process(this);
        followMouseWithPathFinding = IllaClient.getCfg().getBoolean("followMousePathFinding");
    }

    /**
     * Handle a input event that was published.
     */
    @EventSubscriber(eventClass = ClickOnMapEvent.class)
    public void handleClickEvent(@Nonnull SceneEvent event) {
        World.getMapDisplay().getGameScene().publishEvent(event);
    }

    /**
     * Handle a input event that was published.
     */
    @EventSubscriber(eventClass = DoubleClickOnMapEvent.class)
    public void handleDoubleClick(@Nonnull SceneEvent event) {
        World.getMapDisplay().getGameScene().publishEvent(event);
    }

    /**
     * Handle a input event that was published.
     */
    @EventSubscriber(eventClass = DragOnMapEvent.class)
    public void handleDragging(@Nonnull DragOnMapEvent data) {
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
    private void handlePrimaryKeyDrag(@Nonnull DragOnMapEvent data) {
        Movement movement = World.getPlayer().getMovementHandler();
        if (!movement.getFollowMouseHandler().isActive() && !movement.getTargetMouseMovementHandler().isActive()) {
            SceneEvent newEvent = new PrimaryKeyMapDrag(data, new PrimaryKeyMapDragCallback() {
                @Override
                public boolean startDraggingItemFromTile(@Nonnull PrimaryKeyMapDrag event, @Nonnull MapTile tile) {
                    return handleDragOnMap(event, tile);
                }

                @Override
                public void notHandled() {
                    moveTowardsMouse(data);
                }
            });
            World.getMapDisplay().getGameScene().publishEvent(newEvent);
        } else {
            moveTowardsMouse(data);
        }
    }

    private boolean handleDragOnMap(@Nonnull PrimaryKeyMapDrag event, @Nullable MapTile mapTile) {
        if (mapTile == null) {
            return false;
        }
        InteractiveMapTile targetTile = mapTile.getInteractive();

        if (!targetTile.canDrag()) {
            return false;
        }

        event.getInputReceiver().guiTookControl();

        if ((activeScreen != null) && (activeNifty != null)) {
            Item movedItem = targetTile.getTopItem();
            assert movedItem != null;
            int width = movedItem.getTemplate().getGuiTexture().getWidth();
            int height = movedItem.getTemplate().getGuiTexture().getHeight();

            draggedGraphic.resetLayout();
            draggedGraphic.setConstraintWidth(SizeValue.px(width));
            draggedGraphic.setConstraintHeight(SizeValue.px(height));
            draggedGraphic.setConstraintX(SizeValue.px(event.getOldX() - (width / 2)));
            draggedGraphic.setConstraintY(SizeValue.px(event.getOldY() - (height / 2)));
            draggedGraphic.showWithoutEffects();
            draggedGraphic.reactivate();

            draggedImage.setWidth(width);
            draggedImage.setHeight(height);
            draggedImage.showWithoutEffects();

            ImageRenderer imgRender = draggedImage.getRenderer(ImageRenderer.class);
            imgRender.setImage(
                    new NiftyImage(activeNifty.getRenderEngine(), new TextureRenderImage(movedItem.getTemplate())));

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

    @EventTopicSubscriber(topic = "followMousePathFinding")
    private void onFollowMouseWithPathFindingCfgChanged(@Nonnull String topic, @Nonnull ConfigChangedEvent event) {
        followMouseWithPathFinding = event.getConfig().getBoolean("followMousePathFinding");
    }

    /**
     * Calling this function causes the character to walk towards the mouse.
     *
     * @param event the event that contains the data for the move
     */
    private void moveTowardsMouse(@Nonnull DragOnMapEvent event) {
        MouseMovementHandler handler;
        if (followMouseWithPathFinding) {
            handler = World.getPlayer().getMovementHandler().getTargetMouseMovementHandler();
        } else {
            handler = World.getPlayer().getMovementHandler().getFollowMouseHandler();
        }
        handler.handleMouse(event.getNewX(), event.getNewY());
        if (event.isStartDragging()) {
            handler.assumeControl();
        }

        input.enableForwarding(ForwardingTarget.Mouse);
    }

    @EventSubscriber(eventClass = MoveOnMapEvent.class)
    public void handleMouseMove(@Nonnull SceneEvent event) {
        if (World.getInteractionManager().isDragging()) {
            return;
        }
        if (World.getPlayer().getMovementHandler().getFollowMouseHandler().isActive()) {
            return;
        }

        World.getMapDisplay().getGameScene().publishEvent(event);
    }

    @EventSubscriber(eventClass = PointOnMapEvent.class)
    public void handlePointAt(@Nonnull SceneEvent event) {
        if (World.getInteractionManager().isDragging()) {
            return;
        }
        if (World.getPlayer().getMovementHandler().getFollowMouseHandler().isActive()) {
            return;
        }

        World.getMapDisplay().getGameScene().publishEvent(event);
    }

    /**
     * Called in case something is dropped on the game map.
     */
    @NiftyEventSubscriber(id = "mapDropTarget")
    public void dropOnMap(String topic, @Nonnull DroppableDroppedEvent data) {
        Element droppedElement = data.getDraggable().getElement();
        int dropSpotX = droppedElement.getX() + (droppedElement.getWidth() / 2);
        int dropSpotY = droppedElement.getY() + (droppedElement.getHeight() / 2);

        ItemCount amount = World.getInteractionManager().getMovedAmount();
        InteractionManager iManager = World.getInteractionManager();
        if (amount == null) {
            LOGGER.error("Corrupted drag detected!");
            iManager.cancelDragging();
            return;
        }
        if (ItemCount.isGreaterOne(amount) && isShiftPressed()) {
            numberSelect.requestNewPopup(1, amount.getValue(), new Callback() {
                @Override
                public void popupCanceled() {
                    // nothing
                }

                @Override
                public void popupConfirmed(int value) {
                    iManager.dropAtMap(dropSpotX, dropSpotY, ItemCount.getInstance(value));
                }
            });
        } else {
            iManager.dropAtMap(dropSpotX, dropSpotY, amount);
        }
    }

    /**
     * The event subscriber for click events on the run button.
     *
     * @param topic the event topic
     * @param data the event data
     */
    @NiftyEventSubscriber(id = "toggleRunBtn")
    public void onToggleRunButtonClicked(String topic, ButtonClickedEvent data) {
        toggleRunMode();
    }

    private boolean isShiftPressed() {
        return input.isAnyKeyDown(Key.LeftShift, Key.RightShift);
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        activeNifty = nifty;
        activeScreen = screen;
        gamePanel = screen.findElementById("gamePanel");
        draggedGraphic = gamePanel.findElementById("mapDragObject");
        draggedImage = draggedGraphic.findElementById("mapDragImage");
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

    @Override
    public void showItemTooltip(@Nonnull ServerCoordinate location, int stackPosition, @Nonnull Tooltip tooltip) {
        LOGGER.debug("Now showing tooltip for {} at stack position {}", location, stackPosition);
        MapTile targetTile = World.getMap().getMapAt(location);
        if (targetTile == null) {
            return;
        }

        try {
            Item targetItem = targetTile.getItem(stackPosition);

            Rectangle originalDisplayRect = targetItem.getInteractionRect();
            Rectangle fixedRectangle = new Rectangle(originalDisplayRect);
            fixedRectangle.move(-Camera.getInstance().getViewportOffsetX(), -Camera.getInstance().getViewportOffsetY());
            tooltipHandler.showToolTip(fixedRectangle, tooltip);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.warn("Error while showing a tooltip: {}", e.getMessage());
        }
    }

    /**
     * Toggle the pulsing animation of the run button.
     */
    @Override
    public void toggleRunMode() {
        boolean nowWalking = true;
        if (World.getPlayer().getMovementHandler().getDefaultMovementMode() == CharMovementMode.Run) {
            World.getPlayer().getMovementHandler().setDefaultMovementMode(CharMovementMode.Walk);
        } else {
            World.getPlayer().getMovementHandler().setDefaultMovementMode(CharMovementMode.Run);
            nowWalking = false;
        }
        if (activeScreen == null) {
            return;
        }
        @Nullable Element runBtn = activeScreen.findElementById("toggleRunBtn");
        if (runBtn == null) {
            return;
        }
        if (nowWalking) {
            runBtn.stopEffect(EffectEventId.onCustom);
        } else {
            runBtn.startEffect(EffectEventId.onCustom, null, "pulse");
        }
    }

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
        GameMapDragEndOperation(Element draggedElement, Element returnToElement) {
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
}
