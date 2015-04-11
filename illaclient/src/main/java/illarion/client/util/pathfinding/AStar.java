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
package illarion.client.util.pathfinding;

import illarion.client.util.ConnectionPerformanceClock;
import illarion.client.world.CharMovementMode;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class implements the A* path finding algorithm.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AStar implements PathFindingAlgorithm {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(AStar.class);

    @Nullable
    @Override
    public Path findPath(
            @Nonnull MoveCostProvider costProvider,
            @Nonnull ServerCoordinate start,
            @Nonnull ServerCoordinate end,
            int approachDistance, @Nonnull Collection<Direction> allowedDirections,
            @Nonnull CharMovementMode movementMethod,
            @Nonnull CharMovementMode... movementMethods) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("Start and target location must not be equal.");
        }
        /* Pre-Checks */
        if (approachDistance < 0) {
            throw new IllegalArgumentException("The approach distance must not be negative.");
        }
        if (start.getZ() != end.getZ()) {
            /* Different levels are not supported by this algorithm. */
            return null;
        }
        if (start.getDistance(end) <= approachDistance) {
            /* close enough */
            return null;
        }
        log.debug("Searching path from {} to {} getting as close as {} tiles", start, end, approachDistance);
        /* Setting up the data structures. */
        /* Nodes that are in this set were yet not fully processed. */
        NavigableSet<AStarPathNode> openNodes = new TreeSet<>();
        /* The list of nodes and their corresponding locations. */
        Map<ServerCoordinate, AStarPathNode> knownNodes = new HashMap<>();
        /* The methods of movement that apply. */
        EnumSet<CharMovementMode> movementMethodSettings = EnumSet.of(movementMethod, movementMethods);

        expandNode(costProvider, end, null, start, allowedDirections, movementMethodSettings, openNodes);

        while (!openNodes.isEmpty()) {
            /* Take the unchecked node closest to the target. */
            AStarPathNode currentNode = openNodes.pollFirst();
            if (currentNode.getLocation().getStepDistance(end) <= approachDistance) {
                Path createdPath = buildPath(currentNode);
                log.debug("Current node is within range. Building path: {}", createdPath);
                return createdPath;
            }
            AStarPathNode alternative = knownNodes.get(currentNode.getLocation());
            if ((alternative == null) || (alternative.getCost() > currentNode.getCost())) {
                knownNodes.put(currentNode.getLocation(), currentNode);
                expandNode(costProvider, end, currentNode, currentNode.getLocation(), allowedDirections, movementMethodSettings,
                           openNodes);
            }
        }

        return null;
    }

    @Nonnull
    private static Path buildPath(@Nonnull AStarPathNode lastNode) {
        LinkedList<PathNode> path = new LinkedList<>();
        @Nullable AStarPathNode nextNode = lastNode;
        while (nextNode != null) {
            path.addFirst(nextNode);
            nextNode = nextNode.getParentNode();
        }
        return new Path(path);
    }

    private static void expandNode(
            @Nonnull MoveCostProvider costProvider,
            @Nonnull ServerCoordinate end,
            @Nullable AStarPathNode nodeToExpand,
            @Nonnull ServerCoordinate origin, @Nonnull Iterable<Direction> allowedDirections,
            @Nonnull Collection<CharMovementMode> movementMethods,
            @Nonnull Collection<AStarPathNode> storage) {
        if (movementMethods.isEmpty()) {
            throw new IllegalArgumentException("No movement methods selected. This is not valid.");
        }
        for (Direction dir : allowedDirections) {
            ServerCoordinate walkingCoordinates = new ServerCoordinate(origin, dir);
            if (movementMethods.contains(CharMovementMode.Walk)) {
                int moveCost = costProvider.getMovementCost(origin, CharMovementMode.Walk, dir);
                if (moveCost != MoveCostProvider.BLOCKED) {
                    /* Additional cost for distance. */
                    moveCost += (int) (150 * (dir.isDiagonal() ? 1.4142135623730951 : 1.0));
                    /* Additional cost for current ping. */
                    moveCost += (int) ConnectionPerformanceClock.getMaxServerPing();

                    storage.add(new AStarPathNode(nodeToExpand, walkingCoordinates, CharMovementMode.Walk, moveCost,
                            getHeuristic(walkingCoordinates, end)));
                } else {
                    continue;
                }
            }
            if (walkingCoordinates.equals(end)) {
                continue;
            }
            if (movementMethods.contains(CharMovementMode.Run)) {
                ServerCoordinate runningCoordinates = new ServerCoordinate(walkingCoordinates, dir);
                int moveCost = costProvider.getMovementCost(origin, CharMovementMode.Run, dir);
                if (moveCost != MoveCostProvider.BLOCKED) {
                    /* Additional cost for distance. */
                    moveCost += (int) (300 * (dir.isDiagonal() ? 1.4142135623730951 : 1.0));
                    /* Additional cost for current ping. */
                    moveCost += (int) ConnectionPerformanceClock.getMaxServerPing();

                    storage.add(new AStarPathNode(nodeToExpand, runningCoordinates, CharMovementMode.Run, moveCost,
                            getHeuristic(runningCoordinates, end)));
                }
            }
        }
    }

    private static int getHeuristic(@Nonnull ServerCoordinate currentLocation,
                                    @Nonnull ServerCoordinate targetLocation) {
        int dX = Math.abs(currentLocation.getX() - targetLocation.getX());
        int dY = Math.abs(currentLocation.getY() - targetLocation.getY());

        int dMax = Math.max(dX, dY);
        int dMin = Math.min(dX, dY);

        return (int) (((dMax - dMin) + (dMin * 1.4142135623730951)) * 300);
    }
}
