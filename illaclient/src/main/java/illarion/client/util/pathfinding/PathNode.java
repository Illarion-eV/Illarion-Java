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

import illarion.common.types.Location;

import javax.annotation.Nonnull;

/**
 * This interface represents a node along the calculated path.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface PathNode {
    /**
     * Get the location this node is assigned to.
     *
     * @return the location of the node
     */
    @Nonnull
    Location getLocation();

    /**
     * Get the method of movement that is used to reach this node.
     *
     * @return get the movement method
     */
    @Nonnull
    PathMovementMethod getMovementMethod();
}
