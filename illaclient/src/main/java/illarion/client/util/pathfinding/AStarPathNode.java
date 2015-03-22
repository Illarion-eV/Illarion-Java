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

import illarion.client.world.MapTile;
import illarion.common.types.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the path node implementation used for the A* algorithm that contains all required node information to get the
 * algorithm to work.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class AStarPathNode extends AbstractPathNode implements Comparable<AStarPathNode> {
    /**
     * The square root of two.
     */
    private static final double SQRT2 = 1.4142135623730951;

    /**
     * The cost to reach this field.
     */
    private final int cost;

    /**
     * This flag stores if the field is blocked or not.
     */
    private final boolean blocked;

    /**
     * The predicted cost to reach the target location from this field
     */
    private final int heuristic;

    /**
     * The node that prepends this node.
     */
    @Nullable
    private final AStarPathNode parentNode;

    /**
     * Create a node on the path.
     *
     * @param parentNode the parent of this node or {@code null} in case this node is the first step on the path.
     * @param tile the tile this node is assigned to
     * @param method the movement method to reach this node
     * @param approachDirection the this tile is approached from
     * @param heuristic the predicted cost to reach the target from this location
     * @param walkingTile the tile that is stepped over in case running is used as movement
     */
    AStarPathNode(
            @Nullable AStarPathNode parentNode,
            @Nonnull MapTile tile,
            @Nonnull PathMovementMethod method,
            @Nonnull Direction approachDirection, int heuristic, @Nullable MapTile walkingTile) {
        super(tile.getCoordinates(), method);
        blocked = tile.isBlocked();
        this.heuristic = heuristic;
        this.parentNode = parentNode;
        if (heuristic > 0) {
            int tileCost = tile.getMovementCost();
            if ((method == PathMovementMethod.Run) && (walkingTile != null)) {
                tileCost += walkingTile.getMovementCost();
                tileCost = (int) (0.6 * tileCost);
            }
            if (approachDirection.isDiagonal()) {
                tileCost = (int) (SQRT2 * tileCost);
            }
            cost = (parentNode == null) ? tileCost : (parentNode.cost + tileCost);
        } else {
            cost = 0;
        }
    }

    /**
     * Get the predicted cost to reach the target.
     *
     * @return the predicted cost to reach the target
     */
    private int getPredictedCost() {
        return heuristic + cost;
    }

    @Override
    public int compareTo(@Nonnull AStarPathNode o) {
        int result = Integer.compare(getPredictedCost(), o.getPredictedCost());
        return (result == 0) ? 1 : result;
    }

    @Override
    @Nonnull
    public String toString() {
        return getLocation() + " Predicted cost: " + getPredictedCost();
    }

    public int getCost() {
        return cost;
    }

    public boolean isBlocked() {
        return blocked;
    }

    @Nullable
    public AStarPathNode getParentNode() {
        return parentNode;
    }
}
