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
import java.beans.ConstructorProperties;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class DefaultStepData implements StepData {
    @Nonnull
    private final CharMovementMode movementMode;
    @Nullable
    private final Direction direction;

    @ConstructorProperties({"movementMode", "direction"})
    public DefaultStepData(@Nonnull CharMovementMode movementMode, @Nullable Direction direction) {
        this.movementMode = movementMode;
        this.direction = direction;
    }

    @Override
    @Nonnull
    public CharMovementMode getMovementMode() {
        return movementMode;
    }

    @Override
    @Nullable
    public Direction getDirection() {
        return direction;
    }

    @Override
    @Nonnull
    public String toString() {
        return "DefaultStepData" + movementMode.name() + " Direction: " +
                ((direction != null) ? direction.name() : "none");
    }
}
