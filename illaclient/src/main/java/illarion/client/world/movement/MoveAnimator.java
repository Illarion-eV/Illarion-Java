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
import illarion.common.types.Direction;
import illarion.common.types.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * This class takes care for everything regarding the animation of the moves. It triggers and monitors the required
 * animations and reports back the different states of the animation.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class MoveAnimator implements AnimatedMove {
    private static final Logger log = LoggerFactory.getLogger(MoveAnimator.class);
    private static final Marker marker = MarkerFactory.getMarker("Movement");

    @Nonnull
    private final Movement movement;

    @Nonnull
    private final MoveAnimation moveAnimation;

    @Nonnull
    private final Queue<MoveAnimatorTask> taskQueue = new LinkedList<>();

    private boolean animationInProgress;
    private boolean reportingDone;

    @Nullable
    private Direction lastRequestedTurn;

    /**
     * Schedule a move task that is not yet confirmed.
     */
    @Nullable
    private MovingTask uncomfirmedMoveTask;
    @Nullable
    private MovingTask confirmedMoveTask;



    public MoveAnimator(@Nonnull Movement movement, @Nonnull MoveAnimation moveAnimation) {
        this.movement = movement;
        this.moveAnimation = moveAnimation;
    }

    private void scheduleMove(@Nonnull CharMovementMode mode, @Nonnull Location target, int duration) {
        scheduleTask(new MovingTask(this, mode, target, duration));
    }

    private void scheduleTask(@Nonnull MoveAnimatorTask task) {
        taskQueue.offer(task);
        if (!animationInProgress) {
            executeNext();
        }
    }

    void scheduleEarlyMove(@Nonnull CharMovementMode mode, @Nonnull Location target, int duration) {
        if (uncomfirmedMoveTask != null) {
            log.warn(marker, "Scheduling another early move is not possible as there is already one set.");
        } else {
            log.debug(marker, "Scheduling a early move.");
            MovingTask task = new MovingTask(this, mode, target, duration);
            uncomfirmedMoveTask = task;
            scheduleTask(task);
        }
    }

    /**
     * Cancel a currently executed move in case there is any.
     * <br />
     * In case the currently executed move targets location that is reported, the move is allowed to continue.
     *
     * @param allowedTarget allowed target location
     */
    void cancelMove(@Nonnull Location allowedTarget) {
        taskQueue.clear();

        Player parentPlayer = movement.getPlayer();
        if (!parentPlayer.getLocation().equals(allowedTarget)) {
            moveAnimation.stop();
            parentPlayer.setLocation(allowedTarget);
        }
        if (!reportingDone) {
            reportingDone = true;
            movement.reportReadyForNextStep();
        }
    }

    /**
     * Confirm a move with the specified parameters.
     *
     * @param mode the move
     * @param target the target of the move
     * @param duration the duration of the move
     */
    void confirmMove(@Nonnull CharMovementMode mode, @Nonnull Location target, int duration) {
        MovingTask task = uncomfirmedMoveTask;
        if (task == null) {
            log.debug(marker, "No unconfirmed move found. Schedule the move.");
            confirmedMoveTask = null;
            scheduleMove(mode, target, duration);
        } else {
            if (task.isExecuted()) {
                uncomfirmedMoveTask = null;
                confirmedMoveTask = null;
                if (moveAnimation.isRunning()) {
                    /* We have a active move. Lets check it out. */
                    Player parentPlayer = movement.getPlayer();
                    if (parentPlayer.getLocation().equals(target)) {
                        /* Okay we are moving to the right place. Lets check if the timing fits. */
                        if (moveAnimation.getDuration() != duration) {
                            if (log.isDebugEnabled()) {
                                log.debug(marker,
                                          "Move to the correct place is in progress. Fixing time from {}ms to {}ms",
                                          moveAnimation.getDuration(), duration);
                            }
                            /* The timing is off. Lets fix that. */
                            moveAnimation.setDuration(duration);
                            parentPlayer.getCharacter().updateMoveDuration(duration);
                        } else {
                            log.debug(marker, "Already running animation with {}ms is correct", duration);
                        }
                    } else {
                        log.debug(marker, "Move to the wrong location. Resetting.");
                        /* Crap! We are moving to the wrong place... */
                        movement.executeServerLocation(target);
                    }
                } else {
                    log.debug(marker, "The unconfirmed move seems to be done already.");
                    /* Nothing moving here. Lets start the action. */
                    scheduleMove(mode, target, duration);
                }
            } else {
                if (task.isSetupCorrectly(mode, target, duration)) {
                    log.debug(marker, "Move is correctly scheduled.");
                    confirmedMoveTask = task;
                } else {
                    log.debug(marker, "Move is not correctly scheduled.");
                    confirmedMoveTask = new MovingTask(this, mode, target, duration);
                }
            }
        }
    }

    void scheduleTurn(@Nonnull Direction direction) {
        if (lastRequestedTurn != direction) {
            lastRequestedTurn = direction;
            scheduleTask(new TurningTask(this, direction));
        }
    }

    void cancelAll() {
        taskQueue.clear();
        moveAnimation.stop();
        lastRequestedTurn = null;
    }

    void executeTurn(@Nonnull Direction direction) {
        movement.getPlayer().getCharacter().setDirection(direction);
        executeNext();
    }

    void executeMove(@Nonnull CharMovementMode mode, @Nonnull Location target, int duration) {
        Player parentPlayer = movement.getPlayer();
        Char playerCharacter = parentPlayer.getCharacter();
        if ((mode == CharMovementMode.None) || playerCharacter.getLocation().equals(target)) {
            parentPlayer.updateLocation(target);
            playerCharacter.setLocation(target);
            World.getMapDisplay().animationFinished(true);
            movement.reportReadyForNextStep();
            executeNext();
            return;
        }

        reportingDone = false;
        playerCharacter.moveTo(target, mode, duration);
        int oldElevation = World.getMapDisplay().getElevation();
        int newElevation = World.getMap().getElevationAt(target);
        int xOffset = parentPlayer.getLocation().getDcX() - target.getDcX();
        int yOffset = parentPlayer.getLocation().getDcY() - target.getDcY();
        moveAnimation.start(0, 0, -oldElevation, xOffset, yOffset, -newElevation, duration);

        parentPlayer.updateLocation(target);
    }

    private void executeNext() {
        @Nullable MovingTask unconfirmedTask = uncomfirmedMoveTask;
        if ((unconfirmedTask != null) && unconfirmedTask.isExecuted()) {
            uncomfirmedMoveTask = null;
            unconfirmedTask = null;
        }
        MoveAnimatorTask task = taskQueue.poll();
        if (task != null) {
            if (Objects.equals(task, unconfirmedTask)) {
                MoveAnimatorTask confirmedTask = confirmedMoveTask;
                if (confirmedTask != null) {
                    log.debug("Current move is a unconfirmed move. Using the confirmed version.");
                    confirmedMoveTask = null;
                    uncomfirmedMoveTask = null;
                    // overwrite used task
                    //noinspection ReuseOfLocalVariable
                    task = confirmedTask;
                } else {
                    log.debug("Current move is a unconfirmed move. No confirmed version present.");
                }
            }
            animationInProgress = true;
            task.execute();
        } else {
            animationInProgress = false;
        }
    }

    @Override
    public void setPosition(int posX, int posY, int posZ) {
        if (!reportingDone) {
            int remaining = moveAnimation.timeRemaining();
            if (remaining < 20) {
                log.debug(marker, "Requesting next move {}ms before the animation finishes.", remaining);
                reportingDone = true;
                movement.reportReadyForNextStep();
            }
        }
    }

    @Override
    public void animationStarted() {

    }

    @Override
    public void animationFinished(boolean finished) {
        if (!reportingDone) {
            reportingDone = true;
            movement.reportReadyForNextStep();
        }
        executeNext();
    }
}
