/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.util;

import javolution.context.ObjectFactory;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

import illarion.client.world.Game;
import illarion.client.world.MapTile;

import illarion.common.util.Location;
import illarion.common.util.Reusable;

/**
 * A single path node that is used during the path finding and represents a
 * single step on the path that is searched or was found.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.95
 * @version 1.22
 */
public final class PathNode implements Comparable<PathNode>, Reusable {
    /**
     * This procedure is used to recycle all references in the cache.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class CleanCacheProcedure implements
        TObjectProcedure<PathNode> {
        /**
         * Public constructor so the parent class is able to create a instance.
         */
        public CleanCacheProcedure() {
            // nothing to do
        }

        @Override
        public boolean execute(final PathNode node) {
            node.reset();
            node.recycle();
            return true;
        }

    }

    /**
     * The factory that is used to create and buffer the instances of the path
     * nodes.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class PathNodeFactory extends ObjectFactory<PathNode> {
        /**
         * Public constructor to allow the parent class to create proper
         * instances.
         */
        public PathNodeFactory() {
            // nothing to do
        }

        /**
         * Create a new path node.
         * 
         * @return the new path node instance
         */
        @Override
        protected PathNode create() {
            return new PathNode();
        }
    }

    /**
     * The cache for the path nodes that were loaded already. It needed to store
     * them so the pathfinder can access values calculated before.
     */
    private static TLongObjectHashMap<PathNode> cache =
        new TLongObjectHashMap<PathNode>();

    /**
     * The instance of the CleanCacheProcedure instance that is used to clean up
     * the path nodes.
     */
    private static final CleanCacheProcedure CLEAN_HELPER =
        new CleanCacheProcedure();

    /**
     * The factory used to create and buffer the instances of this path nodes.
     */
    private static final PathNodeFactory FACTORY = new PathNodeFactory();

    /**
     * Is the tile blocked or not.
     */
    private boolean blocked = false;

    /**
     * True if the tile is in the list of tiles that were already checked.
     */
    private boolean close = false;

    /**
     * The movement cost to reach this tile at the current settings. Means the
     * movement cost of this tile and of all tiles stepped on before.
     */
    private int cost = 0;

    /**
     * The depth of the tile. Means how many steps it is away from the starting
     * position.
     */
    private int depth = 0;

    /**
     * The heuristic value, means the estimated movement cost to reach the
     * target position.
     */
    private int heuristic = 0;

    /**
     * The location this path node represents.
     */
    private Location loc;

    /**
     * True in case the path node is in the list of tile that still need to be
     * checked.
     */
    private boolean open = false;

    /**
     * The parent node, so the node the character steps on before it reaches
     * this path node.
     */
    private PathNode parent;

    /**
     * The map tile of this path node, needed to access some tile related
     * informations.
     */
    private MapTile tile;

    /**
     * Constructor for a path node.
     */
    PathNode() {
        // nothing to do
    }

    /**
     * Delete all nodes in the cache. Needed for a fresh start, like a new path
     * finding.
     */
    public static void clearCache() {
        cache.forEachValue(CLEAN_HELPER);
        cache.clear();
    }

    /**
     * Get a path node on a specified location. The path node is taken from the
     * cache and is only newly created in case it is not present in the cache.
     * 
     * @param loc the location the path node shall represent
     * @return the path node, fresh created or from the cache
     */
    public static PathNode getNode(final Location loc) {
        final long key = loc.getKey();
        PathNode node = cache.get(key);
        if (node == null) {
            node = FACTORY.object();
            node.setPosition(loc);

            cache.put(key, node);
        }
        return node;
    }

    /**
     * Compare this path node with another one. Needed to sort the path nodes in
     * the list of path nodes that are still to check, so the path nodes that
     * most likely are checked first. The {@link #getCost()} and the heuristic
     * value are added and compared for both nodes.
     * 
     * @param o the path node this path nodes is compared with
     * @return 0 in case the result of the sumation is the same for both nodes,
     *         1 in case the value of this node is larger, else -1
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final PathNode o) {
        final PathNode node = o;

        final int ownf = heuristic + cost;
        final int otherf = node.heuristic + node.cost;

        if (ownf < otherf) {
            return -1;
        } else if (ownf > otherf) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Check if this path node and another one is equal.
     * 
     * @param o the path node that is possibly equal with this path node.
     * @return true in case this path node and the path node in the function
     *         parameter are equal
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PathNode)) {
            return false;
        }
        final PathNode tempNode = (PathNode) o;
        return (loc.equals(tempNode.getLocation()));
    }

    /**
     * Check if the tile is in the list of tiles that are already checked.
     * 
     * @return true if its in the list
     */
    public boolean getClosed() {
        return close;
    }

    /**
     * Get the movement cost to reach this tile when walking from the starting
     * location on the current path to this path node.
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
     * Get the distance from the position of this node to the destination
     * position.
     * 
     * @param dest the destination position
     * @return the distance to the destination position
     */
    public int getDistanceTo(final Location dest) {
        return loc.getDistance(dest);
    }

    /**
     * Get the location of this path node.
     * 
     * @return the location of this path node
     */
    public Location getLocation() {
        return loc;
    }

    /**
     * Check if the tile is in the list of tiles that still need to be checked.
     * 
     * @return true if its in the list
     */
    public boolean getOpen() {
        return open;
    }

    /**
     * Get the parent node, so the node the character steps on before steping on
     * this node.
     * 
     * @return the parent path node
     */
    public PathNode getParent() {
        return parent;
    }

    /**
     * Get the movement cost to get on this path node, independed from all tiles
     * before.
     * 
     * @return the movement cost
     */
    public int getValue() {
        return tile.getMovementCost();
    }

    /**
     * Get the has code for this path node.
     * 
     * @return the hash code
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (int) (loc.getKey() % Integer.MAX_VALUE);
    }

    /**
     * Check if this path node is blocked by anything, such as a item or a other
     * character or simply a tile that is not passable.
     * 
     * @return true if the tile is blocked
     */
    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    @Override
    public void reset() {
        if (loc != null) {
            loc.recycle();
            loc = null;
        }
        tile = null;
        parent = null;
        cost = 0;
        depth = 0;
        blocked = false;
        open = false;
        close = false;
        heuristic = 0;
    }

    /**
     * Change the status if the path node is in the list of tiles that are
     * already checked, or not.
     * 
     * @param newClose true if the tile is in the list
     */
    public void setClosed(final boolean newClose) {
        close = newClose;
    }

    /**
     * Set the movement cost to reach this tile when walking from the starting
     * location on the current path to this path node.
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
     * Change the status if the path node is in the list of tiles that still
     * need to be checked, or not.
     * 
     * @param newOpen true if the tile is in the list
     */
    public void setOpen(final boolean newOpen) {
        open = newOpen;
    }

    /**
     * Set the parent node, that is before this node in the path. This also
     * updates the depth to the depth of the parent node + 1.
     * 
     * @param newParent the new parent node
     */
    public void setParent(final PathNode newParent) {
        parent = newParent;
        depth = newParent.getDepth() + 1;
    }

    /**
     * Set the location of this node. This also fetches the required references
     * for this class to work properly.
     * 
     * @param location the location of this node
     */
    public void setPosition(final Location location) {
        loc = Location.getInstance();
        loc.set(location);
        tile = Game.getMap().getMapAt(loc);
        blocked = ((tile == null) || tile.isBlocked());
    }

    /**
     * Get a string that represents this path node.
     * 
     * @return a string that contains the position of this path node
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "(" + loc.getScX() + "," + loc.getScY() + ")";
    }
}
