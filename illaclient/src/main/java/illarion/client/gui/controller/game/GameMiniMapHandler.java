/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import de.lessvoid.nifty.builder.ElementBuilder.Align;
import de.lessvoid.nifty.builder.ElementBuilder.VAlign;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.gui.MiniMapGui;
import illarion.client.world.World;
import illarion.common.types.ServerCoordinate;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * This class is the GUI control of the mini map. It takes care of all the required interaction with the mini map
 * GUI, however it does not draw the mini map itself. It just displays the already drawn mini map on the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GameMiniMapHandler implements MiniMapGui, ScreenController, UpdatableHandler {
    /**
     * The instance of Nifty used to control the elements on the screen.
     */
    private Nifty nifty;

    /**
     * This is the instance of the screen that is active and showing the game.
     */
    private Screen screen;

    /**
     * The main element of the mini map.
     */
    @Nullable
    private Element miniMapPanel;

    /**
     * The buffer that stores the currently not used instances of the mini map arrows.
     */
    @Nonnull
    private final Queue<MiniMapArrowPointer> arrowPointerBuffer;

    /**
     * The buffer of currently unused start pointers.
     */
    @Nonnull
    private final Queue<MiniMapStartPointer> startPointerBuffer;

    /**
     * The list of pointers that are currently active and need to be updated.
     */
    @Nonnull
    private final List<MiniMapArrowPointer> activeArrowPointers;

    /**
     * The list of start pointers that are currently active and need to be updated.
     */
    @Nonnull
    private final List<MiniMapStartPointer> activeStartPointers;

    /**
     * Create a new game mini map handler.
     */
    public GameMiniMapHandler() {
        arrowPointerBuffer = new LinkedList<>();
        activeArrowPointers = new LinkedList<>();

        startPointerBuffer = new LinkedList<>();
        activeStartPointers = new LinkedList<>();
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        miniMapPanel = screen.findElementById("miniMapPanel");
    }

    @Override
    @Nonnull
    public Pointer createTargetPointer() {
        return createTargetPointer(true);
    }

    @Nonnull
    public Pointer createTargetPointer(boolean isCurrentQuest) {
        if (arrowPointerBuffer.isEmpty()) {
            ImageBuilder builder = new ImageBuilder();
            builder.visible(false);
            builder.align(Align.Center);
            builder.valign(VAlign.Center);
            Element image = builder.build(nifty, screen, miniMapPanel);
            MiniMapArrowPointer pointer = new MiniMapArrowPointer(image);
            pointer.setCurrentQuest(isCurrentQuest);
            image.getRenderer(ImageRenderer.class).setImage(new NiftyImage(nifty.getRenderEngine(), pointer));
            image.setConstraintHeight(SizeValue.px(pointer.getHeight()));
            image.setConstraintWidth(SizeValue.px(pointer.getWidth()));
            miniMapPanel.layoutElements();
            return pointer;
        }
        return arrowPointerBuffer.poll();
    }

    @Override
    @Nonnull
    public Pointer createStartPointer(boolean available) {
        MiniMapStartPointer pointer;
        if (startPointerBuffer.isEmpty()) {
            ImageBuilder builder = new ImageBuilder();
            builder.visible(false);
            builder.align(Align.Center);
            builder.valign(VAlign.Center);
            Element image = builder.build(nifty, screen, miniMapPanel);
            pointer = new MiniMapStartPointer(image);
            image.getRenderer(ImageRenderer.class).setImage(new NiftyImage(nifty.getRenderEngine(), pointer));
            image.setConstraintHeight(SizeValue.px(pointer.getHeight()));
            image.setConstraintWidth(SizeValue.px(pointer.getWidth()));
            miniMapPanel.layoutElements();
        } else {
            pointer = startPointerBuffer.poll();
        }
        pointer.setAvailable(available);
        return pointer;
    }

    @Override
    public void releasePointer(@Nullable Pointer pointer) {
        if (pointer instanceof MiniMapArrowPointer) {
            MiniMapArrowPointer arrowPointer = (MiniMapArrowPointer) pointer;

            World.getUpdateTaskManager().addTaskForLater((container, delta) -> {
                if (arrowPointer.getParentElement().isVisible()) {
                    arrowPointer.getParentElement().hide(() -> {
                        activeArrowPointers.remove(arrowPointer);
                        arrowPointerBuffer.offer(arrowPointer);
                    });
                } else {
                    activeArrowPointers.remove(arrowPointer);
                    arrowPointerBuffer.offer(arrowPointer);
                }
            });
        } else if (pointer instanceof MiniMapStartPointer) {
            MiniMapStartPointer startPointer = (MiniMapStartPointer) pointer;
            World.getUpdateTaskManager().addTaskForLater((container, delta) -> {
                if (startPointer.getParentElement().isVisible()) {
                    startPointer.getParentElement().hide(() -> {
                        activeStartPointers.remove(startPointer);
                        startPointerBuffer.offer(startPointer);
                    });
                } else {
                    activeStartPointers.remove(startPointer);
                    startPointerBuffer.offer(startPointer);
                }
            });
        }
    }

    @Override
    public void addPointer(@Nonnull Pointer pointer) {
        World.getUpdateTaskManager().addTaskForLater((container, delta) -> {
            if (pointer instanceof MiniMapArrowPointer) {
                MiniMapArrowPointer arrowPointer = (MiniMapArrowPointer) pointer;
                arrowPointer.getParentElement().show(() -> {
                    if (!activeArrowPointers.contains(arrowPointer)) {
                        activeArrowPointers.add(arrowPointer);
                    }
                });
            } else if (pointer instanceof MiniMapStartPointer) {
                MiniMapStartPointer startPointer = (MiniMapStartPointer) pointer;
                startPointer.getParentElement().show(() -> {
                    if (!activeStartPointers.contains(startPointer)) {
                        activeStartPointers.add(startPointer);
                    }
                });
            }
        });
    }

    @NiftyEventSubscriber(id = "openMiniMapBtn")
    public void onMiniMapButtonClicked(String topic, ButtonClickedEvent data) {
        toggleMiniMap();
    }
    /**
     * Toggles display of minimap
     * Will hide minimap if visible, show if not
     */
    @Override
    public void toggleMiniMap(){
        World.getUpdateTaskManager().addTask((container, delta) -> {
            if (miniMapPanel != null) {
                if (miniMapPanel.isVisible()) {
                    hideMiniMap();
                } else {
                    showMiniMap();
                }
            }
        });
    }
    public void showMiniMap(){
        World.getUpdateTaskManager().addTask((container, delta) -> {
            if (miniMapPanel != null) {
                miniMapPanel.show();
            }
        });
    }
    public void hideMiniMap(){
        World.getUpdateTaskManager().addTask((container, delta) -> {
            if (miniMapPanel != null) {
                miniMapPanel.hide();
            }
        });
    }
    @Override
    public void onStartScreen() {
        arrowPointerBuffer.clear();
        if (nifty != null) {
            nifty.subscribeAnnotations(this);
        }
        miniMapPanel.findElementById("miniMapImage").getRenderer(ImageRenderer.class)
                .setImage(new NiftyImage(nifty.getRenderEngine(), World.getMap().getMiniMap().getMiniMap()));
    }

    @Override
    public void onEndScreen() {
        if (nifty != null) {
            nifty.unsubscribeAnnotations(this);
        }
        arrowPointerBuffer.clear();
    }

    @Override
    public void update(GameContainer container, int delta) {
        if (World.getPlayer().isLocationSet()) {
            ServerCoordinate playerLocation = World.getPlayer().getLocation();

            for (@Nonnull MiniMapArrowPointer pointer : activeArrowPointers) {
                pointer.update(delta, playerLocation);
            }
            for (@Nonnull MiniMapStartPointer pointer : activeStartPointers) {
                pointer.update(playerLocation);
            }
        }
    }
}
