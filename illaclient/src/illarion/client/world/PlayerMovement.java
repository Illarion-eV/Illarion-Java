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

import illarion.client.graphics.AnimatedMove;
import illarion.client.graphics.MoveAnimation;
import illarion.client.input.InputReceiver;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.MoveCmd;
import illarion.client.util.Path;
import illarion.client.util.PathNode;
import illarion.client.util.PathReceiver;
import illarion.client.util.Pathfinder;
import illarion.common.types.Location;
import illarion.common.util.FastMath;
import org.apache.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.lwjgl.input.Keyboard;

/**
 * The player movement class takes and handles all move requests and orders that are needed to move the player
 * character
 * over the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class PlayerMovement
        implements AnimatedMove, PathReceiver {
    /**
     * This Enumerator contains the possible value for the movement methods of a character.
     */
    public enum MovementMode {
        /**
         * This constant means that no movement is done. The character is only turning around or warping.
         */
        None,

        /**
         * This movement mode means that the character is walking.
         */
        Walk,

        /**
         * This constant means that the character is running.
         */
        Run,

        /**
         * This constant means that the character is being pushed.
         */
        Push;
    }

    /**
     * This value is the relation of the distance from the character location to the location of the cursor to the
     * plain
     * x or y offset. In case the relation is smaller or equal to this the character will move straight horizontal or
     * vertical on the screen. Else it will move diagonal.
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
    private static final Logger LOGGER = Logger.getLogger(PlayerMovement.class);

    /**
     * The destination location of the automated walking.
     */
    private Location autoDest;

    /**
     * The path the player is following by the automated movement.
     */
    private Path autoPath;

    /**
     * The last move that was allowed by the server but yet not performed.
     */
    private int lastAllowedMove = Location.DIR_ZERO;

    /**
     * The mode of the last move that was allowed by the server.
     */
    private MovementMode lastAllowedMoveMode = MovementMode.None;

    /**
     * The last move that was requested from the server.
     */
    private int lastMoveRequest = Location.DIR_ZERO;

    /**
     * The speed of the last allowed move.
     */
    private int lastMoveSpeed = 0;

    /**
     * The target location of the last move.
     */
    private final Location lastMoveTarget = new Location();

    /**
     * The move animation that moves the map around. This animation is also used to fetch the informations about the
     * process of the animation so a overlapped move can be requested in case its needed.
     */
    private final MoveAnimation moveAnimation = new MoveAnimation(World.getMapDisplay());

    /**
     * <code>True</code> in case the character is currently moving, false if not.
     */
    private boolean moving = false;

    /**
     * The player handler that is controlled by this movement handler.
     */
    private final Player parentPlayer;

    /**
     * A reference to the character of the player that is used to get some informations about the player character,
     * such
     * as the current look at direction.
     */
    private final Char playerCharacter;

    /**
     * The variable stores if the location of the character got already updated or not.
     */
    private boolean positionUpdated = false;

    /**
     * The list of direction to store to what directions the player wants to turn to. This is needed to avoid that the
     * client spams the server with turn requests.
     */
    private final boolean[] requestedTurns = new boolean[Location.DIR_MOVE8];

    /**
     * This time stores when the requests of moves and turns are discarded in order to allow the player to request more
     * moves and turns. So nothing is going to stand still in case some messages from the server are not received
     * correctly.
     */
    private long timeOfDiscard = 0L;

    private boolean walkTowards = false;

    private boolean mouseMovementActive = false;

    private int walkTowardsDir = Location.DIR_ZERO;

    private MovementMode walkTowardsMode = MovementMode.None;

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
     * Default constructor.
     *
     * @param parent the player handler that is controlled by this movement handler
     */
    public PlayerMovement(final Player parent) {
        moveAnimation.addTarget(this, false);
        parentPlayer = parent;
        playerCharacter = parent.getCharacter();
        AnnotationProcessor.process(this);
    }

    /**
     * This is the event handler that takes care of processing input event.
     *
     * @param topic the event topic, should be {@link InputReceiver#EB_TOPIC}
     * @param data  the event data
     */
    @EventTopicSubscriber(topic = InputReceiver.EB_TOPIC)
    public void onInputEventReceived(final String topic, final String data) {
        final MovementMode moveMode;
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            moveMode = MovementMode.Run;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
            moveMode = MovementMode.None;
        } else {
            moveMode = MovementMode.Walk;
        }
        if (InputReceiver.EB_TOPIC.equals(topic)) {
            if ("WalkNorth".equals(data)) {
                requestMove(Location.DIR_NORTH, moveMode);
            } else if ("WalkNorthEast".equals(data)) {
                requestMove(Location.DIR_NORTHEAST, moveMode);
            } else if ("WalkEast".equals(data)) {
                requestMove(Location.DIR_EAST, moveMode);
            } else if ("WalkSouthEast".equals(data)) {
                requestMove(Location.DIR_SOUTHEAST, moveMode);
            } else if ("WalkSouth".equals(data)) {
                requestMove(Location.DIR_SOUTH, moveMode);
            } else if ("WalkSouthWest".equals(data)) {
                requestMove(Location.DIR_SOUTHWEST, moveMode);
            } else if ("WalkWest".equals(data)) {
                requestMove(Location.DIR_WEST, moveMode);
            } else if ("WalkNorthWest".equals(data)) {
                requestMove(Location.DIR_NORTHWEST, moveMode);
            }
        }
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
    public void requestMove(final int direction, final MovementMode mode) {
        requestMove(direction, mode, true, false);
    }

    @Override
    public void handlePath(final Path path) {
        cancelAutoWalk();
        autoPath = path;
        if (autoPath != null) {
            autoDest = autoPath.getDestination();
            autoPath.nextStep();
            autoStep();
        }
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

        final Location stepDest = node.getLocation();
        final MapTile tile = World.getMap().getMapAt(stepDest);
        final Location loc = parentPlayer.getLocation();

        // check whether path is clear
        if (tile.isBlocked() || (loc.getDistance(stepDest) > 1)) {
            // recalculate route if blocked or char is off route
            walkTo(autoDest);
            return;
        }

        // execute walking step
        final int direction = loc.getDirection(stepDest);
        requestMove(direction, MovementMode.Walk, false, false);
    }

    /**
     * Stop the automated walking.
     */
    public void cancelAutoWalk() {
        autoPath = null;
        autoDest = null;
    }

    /**
     * Make the character automatically walking to a target location.
     *
     * @param dest the location the character shall walk to
     */
    public void walkTo(final Location dest) {
        cancelAutoWalk();

        final Location loc = parentPlayer.getLocation();
        final MapTile walkTarget = World.getMap().getMapAt(dest);
        if ((walkTarget != null) && (dest.getScZ() == loc.getScZ()) && !walkTarget.isObstacle()) {
            Pathfinder.getInstance().findPath(loc, dest, this);
        }
    }

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
    private void requestMove(final int direction, final MovementMode mode, final boolean stopAutoMove,
                             final boolean runPathModification) {
        if (mode == MovementMode.Push) {
            throw new IllegalArgumentException("Pushed moves are not supported by the player movement handler.");
        }
        if (stopAutoMove) {
            cancelAutoWalk();
        }
        if (mode == MovementMode.None) {
            requestTurn(direction, false);
            discardCheck();
        } else {
            if ((lastMoveRequest == Location.DIR_ZERO) && (lastAllowedMove == Location.DIR_ZERO)) {
                if (!moving || (moveAnimation.timeRemaining() <= MOVEMENT_OVERLAP_TIME)) {
                    lastMoveRequest = direction;
                    timeOfDiscard = System.currentTimeMillis() + TIME_UNTIL_DISCARD;
                    if ((mode == MovementMode.Run) && runPathModification) {
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
        if ((anyTurnRequested() || (playerCharacter.getDirection() != direction)) && !requestedTurns[direction]) {
            requestedTurns[direction] = true;
            CommandFactory.getInstance().getCommand(CommandList.CMD_TURN_N + direction).send();
            timeOfDiscard = System.currentTimeMillis() + TIME_UNTIL_DISCARD;
        } else {
            discardCheck();
        }
    }

    /**
     * Check if any turn is requested but yet not confirmed by the server.
     *
     * @return <code>true</code> in case there is a turn that is requested from the server
     */
    private boolean anyTurnRequested() {
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
        final boolean straightMovePossible = isStepPossible(direction, MovementMode.Run);
        if (straightMovePossible && (runningPathModification == 0)) {
            sendTurnAndMoveToServer(direction, MovementMode.Run);
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
            if (isStepPossible(preferredDir, MovementMode.Walk)) {
                runningPathModification = 0;
                stepsWithPathModification = 0;
                sendTurnAndMoveToServer(preferredDir, MovementMode.Walk);
                return;
            }
        } else if (Math.abs(runningPathModification) >= 2) {
            // path is altered by one or more running steps. Undo them if possible.
            if (isStepPossible(preferredDir, MovementMode.Run)) {
                runningPathModification += preferredMod * 2;
                stepsWithPathModification = 0;
                sendTurnAndMoveToServer(preferredDir, MovementMode.Run);
                return;
            }
        }

        // undoing old changed to the path failed. Try to move along the current modification running.
        if (straightMovePossible && ((stepsWithPathModification + 2) <= MAX_MODIFIED_STEPS)) {
            stepsWithPathModification += 2;
            sendTurnAndMoveToServer(direction, MovementMode.Run);
            return;
        }

        // Increase the modification with a running step if allowed
        if ((runningPathModification - (preferredDir * 2)) <= MAX_PATH_MODIFICATION) {
            if (isStepPossible(secondDir, MovementMode.Run)) {
                runningPathModification -= preferredMod * 2;
                stepsWithPathModification = 0;
                sendTurnAndMoveToServer(secondDir, MovementMode.Run);
                return;
            }
        }

        // Running does not seem to be possible. Try to reduce the path modification with a walking step.
        if (isStepPossible(preferredDir, MovementMode.Walk)) {
            runningPathModification += preferredMod;
            stepsWithPathModification = 0;
            sendTurnAndMoveToServer(preferredDir, MovementMode.Walk);
            return;
        }

        // Try walking without increasing the modification.
        if (isStepPossible(direction, MovementMode.Walk)) {
            stepsWithPathModification += 1;
            sendTurnAndMoveToServer(direction, MovementMode.Walk);
            return;
        }

        // Increase the modification with a walking step.
        if ((runningPathModification - preferredDir) <= MAX_PATH_MODIFICATION) {
            if (isStepPossible(secondDir, MovementMode.Walk)) {
                runningPathModification -= preferredMod;
                stepsWithPathModification = 0;
                sendTurnAndMoveToServer(secondDir, MovementMode.Walk);
                return;
            }
        }

        // all out of options, just run straight ahead and hope for the best. GERONIMO!
        resetRunPathModification();
        sendTurnAndMoveToServer(direction, MovementMode.Run);
    }

    /**
     * Check if a move is possible.
     *
     * @param direction the direction of the move
     * @param mode      the mode method, only {@link MovementMode#Run} and {@link MovementMode#Walk} are allowed
     * @return {@code true} in case the step is possible
     * @throws IllegalArgumentException in case the mode has a illegal value
     * @throws NullPointerException     in case {@code mode} is {@code null}
     * @throws IllegalStateException    in case something else goes wrong
     */
    public static boolean isStepPossible(final int direction, final MovementMode mode) {
        if (mode == null) {
            throw new NullPointerException("mode");
        }
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
    private static boolean isLocationFree(final Location loc) {
        final MapTile tile = World.getMap().getMapAt(loc);
        if (tile == null) {
            return false;
        }
        return !tile.isBlocked();
    }

    /**
     * Reset all running path modifications. This should be done in case moves are made without using the {@link
     * #performNextRunningStep(int)} function.
     */
    public void resetRunPathModification() {
        runningPathLastDirection = -1;
    }

    private void sendTurnAndMoveToServer(final int direction, final MovementMode mode) {
        requestTurn(direction, false);
        sendMoveToServer(direction, mode);
    }

    /**
     * Send the movement command to the server.
     *
     * @param direction the direction of the requested move
     * @param mode      the mode of the requested move
     */
    private void sendMoveToServer(final int direction, final MovementMode mode) {
        final MoveCmd cmd = (MoveCmd) CommandFactory.getInstance().getCommand(CommandList.CMD_MOVE);
        cmd.setDirection(parentPlayer.getPlayerId(), direction);
        if (mode == MovementMode.Run) {
            cmd.setRunning();
        } else {
            cmd.setMoving();
        }
        cmd.send();
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
        if (!positionUpdated && (moveAnimation.animationProgress() > POSITION_UPDATE_PROCESS)) {
            // update mini-map and world-map position
            final Location targetLoc = parentPlayer.getLocation();
            World.getMap().getMinimap().setPlayerLocation(targetLoc);

            // process obstructed tiles
            // Game.getMap().checkObstructions();
            positionUpdated = true;
        } else if ((moveAnimation.timeRemaining() <= MOVEMENT_OVERLAP_TIME) && (lastAllowedMove == Location.DIR_ZERO)
                && (lastMoveRequest == Location.DIR_ZERO)) {
            if (walkTowards) {
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
    public void acknowledgeMove(final MovementMode mode, final Location target, final int speed) {
        lastMoveRequest = Location.DIR_ZERO;

        if (!moving) {
            moving = true;
            performMove(mode, target, speed);
            return;
        }

        lastAllowedMove = playerCharacter.getLocation().getDirection(target);
        lastAllowedMoveMode = mode;
        lastMoveTarget.set(target);
        lastMoveSpeed = speed;
    }

    /**
     * Perform a move of the player character. This function does not perform any checks. It just does the move no
     * matter what the status of everything is.
     *
     * @param mode   the movement mode
     * @param target the target location where the character shall be located at at the end of the move
     * @param speed  the speed of the walk that determines how long the animation takes
     */
    private void performMove(final MovementMode mode, final Location target, final int speed) {
        if ((mode == MovementMode.None) || playerCharacter.getLocation().equals(target)) {
            parentPlayer.updateLocation(target);
            playerCharacter.setLocation(target);
            animationFinished(true);
            World.getMapDisplay().animationFinished(true);
            walkTowards = false;
            return;
        }

        int moveMode = Char.MOVE_WALK;
        if (mode == MovementMode.Run) {
            moveMode = Char.MOVE_RUN;
        }
        playerCharacter.moveTo(target, moveMode, speed);
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
     * @param finished <code>true</code> in case the animation is really done, false in case it got canceled
     */
    @Override
    public void animationFinished(final boolean finished) {
        positionUpdated = false;
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
        playerCharacter.setDirection(direction);
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
     * @return <code>true</code> in case the character is currently moving
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
        // GUI.getInstance().getMouseCursor()
        // .setCursor(MarkerFactory.CRSR_NORMAL);
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

        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            walkTowardsMode = MovementMode.None;
        } else if (distance > 200) {
            walkTowardsMode = MovementMode.Run;
        } else if (distance < 30) {
            walkTowardsMode = MovementMode.None;
        } else {
            walkTowardsMode = MovementMode.Walk;
        }

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

        if (walkTowardsMode == MovementMode.None) {
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
