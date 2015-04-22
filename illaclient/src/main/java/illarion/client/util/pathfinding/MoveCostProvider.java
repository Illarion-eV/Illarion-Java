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

import illarion.client.world.CharMovementMode;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;

import javax.annotation.Nonnull;

/**
 * This interface is used in the path finding algorithms to supply them with the movement costs for a step from one
 * tile to the next.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface MoveCostProvider {
    /**
     * Constant return value for {@link #getMovementCost(ServerCoordinate, CharMovementMode, Direction)}  in case the
     * move is blocked.
     */
    int BLOCKED = -1;

    /**
     * Get the cost for a move from the origin.
     *
     * @param origin    the start location of the move
     * @param mode      the movement method
     * @param direction the direction of the move
     * @return the cost of the move or {@link #BLOCKED} in case the move is not possible
     */
    int getMovementCost(@Nonnull ServerCoordinate origin, @Nonnull CharMovementMode mode, @Nonnull Direction direction);
}
