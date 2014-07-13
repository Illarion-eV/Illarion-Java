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
package illarion.client.world.movement;

import illarion.client.world.CharMovementMode;
import illarion.common.types.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This interface is used to exchange information regarding a single step between the different handlers of the
 * movement system.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
interface StepData {
    /**
     * Get the movement method.
     *
     * @return the movement method
     */
    @Nonnull
    CharMovementMode getMovementMode();

    /**
     * Get the direction of the move.
     *
     * @return the move direction
     */
    @Nullable
    Direction getDirection();
}
