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
import illarion.client.world.World;
import illarion.common.types.Direction;
import illarion.common.types.Location;
import illarion.common.util.FastMath;
import illarion.common.util.Timer;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

/**
 * This keyboard movement handler simply handles the keyboard input to perform the walking operations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SimpleKeyboardMovementHandler extends AbstractMovementHandler implements KeyboardMovementHandler {
    @Nonnull
    private final Input input;

    private final Set<Direction> activeDirections;

    @Nonnull
    private final Timer delayedMoveTrigger;
    private boolean delayedMove;

    SimpleKeyboardMovementHandler(@Nonnull Movement movement, @Nonnull Input input) {
        super(movement);
        activeDirections = EnumSet.noneOf(Direction.class);
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
    public void startMovingTowards(@Nonnull Direction direction) {
        if (activeDirections.add(direction)) {
            delayedMoveTrigger.stop();
            if (!getMovement().isMoving()) {
                delayedMoveTrigger.start();
                delayedMove = true;
            }
        }
    }

    @Override
    public void stopMovingTowards(@Nonnull Direction direction) {
        activeDirections.remove(direction);
    }

    @Nullable
    @Override
    public StepData getNextStep(@Nonnull Location currentLocation) {
        return new DefaultStepData(getMovementMode(), getMovementDirection());
    }

    @Nullable
    private Direction getMovementDirection() {
        if (delayedMove) {
            return null;
        }

        if (activeDirections.isEmpty() || (activeDirections.size() > 2)) {
            return null;
        }

        return getCombined(activeDirections);
    }

    @Nullable
    public static Direction getCombined(@Nonnull Iterable<Direction> activeDirections) {
        int xVec = 0;
        int yVec = 0;
        for (Direction activeDir : activeDirections) {
            xVec += activeDir.getDirectionVectorX();
            yVec += activeDir.getDirectionVectorY();
        }
        xVec = FastMath.sign(xVec);
        yVec = FastMath.sign(yVec);

        for (Direction dir : Direction.values()) {
            if ((dir.getDirectionVectorX() == xVec) && (dir.getDirectionVectorY() == yVec)) {
                return dir;
            }
        }
        return null;
    }

    private CharMovementMode getMovementMode() {
        if (input.isKeyDown(Key.LeftAlt)) {
            return CharMovementMode.None;
        }

        if (!World.getPlayer().getCarryLoad().isRunningPossible()) {
            return CharMovementMode.Walk;
        }

        return getMovement().getDefaultMovementMode();
    }

    @Override
    public String toString() {
        return "Simple keyboard movement handler";
    }
}
