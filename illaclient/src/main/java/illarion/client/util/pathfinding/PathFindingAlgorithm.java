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

import illarion.client.world.GameMap;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * This is the shared interface of a algorithm that can be used in path finding.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface PathFindingAlgorithm {
    /**
     * Search for a path between two points.
     *
     * @param map the game map that delivers the data for the algorithm
     * @param start the location where the character is currently located at
     * @param end the location that is the target of the path, in case this location is blocked the character is
     * supposed to move to one of the locations next to this one, if theses are blocked as well the path
     * finding has to fail
     * @param approachDistance the distance in tiles that is allowed to keep from the target. The path finder will stop
     * once a path to a tile is found that reaches a tile with this distance to the target tile
     * @param allowedDirections the direction that are allowed to be used for finding the path
     * @param movementMethod the first of the movement methods the pathfinder is expected to use
     * @param movementMethods the additional movement methods the pathfinder is expected to use
     * @return the calculated path or {@code null} in case the path finding failed
     */
    @Nullable
    Path findPath(
            @Nonnull GameMap map,
            @Nonnull ServerCoordinate start,
            @Nonnull ServerCoordinate end,
            int approachDistance, @Nonnull Collection<Direction> allowedDirections,
            @Nonnull PathMovementMethod movementMethod,
            @Nonnull PathMovementMethod... movementMethods);
}
