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
import illarion.client.world.Char;
import illarion.client.world.CharMovementMode;
import illarion.client.world.Player;
import illarion.client.world.World;
import illarion.common.types.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This class takes care for the actual execution of the movement commands. It processes the server responses and
 * triggers the animations as required to perform the moves.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MovementExecutor implements AnimatedMove {
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(MovementExecutor.class);
    @Nonnull
    private final MoveAnimation moveAnimation;

    @Nonnull
    private final Movement movement;

    private CharMovementMode nextMovementMode;
    private Location nextTarget;
    private int nextSpeed;

    MovementExecutor(@Nonnull Movement movement, @Nonnull MoveAnimation animation) {
        this.movement = movement;
        moveAnimation = animation;
        moveAnimation.addTarget(this, false);
    }

    /**
     * This function has to be called by the network communication interface once a move command is received from the
     * server. This function will trigger the required animation.
     *
     * @param mode the movement mode
     * @param target the target location where the character is supposed to be located after the move
     * @param speed the animation speed
     */
    public void handleMoveServerResponse(@Nonnull CharMovementMode mode, @Nonnull Location target, int speed) {
        if (moveAnimation.isRunning()) {
            LOGGER.info("Received new step from server! Stored for later execution, due active animation.");
            nextTarget = target;
            nextSpeed = speed;
            nextMovementMode = mode;
            if (!moveAnimation.isRunning()) {
                animationFinished(true);
            }
        } else {
            LOGGER.info("Received new step from server! Executing right now.");
            performMove(mode, target, speed);
        }
    }

    /**
     * Process a turn command that is received from the server.
     *
     * @param direction the new direction after the turn
     */
    public void handleTurnServerResponse(int direction) {
        if (!Location.isValidDirection(direction)) {
            throw new IllegalArgumentException("Direction has a illegal value: " + direction);
        }
        movement.getPlayer().getCharacter().setDirection(direction);
    }

    /**
     * Perform a move of the player character. This function does not perform any checks. It just does the move no
     * matter what the status of everything is.
     *
     * @param mode the movement mode
     * @param target the target location where the character shall be located at at the end of the move
     * @param speed the speed of the walk that determines how long the animation takes
     */
    private void performMove(@Nonnull CharMovementMode mode, @Nonnull Location target, int speed) {
        Player parentPlayer = movement.getPlayer();
        Char playerCharacter = parentPlayer.getCharacter();
        if ((mode == CharMovementMode.None) || playerCharacter.getLocation().equals(target)) {
            parentPlayer.updateLocation(target);
            playerCharacter.setLocation(target);
            World.getMapDisplay().animationFinished(true);
            animationFinished(true);
            movement.reportStepFinished();
            return;
        }

        playerCharacter.moveTo(target, mode, speed);
        int oldElevation = World.getMapDisplay().getElevation();
        int newElevation = World.getMap().getElevationAt(target);
        int xOffset = parentPlayer.getLocation().getDcX() - target.getDcX();
        int yOffset = parentPlayer.getLocation().getDcY() - target.getDcY();
        moveAnimation.start(0, 0, -oldElevation, xOffset, yOffset, -newElevation, speed);

        parentPlayer.updateLocation(target);
    }

    @Override
    public void setPosition(int posX, int posY, int posZ) {
    }

    @Override
    public void animationStarted() {
    }

    @Override
    public void animationFinished(boolean finished) {
        CharMovementMode localMode = nextMovementMode;
        nextMovementMode = null;
        if (localMode != null) {
            performMove(localMode, nextTarget, nextSpeed);
        }
    }
}
