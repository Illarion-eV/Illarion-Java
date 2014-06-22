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
import illarion.common.util.Timer;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This keyboard movement handler simply handles the keyboard input to perform the walking operations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SimpleKeyboardMovementHandler extends AbstractMovementHandler implements KeyboardMovementHandler {
    @Nonnull
    private final Input input;

    private final boolean[] activeDirections;

    @Nonnull
    private final Timer delayedMoveTrigger;
    private boolean delayedMove;

    SimpleKeyboardMovementHandler(@Nonnull Movement movement, @Nonnull Input input) {
        super(movement);
        activeDirections = new boolean[Location.DIR_MOVE8];
        this.input = input;
        delayedMoveTrigger = new Timer(100, new Runnable() {
            @Override
            public void run() {
                delayedMove = false;
                getMovement().update();
            }
        });
        delayedMoveTrigger.setRepeats(false);
    }

    @Override
    public void startMovingTowards(int direction) {
        if (!Location.isValidDirection(direction)) {
            throw new IllegalArgumentException("Direction contains invalid value: " + direction);
        }
        if (!activeDirections[direction]) {
            activeDirections[direction] = true;

            delayedMoveTrigger.stop();
            if (!getMovement().isMoving()) {
                delayedMoveTrigger.start();
                delayedMove = true;
            }
        }
    }

    @Override
    public void stopMovingTowards(int direction) {
        if (!Location.isValidDirection(direction)) {
            throw new IllegalArgumentException("Direction contains invalid value: " + direction);
        }
        activeDirections[direction] = false;
    }

    @Nullable
    @Override
    public StepData getNextStep(@Nonnull Location currentLocation) {
        int dir = getMovementDirection();
        if (dir == Location.DIR_ZERO) {
            return null;
        }
        return new DefaultStepData(getMovementMode(), dir);
    }

    private int getMovementDirection() {
        if (delayedMove) {
            return Location.DIR_ZERO;
        }

        int currentDirection = Location.DIR_ZERO;
        boolean noDirectionSet = true;
        boolean secondDirectionSet = false;

        for (int i = 0; i < activeDirections.length; i++) {
            if (activeDirections[i]) {
                if (noDirectionSet) {
                    noDirectionSet = false;
                    currentDirection = i;
                } else if (secondDirectionSet) {
                    return Location.DIR_ZERO;
                } else {
                    secondDirectionSet = true;
                    if ((i - currentDirection) < 4) {
                        currentDirection += (i - currentDirection) / 2;
                    } else {
                        currentDirection += ((i + 8) - currentDirection) / 2;
                    }
                    currentDirection %= 8;
                }
            }
        }
        return currentDirection;
    }

    private CharMovementMode getMovementMode() {
        if (input.isKeyDown(Key.LeftAlt)) {
            return CharMovementMode.None;
        }

        return getMovement().getDefaultMovementMode();
    }

    @Override
    public String toString() {
        return "Simple keyboard movement handler";
    }
}
