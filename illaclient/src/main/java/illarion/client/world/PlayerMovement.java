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
package illarion.client.world;

import illarion.client.IllaClient;
import illarion.client.graphics.AnimatedMove;
import illarion.client.graphics.MoveAnimation;
import illarion.client.net.client.MoveCmd;
import illarion.client.net.client.TurnCmd;
import illarion.client.util.ConnectionPerformanceClock;
import illarion.client.util.pathfinding.*;
import illarion.client.world.interactive.Usable;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.types.CharacterId;
import illarion.common.types.Location;
import illarion.common.util.FastMath;
import illarion.common.util.Timer;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static illarion.client.util.pathfinding.PathMovementMethod.Run;
import static illarion.client.util.pathfinding.PathMovementMethod.Walk;

/**
 * The player movement class takes and handles all move requests and orders that are needed to move the player
 * character over the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings({"ClassWithTooManyFields", "ClassWithTooManyMethods", "OverlyComplexClass"})
@NotThreadSafe
public final class PlayerMovement implements AnimatedMove {
    /**
     * This value is the relation of the distance from the character location to the location of the cursor to the
     * plain x or y offset. In case the relation is smaller or equal to this the character will move straight
     * horizontal or vertical on the screen. Else it will move diagonal.
     */
    private static final double MOUSE_ANGLE = StrictMath.cos(Math.PI / Location.DIR_MOVE8);

    /**
     * The overlap time of a movement in milliseconds. If this time is left of the last move animation the next step is
     * already requested from the server in oder to get a smooth walking animation.
     */
    private static final int MOVEMENT_OVERLAP_TIME = 200;

    /**
     * The time when the updated position is reported to the rest of the client and the game map is updated regarding
     * this.
     */
    private static final float POSITION_UPDATE_PROCESS = 0.5f;

    /**
     * The time in milliseconds until the requests that are not answered are discarded.
     */
    private static final int TIME_UNTIL_DISCARD = 5000;

    /**
     * The maximal amount of tiles a path can be modified by.
     */
    private static final int MAX_PATH_MODIFICATION = 2;

    /**
     * The amount of modified steps that are allowed to be undone. In case more steps are done without the possibility
     * to fix the location the current location will be assumed to be the correct one.
     */
    private static final int MAX_MODIFIED_STEPS = 4;

    /**
     * The logger instance that takes care of the logging output of this class.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerMovement.class);

    /**
     * The destination location of the automated walking.
     */
    @Nullable
    private Location autoDestination;

    /**
     * The path the player is following by the automated movement.
     */
    @Nullable
    private Path autoPath;

    /**
     * The last move that was allowed by the server but yet not performed.
     */
    private int lastAllowedMove = Location.DIR_ZERO;

    /**
     * The mode of the last move that was allowed by the server.
     */
    @Nonnull
    private CharMovementMode lastAllowedMoveMode = CharMovementMode.None;

    /**
     * The last move that was requested from the server.
     */
    private int lastMoveRequest = Location.DIR_ZERO;

    /**
     * The speed of the last allowed move.
     */
    private int lastMoveSpeed;

    /**
     * The target location of the last move.
     */
    @Nonnull
    private final Location lastMoveTarget = new Location();

    /**
     * The move animation that moves the map around. This animation is also used to fetch the information about the
     * process of the animation so a overlapped move can be requested in case its needed.
     */
    @Nonnull
    private final MoveAnimation moveAnimation = new MoveAnimation(World.getMapDisplay());

    /**
     * {@code True} in case the character is currently moving, {@code false} if not.
     */
    private boolean moving;

    /**
     * The player handler that is controlled by this movement handler.
     */
    @Nonnull
    private final Player parentPlayer;

    /**
     * The variable stores if the location of the character got already updated or not.
     */
    private boolean positionDirty;

    /**
     * The list of direction to store to what directions the player wants to turn to. This is needed to avoid that the
     * client spams the server with turn requests.
     */
    @Nonnull
    private final boolean[] requestedTurns = new boolean[Location.DIR_MOVE8];

    /**
     * This time stores when the requests of moves and turns are discarded in order to allow the player to request more
     * moves and turns. So nothing is going to stand still in case some messages from the server are not received
     * correctly.
     */
    private long timeOfDiscard;

    private boolean walkTowards;

    private boolean mouseMovementActive;

    private int walkTowardsDir = Location.DIR_ZERO;

    @Nonnull
    private CharMovementMode walkTowardsMode = CharMovementMode.None;

    /**
     * The amount of tiles the path was modified by.
     */
    private int runningPathModification;

    /**
     * The last direction the player was running towards.
     */
    private int runningPathLastDirection = -1;

    /**
     * This variable counts the step that were done while the path has a constant modified value.
     */
    private int stepsWithPathModification;

    /**
     * The input component of the game engine.
     */
    @Nonnull
    private final Input input;

    @Nonnull
    private final Timer delayedMoveTrigger;
    private Usable usableAction;
    private boolean isInWalkMode = true;

    private boolean moveToDirectionActive;
    private final boolean[] activeDirections = new boolean[8];
    private boolean mouseFollowAutoRun;

    private final ReadWriteLock autoPathLock = new ReentrantReadWriteLock();

    private boolean allowRunPathModification;

    /* START PATH FINDING */
    /**
     * The active path finding algorithm.
     */
    private final PathFindingAlgorithm pathFinder;
    /* END PATH FINDING */

    /**
     * Default constructor.
     *
     * @param parent the player handler that is controlled by this movement handler
     */
    public PlayerMovement(@Nonnull Input input, @Nonnull Player parent) {
        pathFinder = new MeasureWrapper(new AStar());
        moveAnimation.addTarget(this, false);
        parentPlayer = parent;
        this.input = input;
        AnnotationProcessor.process(this);
        mouseFollowAutoRun = IllaClient.getCfg().getBoolean("mouseFollowAutoRun");
        allowRunPathModification = IllaClient.getCfg().getBoolean("runAutoAvoid");

        delayedMoveTrigger = new Timer(100, new Runnable() {
            @Override
            public void run() {
                if (moveToDirectionActive) {
                    requestMove(getCurrentMoveToDirection(), getMovingToDirectionMode());
                } else {
                    LOGGER.info("Delayed, invalid move trigger received.");
                }
            }
        });
        delayedMoveTrigger.setRepeats(false);
    }

    @EventTopicSubscriber(topic = "mouseFollowAutoRun")
    private void mouseFollowAutoRunChanged(@Nonnull String topic, @Nonnull ConfigChangedEvent configChanged) {
        mouseFollowAutoRun = configChanged.getConfig().getBoolean("mouseFollowAutoRun");
    }

    @EventTopicSubscriber(topic = "runAutoAvoid")
    private void runAutoAvoidChanged(@Nonnull String topic, @Nonnull ConfigChangedEvent configChanged) {
        allowRunPathModification = IllaClient.getCfg().getBoolean("runAutoAvoid");
    }

    private CharMovementMode getMovingToDirectionMode() {
        if (input.isKeyDown(Key.LeftAlt)) {
            return CharMovementMode.None;
        }

        return getCharMovementMode();
    }

    private CharMovementMode getCharMovementMode() {
        return isInWalkMode ? CharMovementMode.Walk : CharMovementMode.Run;
    }

    public void startMovingToDirection(int direction) {
        if ((direction < 0) || (direction >= activeDirections.length)) {
            throw new IllegalArgumentException("Illegal direction: " + direction);
        }
        if (activeDirections[direction]) {
            return;
        }

        activeDirections[direction] = true;

        delayedMoveTrigger.stop();
        if (moveToDirectionActive) {
            requestMove(getCurrentMoveToDirection(), getMovingToDirectionMode());
        } else {
            moveToDirectionActive = true;
            stopWalkTowards();
            delayedMoveTrigger.start();
        }
    }

    public void stopMovingToDirection(int direction) {
        if ((direction < 0) || (direction >= activeDirections.length)) {
            throw new IllegalArgumentException("Illegal direction: " + direction);
        }
        if (!activeDirections[direction]) {
            return;
        }

        if (delayedMoveTrigger.isRunning()) {
            delayedMoveTrigger.stop();
            requestMove(getCurrentMoveToDirection(), getMovingToDirectionMode());
        }
        activeDirections[direction] = false;

        boolean newWalkingState = false;
        for (boolean walkState : activeDirections) {
            if (walkState) {
                newWalkingState = true;
                break;
            }
        }
        moveToDirectionActive = newWalkingState;
    }

    private int getCurrentMoveToDirection() {
        int currentDirection = 0;
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

    /**
     * Request a move of the player character. This function will check if there is already a move or not. In case
     * there
     * is one and depending on how far the old move is processed already, its possible that a new move is requested
     * already earlier so the movement looks all in all smooth.
     *
     * @param direction the direction the move shall be performed in
     * @param mode the mode of the move that shall be performed
     */
    public void requestMove(int direction, @Nonnull CharMovementMode mode) {
        if (direction != Location.DIR_ZERO) {
            requestMove(direction, mode, true, false);
        }
    }

    /**
     * Perform the next step on a automated walking path.
     */
    private void autoStep() {
        Path localAutoPath;
        Usable localUsableAction;
        Location localAutoDestination;
        autoPathLock.readLock().lock();
        try {
            localAutoPath = autoPath;
            localUsableAction = usableAction;
            localAutoDestination = autoDestination;
        } finally {
            autoPathLock.readLock().unlock();
        }

        if (localAutoPath == null) {
            return;
        }

        // get next step
        PathNode node = localAutoPath.nextStep();
        // reached target
        if (node == null) {
            if ((localUsableAction != null) && localUsableAction.isInUseRange()) {
                localUsableAction.use();
            }
            cancelAutoWalk();
            return;
        }

        Location stepDestination = node.getLocation();
        MapTile tile = World.getMap().getMapAt(stepDestination);
        Location loc = parentPlayer.getLocation();

        // check whether path is clear
        int possibleDistance = (node.getMovementMethod() == Run) ? 2 : 1;
        if ((tile == null) || tile.isBlocked() || (loc.getDistance(stepDestination) > possibleDistance)) {
            if (tile == null) {
                LOGGER.info("Recalculating path is required: Tile at {} is null", stepDestination);
            } else if (tile.isBlocked()) {
                LOGGER.info("Recalculating path is required: Tile at {} is blocked", stepDestination);
            } else {
                LOGGER.info("Recalculating path is required: Distance from {} to {} is {}", loc, stepDestination,
                            loc.getDistance(stepDestination));
            }
            // recalculate route if blocked or char is off route
            if (localAutoDestination != null) {
                if (localUsableAction != null) {
                    walkToAndUse(localAutoDestination, localUsableAction);
                } else {
                    walkTo(localAutoDestination);
                }
            }
            return;
        }

        // execute walking step
        int direction = loc.getDirection(stepDestination);
        requestMove(direction, (node.getMovementMethod() == Run) ? CharMovementMode.Run : CharMovementMode.Walk, false,
                    false);
    }

    private void applyPath(@Nullable Path path) {
        if (path == null) {
            cancelAutoWalk();
        } else {
            autoPathLock.writeLock().lock();
            try {
                Usable action = usableAction;
                cancelAutoWalk();
                usableAction = action;
                autoPath = path;
                autoDestination = path.getDestination();
            } finally {
                autoPathLock.writeLock().unlock();
            }
            autoStep();
        }
    }

    /**
     * Stop the automated walking.
     */
    public void cancelAutoWalk() {
        autoPathLock.writeLock().lock();
        try {
            autoPath = null;
            autoDestination = null;
            usableAction = null;
        } finally {
            autoPathLock.writeLock().unlock();
        }
    }

    private void walkTo(@Nonnull Location destination, int approachDistance) {
        Location loc = parentPlayer.getLocation();
        GameMap map = World.getMap();
        if (isInWalkMode) {
            applyPath(pathFinder.findPath(map, loc, destination, approachDistance, Walk));
        } else {
            applyPath(pathFinder.findPath(map, loc, destination, approachDistance, Walk, Run));
        }
    }

    /**
     * Make the character automatically walking to a target location.
     *
     * @param destination the location the character shall walk to
     */
    public void walkTo(@Nonnull Location destination) {
        walkTo(destination, 0);
    }

    /**
     * Make the character automatically walking to a target location.
     *
     * @param destination the location the character shall walk to
     * @param usable the usable the character should use
     */
    public void walkTo(@Nonnull Location destination, @Nonnull Usable usable) {
        walkTo(destination, usable.getUseRange());
    }

    /**
     * Make the character automatically walking to a target location and use the item
     * at the destination.
     *
     * @param destination the location the character shall walk to
     * @param usable the usable the character should use
     */
    public void walkToAndUse(@Nonnull Location destination, @Nonnull Usable usable) {
        walkTo(destination, usable);
        usableAction = usable;
    }

    /**
     * Request a move of the player character. This function will check if there is already a move or not. In case
     * there
     * is one and depending on how far the old move is processed already, its possible that a new move is requested
     * already earlier so the movement looks all in all smooth.
     *
     * @param direction the direction the move shall be performed in
     * @param mode the mode of the move that shall be performed
     * @param stopAutoMove {@code true} in case this request shall remove the currently running automated movement
     * @param runPathModification {@code true} in case the function is supposed to try to change the running path in
     * order to avoid obstacles
     */
    @SuppressWarnings("nls")
    private void requestMove(
            int direction, @Nonnull CharMovementMode mode, boolean stopAutoMove, boolean runPathModification) {
        if (mode == CharMovementMode.Push) {
            throw new IllegalArgumentException("Pushed moves are not supported by the player movement handler.");
        }
        if (stopAutoMove) {
            cancelAutoWalk();
        }
        if (mode == CharMovementMode.None) {
            requestTurn(direction, false);
            discardCheck();
        } else {
            if ((lastMoveRequest == Location.DIR_ZERO) && (lastAllowedMove == Location.DIR_ZERO)) {
                if (!moving || (moveAnimation.timeRemaining() <= getMovementOverlapTime())) {
                    lastMoveRequest = direction;
                    timeOfDiscard = System.currentTimeMillis() + TIME_UNTIL_DISCARD;
                    if ((mode == CharMovementMode.Run) && runPathModification && allowRunPathModification) {
                        performNextRunningStep(direction);
                    } else {
                        resetRunPathModification();
                        sendTurnAndMoveToServer(direction, mode);
                    }
                }
            } else {
                discardCheck();
            }
        }
    }

    /**
     * This function needs to be triggered in case a turn needs to be done.
     *
     * @param direction the direction the player wants his character to look at
     * @param stopAutoMove {@code true} in case this request shall remove the currently running automated movement
     */
    private void requestTurn(int direction, boolean stopAutoMove) {
        if (stopAutoMove) {
            cancelAutoWalk();
        }
        if ((isAnyTrunRequested() || (parentPlayer.getCharacter().getDirection() != direction)) &&
                !requestedTurns[direction]) {
            requestedTurns[direction] = true;
            World.getNet().sendCommand(new TurnCmd(direction));
            timeOfDiscard = System.currentTimeMillis() + TIME_UNTIL_DISCARD;
        } else {
            discardCheck();
        }
    }

    /**
     * Check if any turn is requested but yet not confirmed by the server.
     *
     * @return {@code true} in case there is a turn that is requested from the server
     */
    private boolean isAnyTrunRequested() {
        for (boolean turn : requestedTurns) {
            if (turn) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method plots the next running movement step. It takes obstacles along the way into account and tries to
     * avoid them by slightly changing the direction of the character. Once it altered the path it will try to return
     * to the original path to avoid drifts along the route.
     *
     * @param direction the direction the character is running towards
     */
    private void performNextRunningStep(int direction) {
        // in case the direction changed, reset the path modification data
        if (runningPathLastDirection != direction) {
            runningPathModification = 0;
            stepsWithPathModification = 0;
            runningPathLastDirection = direction;
        }

        // in case it took too long to fix the path, reset everything
        if (stepsWithPathModification > MAX_MODIFIED_STEPS) {
            runningPathModification = 0;
            stepsWithPathModification = 0;
        }

        // try to move in the requested direction
        boolean straightMovePossible = isStepPossible(direction, CharMovementMode.Run);
        if (straightMovePossible && (runningPathModification == 0)) {
            sendTurnAndMoveToServer(direction, CharMovementMode.Run);
            return;
        }

        // calculate the two directions that are possible
        int possibleDir1 = (direction + 1) % Location.DIR_MOVE8;
        int possibleDir2 = (direction == 0) ? 7 : (direction - 1);

        // assign the direction that should be used at preference to undo old path modifications
        int preferredDir;
        int secondDir;
        int preferredMod;
        if (runningPathModification > 0) {
            preferredDir = possibleDir2;
            secondDir = possibleDir1;
            preferredMod = -1;
        } else {
            preferredDir = possibleDir1;
            secondDir = possibleDir2;
            preferredMod = 1;
        }

        // check to undo old path modifications
        if (Math.abs(runningPathModification) == 1) {
            // path is altered by one walking step. Undo it if possible
            if (isStepPossible(preferredDir, CharMovementMode.Walk)) {
                runningPathModification = 0;
                stepsWithPathModification = 0;
                sendTurnAndMoveToServer(preferredDir, CharMovementMode.Walk);
                return;
            }
        } else if (Math.abs(runningPathModification) >= 2) {
            // path is altered by one or more running steps. Undo them if possible.
            if (isStepPossible(preferredDir, CharMovementMode.Run)) {
                runningPathModification += preferredMod * 2;
                stepsWithPathModification = 0;
                sendTurnAndMoveToServer(preferredDir, CharMovementMode.Run);
                return;
            }
        }

        // undoing old changed to the path failed. Try to move along the current modification running.
        if (straightMovePossible && ((stepsWithPathModification + 2) <= MAX_MODIFIED_STEPS)) {
            stepsWithPathModification += 2;
            sendTurnAndMoveToServer(direction, CharMovementMode.Run);
            return;
        }

        // Increase the modification with a running step if allowed
        if ((runningPathModification - (preferredDir * 2)) <= MAX_PATH_MODIFICATION) {
            if (isStepPossible(secondDir, CharMovementMode.Run)) {
                runningPathModification -= preferredMod * 2;
                stepsWithPathModification = 0;
                sendTurnAndMoveToServer(secondDir, CharMovementMode.Run);
                return;
            }
        }

        // Running does not seem to be possible. Try to reduce the path modification with a walking step.
        if (isStepPossible(preferredDir, CharMovementMode.Walk)) {
            runningPathModification += preferredMod;
            stepsWithPathModification = 0;
            sendTurnAndMoveToServer(preferredDir, CharMovementMode.Walk);
            return;
        }

        // Try walking without increasing the modification.
        if (isStepPossible(direction, CharMovementMode.Walk)) {
            stepsWithPathModification += 1;
            sendTurnAndMoveToServer(direction, CharMovementMode.Walk);
            return;
        }

        // Increase the modification with a walking step.
        if ((runningPathModification - preferredDir) <= MAX_PATH_MODIFICATION) {
            if (isStepPossible(secondDir, CharMovementMode.Walk)) {
                runningPathModification -= preferredMod;
                stepsWithPathModification = 0;
                sendTurnAndMoveToServer(secondDir, CharMovementMode.Walk);
                return;
            }
        }

        // all out of options, just run straight ahead and hope for the best. GERONIMO!
        resetRunPathModification();
        sendTurnAndMoveToServer(direction, CharMovementMode.Run);
    }

    /**
     * Check if a move is possible.
     *
     * @param direction the direction of the move
     * @param mode the mode method, only {@link CharMovementMode#Run} and {@link CharMovementMode#Walk} are allowed
     * @return {@code true} in case the step is possible
     * @throws IllegalArgumentException in case the mode has a illegal value
     * @throws IllegalStateException in case something else goes wrong
     */
    public static boolean isStepPossible(int direction, @Nonnull CharMovementMode mode) {
        switch (mode) {
            case None:
            case Push:
                throw new IllegalArgumentException("mode");
            case Walk:
                Location locAfterStep = new Location(World.getPlayer().getLocation(), direction);
                return isLocationFree(locAfterStep);
            case Run:
                Location newLoc = new Location(World.getPlayer().getLocation(), direction);
                if (!isLocationFree(newLoc)) {
                    return false;
                }
                newLoc.moveSC(direction);
                return isLocationFree(newLoc);
        }
        throw new IllegalStateException("isStepPossible reached invalid state.");
    }

    /**
     * Check if its possible to step on a specified location.
     *
     * @param loc the location to test
     * @return {@code true} in case the character is able to step onto the location
     */
    private static boolean isLocationFree(@Nonnull Location loc) {
        MapTile tile = World.getMap().getMapAt(loc);
        return (tile != null) && !tile.isBlocked();
    }

    /**
     * Reset all running path modifications. This should be done in case moves are made without using the
     * {@link #performNextRunningStep(int)} function.
     */
    public void resetRunPathModification() {
        runningPathLastDirection = -1;
    }

    /**
     * Send a turn and a move command to the server,
     *
     * @param direction the direction the character is supposed to move towards
     * @param mode the movement method of the character
     */
    private void sendTurnAndMoveToServer(int direction, @Nonnull CharMovementMode mode) {
        requestTurn(direction, false);
        sendMoveToServer(direction, mode);
    }

    /**
     * Send the movement command to the server.
     *
     * @param direction the direction of the requested move
     * @param mode the mode of the requested move
     */
    private void sendMoveToServer(int direction, @Nonnull CharMovementMode mode) {
        CharacterId playerId = parentPlayer.getPlayerId();
        if (playerId == null) {
            LOGGER.error("Send move to server while ID is not known.");
            return;
        }
        World.getNet().sendCommand(new MoveCmd(playerId, mode, direction));
    }

    /**
     * Check if pending requests need to be discarded in order to keep the stuff working correctly.
     */
    private void discardCheck() {
        if ((timeOfDiscard > 0L) && (timeOfDiscard < System.currentTimeMillis())) {
            for (int dir = 0; dir < Location.DIR_MOVE8; ++dir) {
                requestedTurns[dir] = false;
            }
            lastMoveRequest = Location.DIR_ZERO;
            timeOfDiscard = 0L;
        }
    }

    private long getMovementOverlapTime() {
        return MOVEMENT_OVERLAP_TIME + ConnectionPerformanceClock.getMaxServerPing();
    }

    /**
     * Set current location of the animation. This is useless in this class and this function is only implemented so
     * this class is a valid animation target.
     *
     * @param posX the new x coordinate of the animation target
     * @param posY the new y coordinate of the animation target
     * @param posZ the new z coordinate of the animation target
     */
    @Override
    public void setPosition(int posX, int posY, int posZ) {
        if (positionDirty && (moveAnimation.animationProgress() > POSITION_UPDATE_PROCESS)) {
            positionDirty = false;
        } else if ((moveAnimation.timeRemaining() <= getMovementOverlapTime()) &&
                (lastAllowedMove == Location.DIR_ZERO) && (lastMoveRequest == Location.DIR_ZERO)) {
            if (moveToDirectionActive) {
                requestMove(getCurrentMoveToDirection(), getMovingToDirectionMode());
            } else if (walkTowards) {
                requestMove(walkTowardsDir, walkTowardsMode, true, true);
            } else {
                autoStep();
            }
        }
    }

    /**
     * Perform the move of a player character after the move was confirmed by the player.
     *
     * @param mode the moving method that was send by the server
     * @param target the target location of the character after the move
     * @param speed the speed of the move
     */
    public void acknowledgeMove(
            @Nonnull CharMovementMode mode, @Nonnull Location target, int speed) {
        lastMoveRequest = Location.DIR_ZERO;

        if (!moving) {
            moving = true;
            performMove(mode, target, speed);
            return;
        }

        lastAllowedMove = parentPlayer.getCharacter().getLocation().getDirection(target);
        lastAllowedMoveMode = mode;
        lastMoveTarget.set(target);
        lastMoveSpeed = speed;
    }

    /**
     * Stop the move animation that is currently in progress.
     */
    public void stopAnimation() {
        moveAnimation.stop();
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
        Char playerCharacter = parentPlayer.getCharacter();
        if ((mode == CharMovementMode.None) || playerCharacter.getLocation().equals(target)) {
            parentPlayer.updateLocation(target);
            playerCharacter.setLocation(target);
            animationFinished(true);
            World.getMapDisplay().animationFinished(true);
            walkTowards = false;
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
    public void animationStarted() {
        // nothing to do
    }

    /**
     * This is called in case the animation got finished. This is needed so the next animation can be started correctly
     * in case it was requested.
     *
     * @param finished {@code true} in case the animation is really done, false in case it got canceled
     */
    @Override
    public void animationFinished(boolean finished) {
        positionDirty = true;
        Char playerCharacter = parentPlayer.getCharacter();
        playerCharacter.getLocation().set(parentPlayer.getLocation());
        //playerCharacter.updatePosition(0);
        World.getPeople().checkVisibility();
        World.getMap().checkInside();

        if (lastAllowedMove == Location.DIR_ZERO) {
            moving = false;
            return;
        }

        lastAllowedMove = Location.DIR_ZERO;
        performMove(lastAllowedMoveMode, lastMoveTarget, lastMoveSpeed);
    }

    /**
     * Get the acknowledge of a turn of the player character.
     *
     * @param direction the new direction of the player character after the turn
     */
    public void acknowledgeTurn(int direction) {
        requestedTurns[direction] = false;
        parentPlayer.getCharacter().setDirection(direction);
    }

    /**
     * Check if the character is current<ly moved and turned by mouse control.
     *
     * @return {@code true} in case the character is currently moving by the mouse
     */
    public boolean isMouseMovementActive() {
        return mouseMovementActive;
    }

    /**
     * Get if the player character is currently moving.
     *
     * @return {@code true} in case the character is currently moving
     */
    public boolean isMoving() {
        return moving;
    }

    /**
     * Cleanup the movement handler and prepare everything for a proper shutdown.
     */
    public void shutdown() {
        moveAnimation.removeTarget(this);
        stopWalkTowards();
        cancelAutoWalk();
    }

    /**
     * Stop walking towards the mouse cursor. This stops all movement towards a direction and is releases the mouse
     * movement. Once this is called {@link #isMouseMovementActive()} will return {@code false}.
     */
    public void stopWalkTowards() {
        walkTowards = false;
        mouseMovementActive = false;
    }

    /**
     * Walk towards a direction.
     *
     * @param screenPosX the x location on the screen to walk to
     * @param screenPosY the y location on the screen to walk to
     */
    public void walkTowards(int screenPosX, int screenPosY) {
        MapDimensions mapDimensions = MapDimensions.getInstance();
        int xOffset = screenPosX - (mapDimensions.getOnScreenWidth() / 2);
        int yOffset = -(screenPosY - (mapDimensions.getOnScreenHeight() / 2));
        int distance = FastMath.sqrt((xOffset * xOffset) + (yOffset * yOffset));

        if (distance <= 5) {
            return;
        }

        float relXOffset = (float) xOffset / distance;
        float relYOffset = (float) yOffset / distance;

        //noinspection IfStatementWithTooManyBranches
        walkTowardsMode = getWalkTowardsMode(distance);

        //noinspection IfStatementWithTooManyBranches
        if (relXOffset > MOUSE_ANGLE) {
            walkTowardsDir = Location.DIR_SOUTHEAST;
        } else if (relXOffset < -MOUSE_ANGLE) {
            walkTowardsDir = Location.DIR_NORTHWEST;
        } else if (relYOffset > MOUSE_ANGLE) {
            walkTowardsDir = Location.DIR_NORTHEAST;
        } else if (relYOffset < -MOUSE_ANGLE) {
            walkTowardsDir = Location.DIR_SOUTHWEST;
        } else if ((xOffset > 0) && (yOffset > 0)) {
            walkTowardsDir = Location.DIR_EAST;
        } else if ((xOffset > 0) && (yOffset < 0)) {
            walkTowardsDir = Location.DIR_SOUTH;
        } else if ((xOffset < 0) && (yOffset < 0)) {
            walkTowardsDir = Location.DIR_WEST;
        } else if ((xOffset < 0) && (yOffset > 0)) {
            walkTowardsDir = Location.DIR_NORTH;
        }

        mouseMovementActive = true;

        if (walkTowardsMode == CharMovementMode.None) {
            walkTowards = false;
            requestTurn(walkTowardsDir, true);
            return;
        }

        if (walkTowards) {
            return;
        }

        walkTowards = true;

        requestMove(walkTowardsDir, walkTowardsMode, true, true);
    }

    private CharMovementMode getWalkTowardsMode(int distance) {
        if (input.isAnyKeyDown(Key.LeftShift, Key.RightShift)) {
            return CharMovementMode.None;
        }

        if (mouseFollowAutoRun) {
            return getCharMovementMode();
        }

        CharMovementMode mode = CharMovementMode.Walk;
        if (distance > 200) {
            mode = CharMovementMode.Run;
        } else if (distance < 30) {
            mode = CharMovementMode.None;
        }
        return mode;
    }

    public void toggleRunWalk() {
        isInWalkMode = !isInWalkMode;
    }

    public boolean isInWalkMode() {
        return isInWalkMode;
    }
}
