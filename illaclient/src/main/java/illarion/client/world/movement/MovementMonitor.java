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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This class is used to keep track of current moves and reports back to the main movement handler.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class MovementMonitor implements AnimatedMove {
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(MovementMonitor.class);
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
        long ping = (long) (ConnectionPerformanceClock.getNetCommPing() * 1.5);
        if (!reportingDone && (moveAnimation.timeRemaining() < ping)) {
            LOGGER.info("Reporting ready for next animation. Remaining time for current animation: {}ms",
                        moveAnimation.timeRemaining());
            reportingDone = true;
            movement.reportStepFinished();
        }
    }

    @Override
    public void animationStarted() {
        LOGGER.info("Movement animation started!");
        reportingDone = false;
    }

    @Override
    public void animationFinished(boolean finished) {
        if (!reportingDone) {
            LOGGER.info("Reporting ready for next animation. Old animation ended.");
            reportingDone = true;
            movement.reportStepFinished();
        }
    }
}
