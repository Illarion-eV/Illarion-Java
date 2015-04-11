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
import illarion.common.types.ServerCoordinate;
import org.jetbrains.annotations.Contract;

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
     * The cost to reach this field.
     */
    private final int cost;

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
     * @param target the location this node is bound to
     * @param method the movement method to reach this node
     * @param cost the cost to reach this tile
     * @param heuristic the predicted cost to reach the target from this location
     */
    AStarPathNode(
            @Nullable AStarPathNode parentNode,
            @Nonnull ServerCoordinate target,
            @Nonnull CharMovementMode method,
            int cost,
            int heuristic) {
        super(target, method);
        this.parentNode = parentNode;
        this.cost = (parentNode == null) ? cost : (parentNode.cost + cost);
        this.heuristic = heuristic;
    }

    /**
     * Get the predicted cost to reach the target.
     *
     * @return the predicted cost to reach the target
     */
    @Contract(pure = true)
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

    @Nullable
    public AStarPathNode getParentNode() {
        return parentNode;
    }
}
