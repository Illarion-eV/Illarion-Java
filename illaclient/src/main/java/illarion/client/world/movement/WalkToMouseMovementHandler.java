/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.world.movement;

import illarion.client.IllaClient;
import illarion.client.world.CharMovementMode;
import illarion.client.world.MapDimensions;
import illarion.client.world.World;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.util.FastMath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class WalkToMouseMovementHandler extends WalkToMovementHandler implements MouseTargetMovementHandler {
    private static final Logger log = LoggerFactory.getLogger(WalkToMouseMovementHandler.class);
    /**
     * Always run when moving with the mouse.
     */
    private boolean mouseFollowAutoRun;

    /**
     * Continue walking after the drag is over (to the last set target)
     */
    private boolean continueWalkAfterDragging;

    /**
     * The last reported X coordinate of the mouse.
     */
    private int lastMouseX;

    /**
     * The last reported Y coordinate of the mouse.
     */
    private int lastMouseY;

    @Nonnull
    private final Input input;

    WalkToMouseMovementHandler(@Nonnull Movement movement, @Nonnull Input input) {
        super(movement);

        this.input = input;
        lastMouseX = -1;
        lastMouseY = -1;
        mouseFollowAutoRun = IllaClient.getCfg().getBoolean("mouseFollowAutoRun");
        continueWalkAfterDragging = IllaClient.getCfg().getBoolean("continueWalkAfterDragging");

        AnnotationProcessor.process(this);
    }

    @Override
    public void disengage(boolean transferAllowed) {
        boolean targetWasSet = isTargetSet() && isActive();
        super.disengage(transferAllowed);
        if (transferAllowed && targetWasSet && continueWalkAfterDragging) {
            TargetMovementHandler handler = World.getPlayer().getMovementHandler().getTargetMovementHandler();
            log.debug("Transferring movement control from {} to {}", this, handler);
            handler.walkTo(getTargetLocation(), 0);
            handler.assumeControl();
        }
    }

    @Override
    protected CharMovementMode getMovementMode() {
        if (input.isKeyDown(Key.LeftAlt)) {
            return CharMovementMode.None;
        }

        if (!mouseFollowAutoRun) {
            return getMovement().getDefaultMovementMode();
        }

        MapDimensions mapDimensions = MapDimensions.getInstance();
        int xOffset = lastMouseX - (mapDimensions.getOnScreenWidth() / 2);
        int yOffset = -(lastMouseY - (mapDimensions.getOnScreenHeight() / 2));
        int distance = FastMath.sqrt((xOffset * xOffset) + (yOffset * yOffset));

        CharMovementMode mode = CharMovementMode.Walk;
        if (distance > 200) {
            mode = CharMovementMode.Run;
        } else if (distance < 30) {
            mode = CharMovementMode.None;
        }
        return mode;
    }

    @EventTopicSubscriber(topic = "mouseFollowAutoRun")
    private void mouseFollowAutoRunChanged(@Nonnull String topic, @Nonnull ConfigChangedEvent configChanged) {
        mouseFollowAutoRun = configChanged.getConfig().getBoolean("mouseFollowAutoRun");
    }

    @EventTopicSubscriber(topic = "mouseFollowAutoRun")
    private void continueWalkAfterDraggingChanged(
            @Nonnull String topic, @Nonnull ConfigChangedEvent configChangedEvent) {
        continueWalkAfterDragging = configChangedEvent.getConfig().getBoolean("continueWalkAfterDragging");
    }

    @Override
    public String toString() {
        return "Walk to mouse pointer movement handler";
    }

    @Override
    public void handleMouse(int x, int y) {
        lastMouseX = x;
        lastMouseY = y;
    }
}
