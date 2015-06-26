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

import illarion.client.IllaClient;
import illarion.client.util.pathfinding.Path;
import illarion.client.world.CharMovementMode;
import illarion.client.world.MapDimensions;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;
import illarion.common.util.FastMath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class WalkToMouseMovementHandler extends WalkToMovementHandler implements MouseTargetMovementHandler {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(WalkToMouseMovementHandler.class);
    /**
     * Limit the path finding to the direction the mouse is pointing at.
     */
    private boolean limitPathFindingToMouseDirection;

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
        limitPathFindingToMouseDirection = IllaClient.getCfg().getBoolean("limitPathFindingToMouseDirection");

        AnnotationProcessor.process(this);
    }

    @Override
    public void disengage(boolean transferAllowed) {
        boolean targetWasSet = isTargetSet() && isActive();
        ServerCoordinate oldTarget = targetWasSet ? getTargetLocation() : null;
        super.disengage(transferAllowed);
        if (oldTarget != null) {
            switch (getMovementMode()) {
                case Run:
                case Walk:
                    TargetMovementHandler handler = World.getPlayer().getMovementHandler().getTargetMovementHandler();
                    log.debug("Transferring movement control from {} to {}", this, handler);
                    MapTile targetTile = World.getMap().getMapAt(oldTarget);
                    handler.walkTo(oldTarget, ((targetTile != null) && targetTile.isBlocked()) ? 1 : 0);
                    handler.assumeControl();
                    break;
                default:
                    /* nothing */
            }
        }
    }

    @Nonnull
    @Override
    protected CharMovementMode getMovementMode() {
        if (input.isKeyDown(Key.LeftAlt)) {
            return CharMovementMode.None;
        }

        if (!World.getPlayer().getCarryLoad().isRunningPossible()) {
            return CharMovementMode.Walk;
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
        if (getMovement().isMovementModePossible(mode)) {
            return mode;
        }
        return CharMovementMode.Walk;
    }

    @Nonnull
    @Override
    protected Collection<Direction> getAllowedDirections(@Nonnull ServerCoordinate current,
                                                         @Nonnull ServerCoordinate target) {
        if (limitPathFindingToMouseDirection) {
            int dirX = FastMath.sign(target.getX() - current.getX());
            int dirY = FastMath.sign(target.getY() - current.getY());

            if ((dirX == 0) && (dirY == 0)) {
                return Collections.emptyList();
            }

            Collection<Direction> result = EnumSet.noneOf(Direction.class);
            //noinspection ConstantConditions
            for (Direction testDir : Direction.values()) {
                int testX = testDir.getDirectionVectorX();
                int testY = testDir.getDirectionVectorY();

                if (((testX == dirX) && (testY == dirY)) || ((Math.abs(testX - dirX) + Math.abs(testY - dirY)) == 1)) {
                    result.add(testDir);
                }
            }
            return result;
        } else {
            return super.getAllowedDirections(current, target);
        }
    }

    @Override
    @Nullable
    protected Path calculateNewPath(@Nonnull ServerCoordinate currentLocation) {
        int maxDistance = currentLocation.getStepDistance(getTargetLocation());

        while (getTargetDistance() < maxDistance) {
            Path result = super.calculateNewPath(currentLocation);
            if (result != null) {
                return result;
            }
            increaseTargetDistance();
        }
        return null;
    }

    @Override
    @Nullable
    protected Direction getPreferredDirection() {
        MapDimensions mapDimensions = MapDimensions.getInstance();
        int dX = lastMouseX - (mapDimensions.getOnScreenWidth() / 2);
        int dY = -(lastMouseY - (mapDimensions.getOnScreenHeight() / 2));

        if ((dX == 0) && (dY == 0)) {
            return null;
        }

        double theta = Math.atan2(dY, dX) + Math.PI;
        double part = Math.PI / 8;

        if (theta < part) {
            return Direction.NorthWest;
        } else if (theta < (3 * part)) {
            return Direction.West;
        } else if (theta < (5 * part)) {
            return Direction.SouthWest;
        } else if (theta < (7 * part)) {
            return Direction.South;
        } else if (theta < (9 * part)) {
            return Direction.SouthEast;
        } else if (theta < (11 * part)) {
            return Direction.East;
        } else if (theta < (13 * part)) {
            return Direction.NorthEast;
        } else if (theta < (15 * part)) {
            return Direction.North;
        } else {
            return Direction.NorthWest;
        }
    }

    @EventTopicSubscriber(topic = "limitPathFindingToMouseDirection")
    private void limitPathFindingToMouseDirectionChanged(
            @Nonnull String topic, @Nonnull ConfigChangedEvent configChangedEvent) {
        limitPathFindingToMouseDirection = configChangedEvent.getConfig()
                .getBoolean("limitPathFindingToMouseDirection");
    }

    @Nonnull
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
