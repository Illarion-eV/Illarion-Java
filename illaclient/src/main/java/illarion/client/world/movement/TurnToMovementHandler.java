/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2017 - Illarion e.V.
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
import illarion.common.types.ServerCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/**
 * A movement handler that turns to the target instead of moving.
 *
 * @author Ilya Osadchiy
 */
public class TurnToMovementHandler extends AbstractMovementHandler implements TargetTurnHandler {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(TurnToMovementHandler.class);
    @Nonnull
    private static final Marker marker = MarkerFactory.getMarker("Movement");

    @Nullable
    private ServerCoordinate targetLocation;

    TurnToMovementHandler(@Nonnull Movement movement) {
        super(movement);
    }

    @Override
    public void turnTo(@Nonnull ServerCoordinate target) {
        targetLocation = target;
    }

    @Override
    public void disengage(boolean transferAllowed) {
        super.disengage(transferAllowed);
        targetLocation = null;
    }

    @Nullable
    @Override
    public StepData getNextStep(@Nonnull ServerCoordinate currentLocation) {
        if (targetLocation == null) {
            return null;
        }
        log.debug(marker, "Performing turn to {}", targetLocation);
        Direction direction = currentLocation.getDirection(targetLocation);
        targetLocation = null;
        return new DefaultStepData(CharMovementMode.None, direction);
    }

    @Nonnull
    @Override
    public String toString() {
        return "Turn to target movement handler";
    }

}
