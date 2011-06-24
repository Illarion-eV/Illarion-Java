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

import java.util.LinkedList;

import illarion.common.util.Location;

/**
 * A path created by the path finder that stores the path nodes.
 */
public final class Path {

    /**
     * List if the path nodes that create this path.
     */
    private final LinkedList<PathNode> path = new LinkedList<PathNode>();

    /**
     * Default constructor for a new path.
     */
    public Path() {
        // nothing to do
    }

    /**
     * Get the destination location of the path. So the location where the path
     * ends.
     * 
     * @return the destination of the path
     */
    public Location getDestination() {
        if (!path.isEmpty()) {
            return path.getLast().getLocation();
        }
        return null;
    }

    /**
     * Get the next step of this path and remove it from the list. This function
     * returns always the first value entry of the path node list and removed it
     * from the list then.
     * 
     * @return the next path node of this path
     */
    public PathNode nextStep() {
        PathNode node = null;
        if (!path.isEmpty()) {
            node = path.removeFirst();
        }
        return node;
    }

    /**
     * Add a path node to the very beginning of the path.
     * 
     * @param newNode the new path node that is added to the beginning of the
     *            path
     */
    public void prependStep(final PathNode newNode) {
        path.addFirst(newNode);
    }

    /**
     * Create a string representation of this path. Containing the value of the
     * path and the path itself.
     * 
     * @return the string that defines the path
     */
    @Override
    @SuppressWarnings("nls")
    public String toString() {
        return "Path: " + path.toString();
    }
}
