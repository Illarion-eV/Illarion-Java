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
import illarion.common.types.Location;

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class DefaultStepData implements StepData {
    @Nonnull
    private final CharMovementMode mode;
    private final int direction;

    DefaultStepData(@Nonnull CharMovementMode mode, int direction) {
        if ((direction < 0) || (direction >= Location.DIR_MOVE8)) {
            throw new IllegalArgumentException("Direction is out of range: " + direction);
        }
        this.mode = mode;
        this.direction = direction;
    }

    @Nonnull
    @Override
    public CharMovementMode getMovementMode() {
        return mode;
    }

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return mode.name() + " Direction: " + direction;
    }
}
