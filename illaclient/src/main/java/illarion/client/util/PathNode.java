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
package illarion.client.util;

import gnu.trove.map.hash.TLongObjectHashMap;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A single path node that is used during the path finding and represents a single step on the path that is searched or
 * was found.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class PathNode implements Comparable<PathNode> {
    /**
     * The cache for the path nodes that were loaded already. It needed to store them so the pathfinder can access
     * values calculated before.
     */
    @Nonnull
    private static final TLongObjectHashMap<PathNode> CACHE = new TLongObjectHashMap<>();

    /**
     * Is the tile blocked or not.
     */
    private boolean blocked;

    /**
     * True if the tile is in the list of tiles that were already checked.
     */
    private boolean close;

    /**
     * The movement cost to reach this tile at the current settings. Means the movement cost of this tile and of all
     * tiles stepped on before.
     */
    private int cost;

    /**
     * The depth of the tile. Means how many steps it is away from the starting position.
     */
    private int depth;

    /**
     * The heuristic value, means the estimated movement cost to reach the target position.
     */
    private int heuristic;

    /**
     * The location this path node represents.
     */
    @Nonnull
    private final Location location;

    /**
     * True in case the path node is in the list of tile that still need to be checked.
     */
    private boolean open;

    /**
     * The parent node, so the node the character steps on before it reaches this path node.
     */
    @Nullable
    private PathNode parent;

    /**
     * The map tile of this path node, needed to access some tile related information.
     */
    @Nullable
    private MapTile tile;

    /**
     * Constructor for a path node.
     */
    PathNode() {
        location = new Location();
    }

    /**
     * Delete all nodes in the cache. Needed for a fresh start, like a new path finding.
     */
    public static void clearCache() {
        CACHE.clear();
    }

    /**
     * Get a path node on a specified location. The path node is taken from the cache and is only newly created in case
     * it is not present in the cache.
     *
     * @param loc the location the path node shall represent
     * @return the path node, fresh created or from the cache
     */
    public static PathNode getNode(@Nonnull final Location loc) {
        final long key = loc.getKey();
        PathNode node = CACHE.get(key);
        if (node == null) {
            node = new PathNode();
            node.setPosition(loc);

            CACHE.put(key, node);
        }
        return node;
    }

    /**
     * Compare this path node with another one. Needed to sort the path nodes in the list of path nodes that are still
     * to check, so the path nodes that most likely are checked first. The {@link #getCost()} and the heuristic value
     * are added and compared for both nodes.
     *
     * @param o the path node this path nodes is compared with
     * @return 0 in case the result of the sumation is the same for both nodes, 1 in case the value of this node is
     * larger, else -1
     * @see Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(@Nonnull final PathNode o) {
        final int ownf = heuristic + cost;
        final int otherf = o.heuristic + o.cost;

        if (ownf < otherf) {
            return -1;
        }
        if (ownf > otherf) {
            return 1;
        }
        return 0;
    }

    /**
     * Check if this path node and another one is equal.
     *
     * @param o the path node that is possibly equal with this path node.
     * @return true in case this path node and the path node in the function parameter are equal
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (super.equals(o)) {
            return true;
        }
        if (!(o instanceof PathNode)) {
            return false;
        }
        final PathNode tempNode = (PathNode) o;
        return location.equals(tempNode.getLocation());
    }

    /**
     * Check if the tile is in the list of tiles that are already checked.
     *
     * @return true if its in the list
     */
    public boolean isClosed() {
        return close;
    }

    /**
     * Get the movement cost to reach this tile when walking from the starting location on the current path to this
     * path
     * node.
     *
     * @return the walking cost to reach this tile
     */
    public int getCost() {
        return cost;
    }

    /**
     * Get the depth of the tile, so the steps needed to reach this node.
     *
     * @return the steps needed to reach this path node
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Get the location of this path node.
     *
     * @return the location of this path node
     */
    @Nonnull
    public Location getLocation() {
        return location;
    }

    /**
     * Check if the tile is in the list of tiles that still need to be checked.
     *
     * @return true if its in the list
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Get the parent node, so the node the character steps on before steping on this node.
     *
     * @return the parent path node
     */
    @Nullable
    public PathNode getParent() {
        return parent;
    }

    /**
     * Get the movement cost to get on this path node, independent from all tiles before.
     *
     * @return the movement cost
     */
    public int getValue() {
        if (tile == null) {
            return 0;
        }
        return tile.getMovementCost();
    }

    /**
     * Get the has code for this path node.
     *
     * @return the hash code
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (int) (location.getKey() % Integer.MAX_VALUE);
    }

    /**
     * Check if this path node is blocked by anything, such as a item or a other character or simply a tile that is not
     * passable.
     *
     * @return true if the tile is blocked
     */
    public boolean isBlocked() {
        return blocked;
    }

    /**
     * Change the status if the path node is in the list of tiles that are already checked, or not.
     *
     * @param newClose true if the tile is in the list
     */
    public void setClosed(final boolean newClose) {
        close = newClose;
    }

    /**
     * Set the movement cost to reach this tile when walking from the starting location on the current path to this
     * path
     * node.
     *
     * @param newCost the new value for the movement cost
     */
    public void setCost(final int newCost) {
        cost = newCost;
    }

    /**
     * Set the enstimated movement cost to reach the target location.
     *
     * @param newHeuristic the new estimated walking cost to reach the target.
     */
    public void setHeuristic(final int newHeuristic) {
        heuristic = newHeuristic;
    }

    /**
     * Change the status if the path node is in the list of tiles that still need to be checked, or not.
     *
     * @param newOpen true if the tile is in the list
     */
    public void setOpen(final boolean newOpen) {
        open = newOpen;
    }

    /**
     * Set the parent node, that is before this node in the path. This also updates the depth to the depth of the
     * parent
     * node + 1.
     *
     * @param newParent the new parent node
     */
    public void setParent(@Nonnull final PathNode newParent) {
        parent = newParent;
        depth = newParent.getDepth() + 1;
    }

    /**
     * Set the location of this node. This also fetches the required references for this class to work properly.
     *
     * @param location the location of this node
     */
    public void setPosition(@Nonnull final Location location) {
        this.location.set(location);
        tile = World.getMap().getMapAt(this.location);
        blocked = (tile == null) || tile.isBlocked();
    }

    /**
     * Get a string that represents this path node.
     *
     * @return a string that contains the position of this path node
     * @see Object#toString()
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "(" + location.getScX() + ',' + location.getScY() + ')';
    }
}
