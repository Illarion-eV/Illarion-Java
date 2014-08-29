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
class MovingTask implements MoveAnimatorTask {
    @Nonnull
    private final MoveAnimator moveAnimator;
    @Nonnull
    private final CharMovementMode mode;
    @Nonnull
    private final Location target;
    private final int duration;
    private boolean executed;

    MovingTask(
            @Nonnull MoveAnimator moveAnimator,
            @Nonnull CharMovementMode mode,
            @Nonnull Location target,
            int duration) {
        this.moveAnimator = moveAnimator;
        this.mode = mode;
        this.target = target;
        this.duration = duration;
        executed = false;
    }

    public boolean isSetupCorrectly(@Nonnull CharMovementMode mode, @Nonnull Location target, int duration) {
        return (this.mode == mode) && this.target.equals(target) && (this.duration == duration);
    }

    @Override
    public void execute() {
        executed = true;
        moveAnimator.executeMove(mode, target, duration);
    }

    public boolean isExecuted() {
        return executed;
    }

    @Override
    public String toString() {
        return "Move Task - Mode: " + mode + " Target " + target + " Duration: " + duration + "ms";
    }
}
