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

import illarion.client.world.CharMovementMode;
import illarion.common.types.Direction;
import org.jetbrains.annotations.Contract;

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
    @Contract(pure = true)
    CharMovementMode getMovementMode();

    /**
     * Get the direction of the move.
     *
     * @return the move direction
     */
    @Nullable
    @Contract(pure = true)
    Direction getDirection();

    /**
     * Get the action that is executed once the data for this step is send to the server.
     *
     * @return the action for after this step
     */
    @Nullable
    @Contract(pure = true)
    Runnable getPostStepAction();
}
