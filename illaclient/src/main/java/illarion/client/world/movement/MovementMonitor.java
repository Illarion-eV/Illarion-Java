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

import illarion.client.graphics.AnimatedMove;
import illarion.client.graphics.MoveAnimation;
import illarion.client.util.ConnectionPerformanceClock;

import javax.annotation.Nonnull;

/**
 * This class is used to keep track of current moves and reports back to the main movement handler.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class MovementMonitor implements AnimatedMove {
    @Nonnull
    private final Movement movement;

    @Nonnull
    private final MoveAnimation moveAnimation;

    private boolean reportingDone;

    MovementMonitor(@Nonnull Movement movement, @Nonnull MoveAnimation animation) {
        this.movement = movement;
        moveAnimation = animation;
        reportingDone = false;
    }

    @Override
    public void setPosition(int posX, int posY, int posZ) {
        if (!reportingDone && (moveAnimation.timeRemaining() < ConnectionPerformanceClock.getNetCommPing())) {
            reportingDone = true;
            movement.reportStepFinished();
        }
    }

    @Override
    public void animationStarted() {
        reportingDone = false;
    }

    @Override
    public void animationFinished(boolean finished) {

    }
}
