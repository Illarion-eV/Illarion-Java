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
package illarion.client.world.movement;

import illarion.client.world.CharMovementMode;
import illarion.client.world.MapDimensions;
import illarion.client.world.World;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;
import illarion.common.util.FastMath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the movement handler that takes care for the follow mouse movement system. As long as this is engaged moves
 * following the mouse will be plotted and send to the movement handler.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class FollowMouseMovementHandler extends AbstractMovementHandler implements MouseMovementHandler {
    /**
     * This value is the relation of the distance from the character location to the location of the cursor to the
     * plain x or y offset. In case the relation is smaller or equal to this the character will move straight
     * horizontal or vertical on the screen. Else it will move diagonal.
     */
    private static final double MOUSE_ANGLE = StrictMath.cos(Math.PI / Direction.values().length);

    @Nonnull
    private final Input input;

    /**
     * The last reported X coordinate of the mouse.
     */
    private int lastMouseX;

    /**
     * The last reported Y coordinate of the mouse.
     */
    private int lastMouseY;

    private CharMovementMode currentMovementMode;

    @Nullable
    private Direction walkTowardsDir;

    FollowMouseMovementHandler(@Nonnull Movement movement, @Nonnull Input input) {
        super(movement);
        this.input = input;
        lastMouseX = -1;
        lastMouseY = -1;

        AnnotationProcessor.process(this);
    }

    @Nullable
    @Override
    public StepData getNextStep(@Nonnull ServerCoordinate currentLocation) {
        calculateMove();
        if (walkTowardsDir == null) {
            return null;
        }
        return new DefaultStepData(currentMovementMode, walkTowardsDir);
    }

    @Override
    public void handleMouse(int x, int y) {
        lastMouseX = x;
        lastMouseY = y;
    }

    private void calculateMove() {
        MapDimensions mapDimensions = MapDimensions.getInstance();
        int xOffset = lastMouseX - (mapDimensions.getOnScreenWidth() / 2);
        int yOffset = -(lastMouseY - (mapDimensions.getOnScreenHeight() / 2));
        int distance = FastMath.sqrt((xOffset * xOffset) + (yOffset * yOffset));

        if (distance <= 5) {
            walkTowardsDir = null;
            return;
        }

        float relXOffset = (float) xOffset / distance;
        float relYOffset = (float) yOffset / distance;

        currentMovementMode = getWalkTowardsMode(distance);

        //noinspection IfStatementWithTooManyBranches
        if (relXOffset > MOUSE_ANGLE) {
            walkTowardsDir = Direction.SouthEast;
        } else if (relXOffset < -MOUSE_ANGLE) {
            walkTowardsDir = Direction.NorthWest;
        } else if (relYOffset > MOUSE_ANGLE) {
            walkTowardsDir = Direction.NorthEast;
        } else if (relYOffset < -MOUSE_ANGLE) {
            walkTowardsDir = Direction.SouthWest;
        } else if ((xOffset > 0) && (yOffset > 0)) {
            walkTowardsDir = Direction.East;
        } else if ((xOffset > 0) && (yOffset < 0)) {
            walkTowardsDir = Direction.South;
        } else if ((xOffset < 0) && (yOffset < 0)) {
            walkTowardsDir = Direction.West;
        } else if ((xOffset < 0) && (yOffset > 0)) {
            walkTowardsDir = Direction.North;
        }
    }

    @Nonnull
    private CharMovementMode getWalkTowardsMode(int distance) {
        if (input.isAnyKeyDown(Key.LeftShift, Key.RightShift)) {
            return CharMovementMode.None;
        }

        if (!World.getPlayer().getCarryLoad().isRunningPossible()) {
            return CharMovementMode.Walk;
        }

        CharMovementMode mode = CharMovementMode.Walk;
        if (distance > 200) {
            mode = CharMovementMode.Run;
        } else if (distance < 30) {
            mode = CharMovementMode.None;
        }
        if (getMovement().isMovementModePossible(mode)) {
            return mode;
        }
        return CharMovementMode.Walk;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Follow mouse movement handler";
    }
}
