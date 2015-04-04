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

import illarion.client.util.pathfinding.AStar;
import illarion.client.util.pathfinding.Path;
import illarion.client.util.pathfinding.PathFindingAlgorithm;
import illarion.client.util.pathfinding.PathNode;
import illarion.client.world.CharMovementMode;
import illarion.client.world.World;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;
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

import static illarion.client.world.CharMovementMode.Run;
import static illarion.client.world.CharMovementMode.Walk;

/**
 * This movement handler is used to approach a specified location.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class WalkToMovementHandler extends AbstractMovementHandler implements TargetMovementHandler {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(WalkToMovementHandler.class);
    @Nonnull
    private static final Marker marker = MarkerFactory.getMarker("Movement");

    /**
     * The path finder used to calculate the paths towards the target location.
     */
    @Nonnull
    private final PathFindingAlgorithm pathFindingAlgorithm;

    @Nullable
    private ServerCoordinate targetLocation;
    private int targetDistance;

    @Nullable
    private Runnable targetAction;

    @Nullable
    private Path currentPath;

    @Nonnull
    private final Collection<Direction> allowedDirections;

    WalkToMovementHandler(@Nonnull Movement movement) {
        super(movement);
        allowedDirections = EnumSet.allOf(Direction.class);
        pathFindingAlgorithm = new AStar();
    }

    @Nullable
    @Override
    public StepData getNextStep(@Nonnull ServerCoordinate currentLocation) {
        ServerCoordinate target = getTargetLocation();

        int remainingDistance = currentLocation.getStepDistance(target);
        log.debug(marker, "Remaining distance to target: {} Expected distance: {}", remainingDistance, targetDistance);
        if (remainingDistance <= targetDistance) {
            return new DefaultStepData(CharMovementMode.None, finishMove(currentLocation));
        }
        Path activePath;
        if (isCurrentPathValid()) {
            activePath = currentPath;
        } else {
            activePath = calculateNewPath(currentLocation);
            currentPath = activePath;
        }
        if ((activePath == null) || activePath.isEmpty()) {
            return new DefaultStepData(CharMovementMode.None, finishMove(currentLocation));
        }

        PathNode node = activePath.nextStep();
        if (node == null) {
            return new DefaultStepData(CharMovementMode.None, finishMove(currentLocation));
        }
        if (!isPathNodeValid(currentLocation, node)) {
            activePath = calculateNewPath(currentLocation);
            currentPath = activePath;
            if (activePath == null) {
                return new DefaultStepData(CharMovementMode.None, finishMove(currentLocation));
            }
            node = activePath.nextStep();
            if (node == null) {
                return new DefaultStepData(CharMovementMode.None, finishMove(currentLocation));
            }
            if (!isPathNodeValid(currentLocation, node)) {
                Direction lastDirection = currentLocation.getDirection(target);
                targetLocation = null;
                targetAction = null;
                return new DefaultStepData(CharMovementMode.None, lastDirection);
            }
        }
        log.debug(marker, "Performing step to: {}", node.getLocation());
        Direction moveDir = currentLocation.getDirection(node.getLocation());
        if (activePath.isEmpty() && (targetDistance == 0) && !target.equals(node.getLocation())) {
            targetDistance = 1;
        }
        return new DefaultStepData(node.getMovementMethod(), moveDir);
    }

    @Nullable
    private Direction finishMove(@Nonnull ServerCoordinate currentLocation) {
        if (targetLocation == null) {
            throw new IllegalStateException("Finishing a move is not possible while there is no target location set.");
        }
        Direction direction = null;
        int remainingDistance = currentLocation.getStepDistance(targetLocation);
        if (remainingDistance > 0) {
            direction = currentLocation.getDirection(targetLocation);
        }
        executeTargetAction();
        targetLocation = null;
        return direction;
    }

    private void executeTargetAction() {
        Runnable action = targetAction;
        targetAction = null;
        if (action != null) {
            action.run();
        }
    }

    private static boolean isPathNodeValid(@Nonnull ServerCoordinate currentLocation, @Nonnull PathNode node) {
        int distanceToPlayer = currentLocation.getStepDistance(node.getLocation());
        switch (node.getMovementMethod()) {
            case Walk:
                if (distanceToPlayer == 1) {
                    return true;
                }
                break;
            case Run:
                if (!World.getPlayer().getCarryLoad().isRunningPossible()) {
                    return false;
                }
                if (distanceToPlayer == 2) {
                    return true;
                }
                break;
        }
        log.warn(marker, "Next path node {} is out of range: {}", node, distanceToPlayer);
        return false;
    }

    protected int getTargetDistance() {
        return targetDistance;
    }

    protected void increaseTargetDistance() {
        targetDistance++;
    }

    private boolean isCurrentPathValid() {
        Path path = currentPath;
        if (path == null) {
            log.debug(marker, "Path is not valid: Current path is NULL");
            return false;
        }
        ServerCoordinate destination = path.getDestination();
        if (destination == null) {
            log.debug(marker, "Path is not valid: Path destination is NULL");
            return false;
        }
        if (!destination.equals(targetLocation)) {
            log.debug(marker, "Path is not valid: Destination ({}) does not equal the current target location ({}).",
                      destination, targetLocation);
            return false;
        }
        return true;
    }

    @Nullable
    protected Path calculateNewPath(@Nonnull ServerCoordinate currentLocation) {
        ServerCoordinate target = getTargetLocation();

        log.info(marker, "Calculating a new path to: {}", target);
        PathFindingAlgorithm algorithm = pathFindingAlgorithm;

        switch (getMovementMode()) {
            case Walk:
                return algorithm.findPath(getMovement(), currentLocation, target, targetDistance,
                                          getAllowedDirections(), Walk);
            case Run:
                return algorithm.findPath(getMovement(), currentLocation, target, targetDistance,
                                          getAllowedDirections(), Walk, Run);
            default:
                return null;
        }
    }

    @Nonnull
    protected CharMovementMode getMovementMode() {
        if (!World.getPlayer().getCarryLoad().isRunningPossible()) {
            return Walk;
        }
        CharMovementMode mode = getMovement().getDefaultMovementMode();
        if (getMovement().isMovementModePossible(mode)) {
            return mode;
        }
        return Walk;
    }

    @Override
    public void disengage(boolean transferAllowed) {
        super.disengage(transferAllowed);
        targetLocation = null;
        setTargetReachedAction(null);
    }

    @Nonnull
    protected Collection<Direction> getAllowedDirections() {
        return Collections.unmodifiableCollection(allowedDirections);
    }

    @Override
    public void walkTo(@Nonnull ServerCoordinate target, int distance) {
        setTargetReachedAction(null);
        targetLocation = target;
        targetDistance = distance;
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

    @Nonnull
    @Override
    public String toString() {
        return "Walk to target movement handler";
    }

    protected boolean isTargetSet() {
        return targetLocation != null;
    }

    @Nonnull
    protected ServerCoordinate getTargetLocation() {
        if (targetLocation == null) {
            throw new IllegalStateException("The target location is not set.");
        }
        return targetLocation;
    }
}
