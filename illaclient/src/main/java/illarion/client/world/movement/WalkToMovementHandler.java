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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static illarion.client.util.pathfinding.PathMovementMethod.Run;
import static illarion.client.util.pathfinding.PathMovementMethod.Walk;

/**
 * This movement handler is used to approach a specified location.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class WalkToMovementHandler extends AbstractMovementHandler implements TargetMovementHandler {
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(WalkToMovementHandler.class);
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

    WalkToMovementHandler(@Nonnull Movement movement) {
        super(movement);
        pathFindingAlgorithm = new AStar();
        targetLocation = new Location();
    }

    @Nonnull
    @Override
    public StepData getNextStep() {
        if (!targetSet) {
            return new DefaultStepData(CharMovementMode.None, 0);
        }
        if (getPlayerLocation().getDistance(targetLocation) <= targetDistance) {
            targetSet = false;
            executeTargetAction();
            return new DefaultStepData(CharMovementMode.None, 0);
        }
        Path activePath = isCurrentPathValid() ? currentPath : calculateNewPath();
        assert activePath != null;

        PathNode node = activePath.nextStep();
        if (node == null) {
            targetSet = false;
            executeTargetAction();
            return new DefaultStepData(CharMovementMode.None, 0);
        }
        if (!isPathNodeValid(node)) {
            currentPath = calculateNewPath();
            node = activePath.nextStep();
            if (node == null) {
                return new DefaultStepData(CharMovementMode.None, 0);
            }
        }
        return new DefaultStepData(convertMovementMode(node.getMovementMethod()), getDirection(node.getLocation()));
    }

    private void executeTargetAction() {
        Runnable action = targetAction;
        if (action != null) {
            LOGGER.info("Executing target reached action!");
            action.run();
        }
    }

    private boolean isPathNodeValid(@Nonnull PathNode node) {
        int distanceToPlayer = getPlayerLocation().getDistance(node.getLocation());
        switch (node.getMovementMethod()) {
            case Walk:
                return distanceToPlayer == 1;
            case Run:
                return distanceToPlayer <= 2;
        }
        return false;
    }

    @Nonnull
    private Location getPlayerLocation() {
        return getMovement().getPlayer().getLocation();
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

    private int getDirection(@Nonnull Location target) {
        return getPlayerLocation().getDirection(target);
    }

    private boolean isCurrentPathValid() {
        Path path = currentPath;
        if (path == null) {
            return false;
        }
        Location destination = path.getDestination();
        return (destination != null) && destination.equals(targetLocation);
    }

    private Path calculateNewPath() {
        Location startLocation = getPlayerLocation();
        if (getMovement().getDefaultMovementMode() == CharMovementMode.Walk) {
            return pathFindingAlgorithm.findPath(World.getMap(), startLocation, targetLocation, targetDistance, Walk);
        } else {
            return pathFindingAlgorithm
                    .findPath(World.getMap(), startLocation, targetLocation, targetDistance, Walk, Run);
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
