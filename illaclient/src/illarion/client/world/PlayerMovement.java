/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world;

import illarion.client.IllaClient;
import illarion.client.graphics.AnimatedMove;
import illarion.client.graphics.MoveAnimation;
import illarion.client.net.client.MoveCmd;
import illarion.client.net.client.TurnCmd;
import illarion.client.util.Path;
import illarion.client.util.PathNode;
import illarion.client.util.PathReceiver;
import illarion.client.util.Pathfinder;
import illarion.common.types.CharacterId;
import illarion.common.types.Location;
import illarion.common.util.FastMath;
import illarion.common.util.Timer;
import org.apache.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * The player movement class takes and handles all move requests and orders that are needed to move the player
 * character over the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings({"ClassWithTooManyFields", "ClassWithTooManyMethods", "OverlyComplexClass"})
@NotThreadSafe
public final class PlayerMovement implements AnimatedMove, PathReceiver {
    /**
     * This value is the relation of the distance from the character location to the location of the cursor to the
     * plain x or y offset. In case the relation is smaller or equal to this the character will move straight
     * horizontal or vertical on the screen. Else it will move diagonal.
     */
    private static final double MOUSE_ANGLE = Math.cos(Math.PI / Location.DIR_MOVE8);

    /**
     * The overlap time of a movement in milliseconds. If this time is left of the last move animation the next step is
     * already requested from the server in oder to get a smooth walking animation.
     */
    private static final int MOVEMENT_OVERLAP_TIME = 250;

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
    private static final Logger LOGGER = Logger.getLogger(PlayerMovement.class);

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


    /**
     * Default constructor.
     *
     * @param parent the player handler that is controlled by this movement handler
     */
    public PlayerMovement(@Nonnull final Input input, @Nonnull final Player parent) {
        moveAnimation.addTarget(this, false);
        parentPlayer = parent;
        this.input = input;
        AnnotationProcessor.process(this);

        delayedMoveTrigger = new Timer(100, new Runnable() {
            @Override
            public void run() {
                if (moveToDirectionActive) {
                    requestMove(getCurrentMoveToDirection(), moveToDirectionMode);
                } else {
                    LOGGER.info("Delayed, invalid move trigger received.");
                }
            }
        });
        delayedMoveTrigger.setRepeats(false);
    }

    private boolean moveToDirectionActive;
    private final boolean[] activeDirections = new boolean[8];
    private CharMovementMode moveToDirectionMode = CharMovementMode.Walk;

    public void setMovingToDirectionMode(final CharMovementMode mode) {
        moveToDirectionMode = mode;
    }

    public void startMovingToDirection(final int direction) {
        if (activeDirections[direction]) {
            return;
        }

        activeDirections[direction] = true;

        delayedMoveTrigger.stop();
        if (moveToDirectionActive) {
            requestMove(getCurrentMoveToDirection(), moveToDirectionMode);
        } else {
            moveToDirectionActive = true;
            stopWalkTowards();
            delayedMoveTrigger.start();
        }
    }

