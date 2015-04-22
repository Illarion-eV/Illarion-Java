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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * This is not actually a path finding algorithm, but rather a wrapper around another one used to measure the
 * performance of the path finding algorithm.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MeasureWrapper implements PathFindingAlgorithm {
    /**
     * The logger utilized by this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureWrapper.class);

    /**
     * The wrapped algorithm.
     */
    @Nonnull
    private final PathFindingAlgorithm wrappedAlgorithm;

    /**
     * Create a new measurement wrapper and set the algorithm that is wrapped.
     *
     * @param wrappedAlgorithm the wrapped algorithm
     */
    public MeasureWrapper(@Nonnull PathFindingAlgorithm wrappedAlgorithm) {
        this.wrappedAlgorithm = wrappedAlgorithm;
    }

    @Nullable
    @Override
    public Path findPath(
            @Nonnull MoveCostProvider costProvider,
            @Nonnull ServerCoordinate start,
            @Nonnull ServerCoordinate end,
            int approachDistance, @Nonnull Collection<Direction> allowedDirections,
            @Nonnull CharMovementMode movementMethod,
            @Nonnull CharMovementMode... movementMethods) {
        long startTime = System.currentTimeMillis();
        Path path = wrappedAlgorithm
                .findPath(costProvider, start, end, approachDistance, allowedDirections, movementMethod, movementMethods);
        if (LOGGER.isInfoEnabled()) {
            long timeElapsed = System.currentTimeMillis() - startTime;
            LOGGER.info("Path finding from {} to {} took {} milliseconds to create {} using the directions {}", start,
                        end, timeElapsed, path, allowedDirections);
        }

        return path;
    }
}
