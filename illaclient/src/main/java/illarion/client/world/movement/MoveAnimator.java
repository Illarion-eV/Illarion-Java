/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import illarion.client.util.UpdateTaskManager;
import illarion.client.world.Char;
import illarion.client.world.CharMovementMode;
import illarion.client.world.Player;
import illarion.client.world.World;
import illarion.common.graphics.Layer;
import illarion.common.types.Direction;
import illarion.common.types.DisplayCoordinate;
import illarion.common.types.ServerCoordinate;
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
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(MoveAnimator.class);
    @Nonnull
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

    private void scheduleMove(@Nonnull CharMovementMode mode, @Nonnull ServerCoordinate target, int duration) {
        scheduleTask(new MovingTask(this, mode, target, duration));
    }

    private void scheduleTask(@Nonnull MoveAnimatorTask task) {
        taskQueue.offer(task);
        if (!animationInProgress) {
            World.getUpdateTaskManager().addTaskForLater((container, delta) -> {
                if (!animationInProgress) {
                    executeNext();
                }
            });
        }
    }

    void scheduleEarlyMove(@Nonnull CharMovementMode mode, @Nonnull ServerCoordinate target, int duration) {
        if (uncomfirmedMoveTask != null) {
            log.warn(marker, "Scheduling another early move is not possible as there is already one set.");
        } else {
            log.debug(marker, "Scheduling a early move. FrameAnimationMode: {}, Target: {}, Duration: {}ms", mode,
                      target, duration);
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
    void cancelMove(@Nonnull ServerCoordinate allowedTarget) {
        Player parentPlayer = movement.getPlayer();

        MovingTask task = uncomfirmedMoveTask;
        UpdateTaskManager utm = World.getUpdateTaskManager();
        if (task == null) {
            log.debug(marker, "Received cancel move, but there is no unconfirmed move. Settings location to {}",
                    allowedTarget);
            utm.addTaskForLater((container, delta) -> {
                log.debug(marker, "Setting player location to {} now.", allowedTarget);
                parentPlayer.setLocation(allowedTarget);
            });
        } else {
            taskQueue.clear();
            if (task.isExecuted()) {
                uncomfirmedMoveTask = null;
                confirmedMoveTask = null;
                if (moveAnimation.isRunning()) {
                    log.debug(marker, "Received cancel move, move was already in progress. Resetting");
                    utm.addTaskForLater((container, delta) -> {
                        log.debug(marker, "Resetting location to {} for cancel.", allowedTarget);
                        parentPlayer.setLocation(allowedTarget);
                        parentPlayer.getCharacter().resetAnimation(true);
                        moveAnimation.stop();
                    });
                } else {
                    log.debug(marker, "Move seems to be done already.");
                    utm.addTaskForLater((container, delta) -> parentPlayer.setLocation(allowedTarget));
                }
            } else {
                log.debug(marker, "Move did not start yet. We are good.");
            }
        }
        movement.reportReadyForNextStep();
    }

    /**
     * Confirm a move with the specified parameters.
     *
     * @param mode the move
     * @param target the target of the move
     * @param duration the duration of the move
     */
    void confirmMove(@Nonnull CharMovementMode mode, @Nonnull ServerCoordinate target, int duration) {
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
                        if (moveAnimation.getDuration() == duration) {
                            log.debug(marker, "Already running animation with {}ms to {} is correct", duration, target);
                        } else {
                            if (log.isWarnEnabled()) {
                                log.warn(marker,
                                         "Move to the correct place is in progress. Fixing time from {}ms to {}ms",
                                         moveAnimation.getDuration(), duration);
                            }
                            /* The timing is off. Lets fix that. */
                            moveAnimation.setDuration(duration);
                            parentPlayer.getCharacter().updateMoveDuration(duration);
                        }
                    } else {
                        log.warn(marker, "Move to the wrong location. Resetting. Expected location: {} Player " +
                                "location: {}", target, parentPlayer.getLocation());
                        /* Crap! We are moving to the wrong place... */
                        movement.executeServerLocation(target);
                        movement.reportReadyForNextStep();
                    }
                } else {
                    log.debug(marker, "The unconfirmed move seems to be done already.");
                    movement.reportReadyForNextStep();
                }
            } else {
                if (task.isSetupCorrectly(mode, target, duration)) {
                    log.debug(marker, "Move is correctly scheduled.");
                    confirmedMoveTask = task;
                } else {
                    confirmedMoveTask = new MovingTask(this, mode, target, duration);
                    log.warn(marker, "Move is not correctly scheduled. Scheduled: {}, New: {}", task,
                             confirmedMoveTask);
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
        log.debug("Executing turn to {} now.", direction);
        movement.getPlayer().getCharacter().setDirection(direction);
        executeNext();
    }

    void executeMove(@Nonnull CharMovementMode mode, @Nonnull ServerCoordinate target, int duration) {
        log.debug("Executing move (FrameAnimationMode: {}) to {} (Duration: {}ms) now.", mode, target, duration);
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
        DisplayCoordinate startDC = getDisplayCoordinateAt(parentPlayer.getLocation());
        DisplayCoordinate targetDC = getDisplayCoordinateAt(target);

        playerCharacter.moveTo(target, mode, duration);
        moveAnimation.start(startDC, targetDC, duration);
        parentPlayer.updateLocation(target);
    }

    @Nonnull
    private DisplayCoordinate getDisplayCoordinateAt(@Nonnull ServerCoordinate coordinate) {
        int elevation = World.getMap().getElevationAt(coordinate);
        int x = coordinate.toDisplayX();
        int y = coordinate.toDisplayY() - elevation;
        int layer = coordinate.toDisplayLayer(Layer.Chars);

        return new DisplayCoordinate(x, y, layer);
    }

    private boolean executeNext() {
        @Nullable MovingTask unconfirmedTask = uncomfirmedMoveTask;
        if ((unconfirmedTask != null) && unconfirmedTask.isExecuted()) {
            log.debug("Stopping move execution because a unconfirmed move finished executing.");
            /* Found a executed but not yet confirmed move. Hold everything right here and wait for the confirmation. */
            animationInProgress = false;
            return false;
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
            log.debug(marker, "Movement animator is executing next task: {}", task);
            task.execute();
            return true;
        } else {
            animationInProgress = false;
            return false;
        }
    }

    @Override
    public void setPosition(@Nonnull DisplayCoordinate position) {
        if (isReportingRequired()) {
            int remaining = moveAnimation.timeRemaining();
            if (remaining < 20) {
                log.debug(marker, "Requesting next move {}ms before the animation finishes.", remaining);
                reportingDone = true;
                movement.reportReadyForNextStep();
            }
        }
    }

    private boolean isReportingRequired() {
        return !reportingDone && (uncomfirmedMoveTask == null);
    }

    @Override
    public void animationStarted() {

    }

    @Override
    public void animationFinished(boolean finished) {
        if (isReportingRequired()) {
            log.debug(marker, "Requesting next move at the end of the animation.");
            reportingDone = true;
            if (finished) {
                movement.reportReadyForNextStep();
            }
        }
        World.getMapDisplay().setLocation(getDisplayCoordinateAt(movement.getPlayer().getLocation()));
        executeNext();
    }
}
