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

import illarion.client.util.pathfinding.*;
import illarion.client.world.CharMovementMode;
import illarion.client.world.World;
import illarion.common.types.Direction;
import illarion.common.types.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;

import static illarion.client.util.pathfinding.PathMovementMethod.Run;
import static illarion.client.util.pathfinding.PathMovementMethod.Walk;

/**
 * This movement handler is used to approach a specified location.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class WalkToMovementHandler extends AbstractMovementHandler implements TargetMovementHandler {
    private static final Logger log = LoggerFactory.getLogger(WalkToMovementHandler.class);
    private static final Marker marker = MarkerFactory.getMarker("Movement");
    /**
     * The path finder used to calculate the paths towards the target location.
     */
    @Nonnull
    private final PathFindingAlgorithm pathFindingAlgorithm;

    @Nonnull
    private final Location targetLocation;
    private int targetDistance;

    private boolean targetSet;

    @Nullable
    private Runnable targetAction;

    @Nullable
    private Path currentPath;

    @Nonnull
    private final Collection<Direction> allowedDirections;

    WalkToMovementHandler(@Nonnull Movement movement) {
        super(movement);
        pathFindingAlgorithm = new AStar();
        targetLocation = new Location();
        allowedDirections = EnumSet.allOf(Direction.class);
    }

    @Nullable
    @Override
    public StepData getNextStep(@Nonnull Location currentLocation) {
        if (!targetSet) {
            return null;
        }
        int remainingDistance = currentLocation.getDistance(targetLocation);
        log.debug(marker, "Remaining distance to target: {} Expected distance: {}", remainingDistance, targetDistance);
        if (remainingDistance <= targetDistance) {
            targetSet = false;
            executeTargetAction();
            return null;
        }
        Path activePath;
        if (isCurrentPathValid()) {
            activePath = currentPath;
        } else {
            activePath = calculateNewPath(currentLocation);
            currentPath = activePath;
        }
        if (activePath == null) {
            targetSet = false;
            executeTargetAction();
            return null;
        }

        PathNode node = activePath.nextStep();
        if (node == null) {
            targetSet = false;
            executeTargetAction();
            return null;
        }
        if (!isPathNodeValid(currentLocation, node)) {
            activePath = calculateNewPath(currentLocation);
            currentPath = activePath;
            if (activePath == null) {
                targetSet = false;
                executeTargetAction();
                return new DefaultStepData(CharMovementMode.None, null);
            }
            node = activePath.nextStep();
            if (node == null) {
                targetSet = false;
                executeTargetAction();
                return null;
            }
        }
        log.debug(marker, "Performing step to: {}", node.getLocation());
        CharMovementMode modeMode = convertMovementMode(node.getMovementMethod());
        Direction moveDir = getDirection(currentLocation, node.getLocation());
        return new DefaultStepData(modeMode, moveDir);
    }

    private void executeTargetAction() {
        Runnable action = targetAction;
        targetAction = null;
        if (action != null) {
            action.run();
        }
    }

    private static boolean isPathNodeValid(@Nonnull Location currentLocation, @Nonnull PathNode node) {
        int distanceToPlayer = currentLocation.getDistance(node.getLocation());
        switch (node.getMovementMethod()) {
            case Walk:
                if (distanceToPlayer == 1) {
                    return true;
                }
                break;
            case Run:
                if (distanceToPlayer == 2) {
                    return true;
                }
                break;
        }
        log.warn(marker, "Next path node {} is out of range: {}", node, distanceToPlayer);
        return false;
    }

    @Nonnull
    private static CharMovementMode convertMovementMode(@Nonnull PathMovementMethod method) {
        switch (method) {
            case Walk:
                return CharMovementMode.Walk;
            case Run:
                return CharMovementMode.Run;
        }
        return CharMovementMode.Walk;
    }

    @Nullable
    private static Direction getDirection(@Nonnull Location currentLocation, @Nonnull Location target) {
        return currentLocation.getDirection(target);
    }

    private boolean isCurrentPathValid() {
        Path path = currentPath;
        if (path == null) {
            log.debug(marker, "Path is not valid: Current path is NULL");
            return false;
        }
        Location destination = path.getDestination();
        if (destination == null) {
            log.debug(marker, "Path is not valid: Path destination is NULL");
            return false;
        }
        if (!destination.equals(targetLocation)) {
            log.debug(marker, "Path is not valid: Destination does not equal the current target location.");
            return false;
        }
        return true;
    }

    @Nullable
    private Path calculateNewPath(@Nonnull Location currentLocation) {
        log.info(marker, "Calculating a new path to: {}", targetLocation);
        PathFindingAlgorithm algorithm = pathFindingAlgorithm;

        switch (getMovementMode()) {
            case Walk:
                return algorithm.findPath(World.getMap(), currentLocation, targetLocation, targetDistance,
                                          getAllowedDirections(), Walk);
            case Run:
                return algorithm.findPath(World.getMap(), currentLocation, targetLocation, targetDistance,
                                          getAllowedDirections(), Walk, Run);
            default:
                return null;
        }
    }

    protected CharMovementMode getMovementMode() {
        return getMovement().getDefaultMovementMode();
    }

    @Override
    public void disengage(boolean transferAllowed) {
        super.disengage(transferAllowed);
        targetSet = false;
        setTargetReachedAction(null);
    }

    protected Collection<Direction> getAllowedDirections() {
        return Collections.unmodifiableCollection(allowedDirections);
    }

    @Override
    public void walkTo(@Nonnull Location target, int distance) {
        setTargetReachedAction(null);
        targetLocation.set(target);
        targetDistance = distance;
        targetSet = true;
    }

    @Override
    public void setTargetReachedAction(@Nullable Runnable action) {
        if (!Objects.equals(targetAction, action)) {
            targetAction = action;
            if (action == null) {
                log.debug(marker, "Removed target reached action");
            } else {
                log.debug(marker, "Set target reached action");
            }
        }
    }

    @Override
    public String toString() {
        return "Walk to target movement handler";
    }

    @Nonnull
    protected Location getTargetLocation() {
        return targetLocation;
    }

    protected boolean isTargetSet() {
        return targetSet;
    }
}