    public void stopMovingToDirection(final int direction) {
        if (!activeDirections[direction]) {
            return;
        }

        if (delayedMoveTrigger.isRunning()) {
            delayedMoveTrigger.stop();
            requestMove(getCurrentMoveToDirection(), moveToDirectionMode);
        }
        activeDirections[direction] = false;

        boolean newWalkingState = false;
        for (final boolean walkState : activeDirections) {
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
     * @param mode      the mode of the move that shall be performed
     */
    public void requestMove(final int direction, @Nonnull final CharMovementMode mode) {
        requestMove(direction, mode, true, false);
    }

    @Override
    public void handlePath(@Nonnull final Path path) {
        cancelAutoWalk();
        autoPath = path;
        autoDestination = autoPath.getDestination();
        autoPath.nextStep();
        autoStep();
    }

    /**
     * Perform the next step on a automated walking path.
     */
    private void autoStep() {
        if (autoPath == null) {
            return;
        }

        // get next step
        final PathNode node = autoPath.nextStep();
        // reached target
        if (node == null) {
            cancelAutoWalk();
            return;
        }

        final Location stepDestination = node.getLocation();
        final MapTile tile = World.getMap().getMapAt(stepDestination);
        final Location loc = parentPlayer.getLocation();

        // check whether path is clear
        if ((tile == null) || tile.isBlocked() || (loc.getDistance(stepDestination) > 1)) {
            // recalculate route if blocked or char is off route
            if (autoDestination != null) {
                walkTo(autoDestination);
            }
            return;
        }

        // execute walking step
        final int direction = loc.getDirection(stepDestination);
        requestMove(direction, CharMovementMode.Walk, false, false);
    }

    /**
     * Stop the automated walking.
     */
    public void cancelAutoWalk() {
        autoPath = null;
        autoDestination = null;
    }

    /**
     * Make the character automatically walking to a target location.
     *
     * @param destination the location the character shall walk to
     */
    public void walkTo(@Nonnull final Location destination) {
        cancelAutoWalk();

        final Location loc = parentPlayer.getLocation();
        final MapTile walkTarget = World.getMap().getMapAt(destination);
        if ((walkTarget != null) && (destination.getScZ() == loc.getScZ()) && !walkTarget.isObstacle()) {
            Pathfinder.getInstance().findPath(loc, destination, this);
        }
    }

    private boolean allowRunPathModification = IllaClient.getCfg().getBoolean("runAutoAvoid");

    /**
     * Request a move of the player character. This function will check if there is already a move or not. In case
     * there
     * is one and depending on how far the old move is processed already, its possible that a new move is requested
     * already earlier so the movement looks all in all smooth.
     *
     * @param direction           the direction the move shall be performed in
     * @param mode                the mode of the move that shall be performed
     * @param stopAutoMove        {@code true} in case this request shall remove the currently running automated movement
     * @param runPathModification {@code true} in case the function is supposed to try to change the running path in
     *                            order to avoid obstacles
     */
    @SuppressWarnings("nls")
    private void requestMove(final int direction, @Nonnull final CharMovementMode mode, final boolean stopAutoMove,
                             final boolean runPathModification) {
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
                if (!moving || (moveAnimation.timeRemaining() <= MOVEMENT_OVERLAP_TIME)) {
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
     * @param direction    the direction the player wants his character to look at
     * @param stopAutoMove {@code true} in case this request shall remove the currently running automated movement
     */
    private void requestTurn(final int direction, final boolean stopAutoMove) {
        if (stopAutoMove) {
            cancelAutoWalk();
        }
        if ((isAnyTrunRequested() || (parentPlayer.getCharacter().getDirection() != direction))
                && !requestedTurns[direction]) {
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
        for (final boolean turn : requestedTurns) {
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
    private void performNextRunningStep(final int direction) {
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
        final boolean straightMovePossible = isStepPossible(direction, CharMovementMode.Run);
        if (straightMovePossible && (runningPathModification == 0)) {
            sendTurnAndMoveToServer(direction, CharMovementMode.Run);
            return;
        }

        // calculate the two directions that are possible
        final int possibleDir1 = (direction + 1) % Location.DIR_MOVE8;
        final int possibleDir2 = (direction == 0) ? 7 : (direction - 1);

        // assign the direction that should be used at preference to undo old path modifications
        final int preferredDir;
        final int secondDir;
        final int preferredMod;
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
     * @param mode      the mode method, only {@link CharMovementMode#Run} and {@link CharMovementMode#Walk} are allowed
     * @return {@code true} in case the step is possible
     * @throws IllegalArgumentException in case the mode has a illegal value
     * @throws IllegalStateException    in case something else goes wrong
     */
    public static boolean isStepPossible(final int direction, @Nonnull final CharMovementMode mode) {
        switch (mode) {
            case None:
            case Push:
                throw new IllegalArgumentException("mode");
            case Walk:
                final Location locAfterStep = new Location(World.getPlayer().getLocation(), direction);
                return isLocationFree(locAfterStep);
            case Run:
                final Location newLoc = new Location(World.getPlayer().getLocation(), direction);
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
    private static boolean isLocationFree(@Nonnull final Location loc) {
        final MapTile tile = World.getMap().getMapAt(loc);
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
     * @param mode      the movement method of the character
     */
    private void sendTurnAndMoveToServer(final int direction, @Nonnull final CharMovementMode mode) {
        requestTurn(direction, false);
        sendMoveToServer(direction, mode);
    }

    /**
     * Send the movement command to the server.
     *
     * @param direction the direction of the requested move
     * @param mode      the mode of the requested move
     */
    private void sendMoveToServer(final int direction, @Nonnull final CharMovementMode mode) {
        final CharacterId playerId = parentPlayer.getPlayerId();
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

    /**
     * Set current location of the animation. This is useless in this class and this function is only implemented so
     * this class is a valid animation target.
     *
     * @param posX the new x coordinate of the animation target
     * @param posY the new y coordinate of the animation target
     * @param posZ the new z coordinate of the animation target
     */
    @Override
    public void setPosition(final int posX, final int posY, final int posZ) {
        if (positionDirty && (moveAnimation.animationProgress() > POSITION_UPDATE_PROCESS)) {
            positionDirty = false;
        } else if ((moveAnimation.timeRemaining() <= MOVEMENT_OVERLAP_TIME) && (lastAllowedMove == Location.DIR_ZERO)
                && (lastMoveRequest == Location.DIR_ZERO)) {
            if (moveToDirectionActive) {
                requestMove(getCurrentMoveToDirection(), moveToDirectionMode);
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
     * @param mode   the moving method that was send by the server
     * @param target the target location of the character after the move
     * @param speed  the speed of the move
     */
    public void acknowledgeMove(@Nonnull final CharMovementMode mode, @Nonnull final Location target,
                                final int speed) {
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
     * @param mode   the movement mode
     * @param target the target location where the character shall be located at at the end of the move
     * @param speed  the speed of the walk that determines how long the animation takes
     */
    private void performMove(@Nonnull final CharMovementMode mode, @Nonnull final Location target, final int speed) {
        final Char playerCharacter = parentPlayer.getCharacter();
        if ((mode == CharMovementMode.None) || playerCharacter.getLocation().equals(target)) {
            parentPlayer.updateLocation(target);
            playerCharacter.setLocation(target);
            animationFinished(true);
            World.getMapDisplay().animationFinished(true);
            walkTowards = false;
            return;
        }

        playerCharacter.moveTo(target, mode, speed);
        final int oldElevation = World.getMapDisplay().getElevation();
        final int newElevation = World.getMap().getElevationAt(target);
        final int xOffset = parentPlayer.getLocation().getDcX() - target.getDcX();
        final int yOffset = parentPlayer.getLocation().getDcY() - target.getDcY();
        moveAnimation.start(0, 0, -oldElevation, xOffset, yOffset, -newElevation, speed);

        parentPlayer.updateLocation(target);
    }

    /**
     * This is called in case the animation got finished. This is needed so the next animation can be started correctly
     * in case it was requested.
     *
     * @param finished {@code true} in case the animation is really done, false in case it got canceled
     */
    @Override
    public void animationFinished(final boolean finished) {
        positionDirty = true;
        final Char playerCharacter = parentPlayer.getCharacter();
        playerCharacter.getLocation().set(parentPlayer.getLocation());
        playerCharacter.updatePosition(0);
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
    public void acknowledgeTurn(final int direction) {
        requestedTurns[direction] = false;
        parentPlayer.getCharacter().setDirection(direction);
    }

    /**
     * Check if the character is currently moved and turned by mouse control.
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
    public void walkTowards(final int screenPosX, final int screenPosY) {
        final MapDimensions mapDimensions = MapDimensions.getInstance();
        final int xOffset = screenPosX - (mapDimensions.getOnScreenWidth() / 2);
        final int yOffset = -(screenPosY - (mapDimensions.getOnScreenHeight() / 2));
        final int distance = FastMath.sqrt((xOffset * xOffset) + (yOffset * yOffset));

        if (distance <= 5) {
            return;
        }

        final float relXOffset = (float) xOffset / (float) distance;
        final float relYOffset = (float) yOffset / (float) distance;

        //noinspection IfStatementWithTooManyBranches
        if (input.isAnyKeyDown(Key.LeftShift, Key.RightShift)) {
            walkTowardsMode = CharMovementMode.None;
        } else if (distance > 200) {
            walkTowardsMode = CharMovementMode.Run;
        } else if (distance < 30) {
            walkTowardsMode = CharMovementMode.None;
        } else {
            walkTowardsMode = CharMovementMode.Walk;
        }

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
}
