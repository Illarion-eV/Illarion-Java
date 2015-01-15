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
package illarion.client.util.pathfinding;

import illarion.client.world.GameMap;
import illarion.client.world.MapTile;
import illarion.common.types.Direction;
import illarion.common.types.Location;
import illarion.common.util.FastMath;
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
    private static final Logger log = LoggerFactory.getLogger(AStar.class);

    @Nullable
    @Override
    public Path findPath(
            @Nonnull GameMap map,
            @Nonnull Location start,
            @Nonnull Location end,
            int approachDistance, @Nonnull Collection<Direction> allowedDirections,
            @Nonnull PathMovementMethod movementMethod,
            @Nonnull PathMovementMethod... movementMethods) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("Start and target location must not be equal.");
        }
        /* Pre-Checks */
        if (approachDistance < 0) {
            throw new IllegalArgumentException("The approach distance must not be negative.");
        }
        if (start.getScZ() != end.getScZ()) {
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
        Map<Location, AStarPathNode> knownNodes = new HashMap<>();
        /* The methods of movement that apply. */
        EnumSet<PathMovementMethod> movementMethodSettings = EnumSet.of(movementMethod, movementMethods);

        expandNode(map, end, null, start, allowedDirections, movementMethodSettings, openNodes);

        while (!openNodes.isEmpty()) {
            /* Take the unchecked node closest to the target. */
            AStarPathNode currentNode = openNodes.pollFirst();
            if (currentNode.getLocation().getDistance(end) <= approachDistance) {
                Path createdPath = buildPath(currentNode);
                log.debug("Current node is within range. Building path: {}", createdPath);
                return createdPath;
            }
            AStarPathNode alternative = knownNodes.get(currentNode.getLocation());
            if ((alternative == null) || (alternative.getCost() > currentNode.getCost())) {
                knownNodes.put(currentNode.getLocation(), currentNode);
                expandNode(map, end, currentNode, currentNode.getLocation(), allowedDirections, movementMethodSettings,
                           openNodes);
            }
        }

        return null;
    }

    @Nonnull
    private static Path buildPath(@Nonnull AStarPathNode lastNode) {
        LinkedList<PathNode> path = new LinkedList<>();
        @Nullable AStarPathNode nextNode = lastNode;
        if (nextNode.isBlocked()) {
            /* Skip the last node in case its blocked and walk to the tile next to it. */
            nextNode = nextNode.getParentNode();
        }
        while (nextNode != null) {
            path.addFirst(nextNode);
            nextNode = nextNode.getParentNode();
        }
        return new Path(path);
    }

    private static void expandNode(
            @Nonnull GameMap map,
            @Nonnull Location end,
            @Nullable AStarPathNode nodeToExpand,
            @Nonnull Location origin, @Nonnull Iterable<Direction> allowedDirections,
            @Nonnull Collection<PathMovementMethod> movementMethods,
            @Nonnull Collection<AStarPathNode> storage) {
        if (movementMethods.isEmpty()) {
            throw new IllegalArgumentException("No movement methods selected. This is not valid.");
        }
        for (Direction dir : allowedDirections) {
            Location walkTarget = new Location(origin, dir);
            MapTile walkingTargetTile = map.getMapAt(walkTarget);
            if (movementMethods.contains(PathMovementMethod.Walk)) {
                if ((walkingTargetTile != null) &&
                        (!walkingTargetTile.isBlocked() || walkingTargetTile.getLocation().equals(end))) {
                    storage.add(new AStarPathNode(nodeToExpand, walkingTargetTile, PathMovementMethod.Walk, dir,
                                                  getHeuristic(walkTarget, end), null));
                } else {
                    continue;
                }
            }
            if (movementMethods.contains(PathMovementMethod.Run)) {
                Location runTarget = new Location(walkTarget, dir);
                MapTile runningTargetTile = map.getMapAt(runTarget);
                if ((runningTargetTile != null) && !runningTargetTile.isBlocked()) {
                    storage.add(new AStarPathNode(nodeToExpand, runningTargetTile, PathMovementMethod.Run, dir,
                                                  getHeuristic(runTarget, end), walkingTargetTile));
                }
            }
        }
    }

    private static int getHeuristic(@Nonnull Location currentLocation, @Nonnull Location targetLocation) {
        return FastMath.floor(currentLocation.getSqrtDistance(targetLocation) * 10.f);
    }
}
