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
import illarion.common.types.Location;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static illarion.client.util.pathfinding.PathMovementMethod.Run;
import static illarion.client.util.pathfinding.PathMovementMethod.Walk;

/**
 * This movement handler is used to approach a specified location.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Slf4j
class WalkToMovementHandler extends AbstractMovementHandler implements TargetMovementHandler {
    /**
     * The path finder used to calculate the paths towards the target location.
     */
    @Nonnull
    private final PathFindingAlgorithm pathFindingAlgorithm;

    @Nonnull
    @Getter(AccessLevel.PROTECTED)
    private final Location targetLocation;
    private int targetDistance;

    @Getter(AccessLevel.PROTECTED)
    private boolean targetSet;

    @Nullable
    private Runnable targetAction;

    @Nullable
    private Path currentPath;

    WalkToMovementHandler(@Nonnull Movement movement) {
        super(movement);
        pathFindingAlgorithm = new AStar();
        targetLocation = new Location();
    }

    @Nonnull
    @Override
    public StepData getNextStep(@Nonnull Location currentLocation) {
        if (!targetSet) {
            return new DefaultStepData(CharMovementMode.None, 0);
        }
        int remainingDistance = currentLocation.getDistance(targetLocation);
        log.debug("Remaining distance to target: {} Expected distance: {}", remainingDistance, targetDistance);
        if (remainingDistance <= targetDistance) {
            targetSet = false;
            executeTargetAction();
            return new DefaultStepData(CharMovementMode.None, 0);
        }
        Path activePath;
        if (isCurrentPathValid()) {
            activePath = currentPath;
        } else {
            activePath = calculateNewPath(currentLocation);
            currentPath = activePath;
        }
        if (activePath == null) {
            return new DefaultStepData(CharMovementMode.None, 0);
        }

        PathNode node = activePath.nextStep();
        if (node == null) {
            targetSet = false;
            executeTargetAction();
            return new DefaultStepData(CharMovementMode.None, 0);
        }
        if (!isPathNodeValid(currentLocation, node)) {
            currentPath = calculateNewPath(currentLocation);
            node = activePath.nextStep();
            if (node == null) {
                return new DefaultStepData(CharMovementMode.None, 0);
            }
        }
        log.debug("Performing step to: {}", node.getLocation());
        CharMovementMode modeMode = convertMovementMode(node.getMovementMethod());
        int moveDir = getDirection(currentLocation, node.getLocation());
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
        log.warn("Next path node {} is out of range: {}", node, distanceToPlayer);
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

    private int getDirection(@Nonnull Location currentLocation, @Nonnull Location target) {
        return currentLocation.getDirection(target);
    }

    private boolean isCurrentPathValid() {
        Path path = currentPath;
        if (path == null) {
            log.debug("Path is not valid: Current path is NULL");
            return false;
        }
        Location destination = path.getDestination();
        if (destination == null) {
            log.debug("Path is not valid: Path destination is NULL");
            return false;
        }
        if (!destination.equals(targetLocation)) {
            log.debug("Path is not valid: Destination does not equal the current target location.");
            return false;
        }
        return true;
    }

    @Nullable
    private Path calculateNewPath(@Nonnull Location currentLocation) {
        log.info("Calculating a new path to: {}", targetLocation);
        PathFindingAlgorithm algorithm = pathFindingAlgorithm;
        if (getMovement().getDefaultMovementMode() == CharMovementMode.Walk) {
            return algorithm.findPath(World.getMap(), currentLocation, targetLocation, targetDistance, Walk);
        } else {
            return algorithm.findPath(World.getMap(), currentLocation, targetLocation, targetDistance, Walk, Run);
        }
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
        targetAction = action;
    }

    @Override
    public String toString() {
        return "Walk to target movement handler";
    }
}
