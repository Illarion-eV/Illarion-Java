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
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class implements the A* path finding algorithm.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AStar implements PathFindingAlgorithm {
    @Nullable
    @Override
    public Path findPath(
            @Nonnull GameMap map,
            @Nonnull Location start,
            @Nonnull Location end,
            int approachDistance,
            @Nonnull PathMovementMethod movementMethod,
            @Nonnull PathMovementMethod... movementMethods) {
        /* Pre-Checks */
        if (approachDistance < 0) {
            throw new IllegalArgumentException("The approach distance must not be negative.");
        }
        if (start.getScZ() != end.getScZ()) {
            /* Different levels are not supported by this algorithm. */
            return null;
        }
        /* Setting up the data structures. */
        /* Nodes that are in this set were yet not fully processed. */
        NavigableSet<AStarPathNode> openNodes = new TreeSet<>();
        /* The list of nodes and their corresponding locations. */
        Map<Location, AStarPathNode> knownNodes = new HashMap<>();
        /* The methods of movement that apply. */
        EnumSet<PathMovementMethod> movementMethodSettings = EnumSet.of(movementMethod, movementMethods);

        expandNode(map, end, null, start, movementMethodSettings, openNodes);

        while (!openNodes.isEmpty()) {
            /* Take the unchecked node closest to the target. */
            AStarPathNode currentNode = openNodes.pollFirst();
            if (currentNode.getLocation().getDistance(end) <= approachDistance) {
                return buildPath(currentNode);
            }
            AStarPathNode alternative = knownNodes.get(currentNode.getLocation());
            if ((alternative == null) || (alternative.getCost() > currentNode.getCost())) {
                knownNodes.put(currentNode.getLocation(), currentNode);
                expandNode(map, end, currentNode, currentNode.getLocation(), movementMethodSettings, openNodes);
            }
        }

        return null;
    }

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
            @Nonnull GameMap map,
            @Nonnull Location end,
            @Nullable AStarPathNode nodeToExpand,
            @Nonnull Location origin,
            @Nonnull Collection<PathMovementMethod> movementMethods,
            @Nonnull Collection<AStarPathNode> storage) {
        for (int i = 0; i < Location.DIR_MOVE8; i++) {
            Location walkTarget = new Location(origin, i);
            if (movementMethods.contains(PathMovementMethod.Walk)) {
                MapTile walkingTargetTile = map.getMapAt(walkTarget);
                if ((walkingTargetTile != null) && !walkingTargetTile.isBlocked()) {
                    storage.add(new AStarPathNode(nodeToExpand, walkingTargetTile, PathMovementMethod.Walk, i,
                                                  getHeuristic(walkTarget, end)));
                } else {
                    continue;
                }
            }
            if (movementMethods.contains(PathMovementMethod.Run)) {
                Location runTarget = new Location(walkTarget, i);
                MapTile runningTargetTile = map.getMapAt(runTarget);
                if ((runningTargetTile != null) && !runningTargetTile.isBlocked()) {
                        storage.add(new AStarPathNode(nodeToExpand, runningTargetTile, PathMovementMethod.Run, i,
                                                      getHeuristic(runTarget, end)));
                }
            }
        }
    }

    private static int getHeuristic(
            @Nonnull Location currentLocation,
            @Nonnull Location targetLocation) {
        return currentLocation.getDistance(targetLocation) * 2;
    }
}
