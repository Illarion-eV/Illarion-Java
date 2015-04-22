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
package illarion.client.world.movement;

import illarion.common.types.ServerCoordinate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The purpose of this movement handler is in general to approach a specified target location.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface TargetMovementHandler extends MovementHandler {
    /**
     * Start moving towards a location until its within the distance specified.
     *
     * @param target the target location
     * @param distance the distance to the location that is sufficient for the approach
     */
    void walkTo(@Nonnull ServerCoordinate target, int distance);

    /**
     * Set the action that is executed once the target of the path is reached.
     *
     * @param action the action
     */
    void setTargetReachedAction(@Nullable Runnable action);
}
