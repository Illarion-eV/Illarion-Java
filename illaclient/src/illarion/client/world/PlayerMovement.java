/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.world;

import illarion.client.graphics.AnimatedMove;
import illarion.client.graphics.MapDisplayManager;
import illarion.client.graphics.MarkerFactory;
import illarion.client.graphics.MoveAnimation;
import illarion.client.guiNG.GUI;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.MoveCmd;
import illarion.client.util.Path;
import illarion.client.util.PathNode;
import illarion.client.util.PathReceiver;
import illarion.client.util.Pathfinder;

import illarion.common.util.FastMath;
import illarion.common.util.Location;

import illarion.sound.SoundListener;

/**
 * The player movement class takes and handles all move requests and orders that
 * are needed to move the player character over the map.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class PlayerMovement implements AnimatedMove, PathReceiver {
    /**
     * The constant for no move.
     */
    public static final int MOVE_MODE_NONE = -1;

    /**
     * The constant for a pushed move.
     */
    public static final int MOVE_MODE_PUSH = 2;

    /**
     * The constant for a running move.
     */
    public static final int MOVE_MODE_RUN = 1;

    /**
     * The constant for a normal walking move.
     */
    public static final int MOVE_MODE_WALK = 0;

    /**
     * This value is the relation of the distance from the character location to
     * the location of the cursor to the plain x or y offset. In case the
     * relation is smaller or equal to this the character will move straight
     * horizontal or vertical on the screen. Else it will move diagonal.
     */
    private static final double MOUSE_ANGLE = Math.cos(Math.PI
        / Location.DIR_MOVE8);

    /**
     * The overlap time of a movement in milliseconds. If this time is left of
     * the last move animation the next step is already requested from the
     * server in oder to get a smooth walking animation.
     */
    private static final int MOVEMENT_OVERLAP_TIME = 250;

    /**
     * The time when the updated position is reported to the rest of the client
     * and the game map is updated regarding this.
     */
    private static final float POSITION_UPDATE_PROCESS = 0.5f;

    /**
     * The time in milliseconds until the requests that are not answered are
     * discarded.
     */
    private static final int TIME_UNTIL_DISCARD = 5000;

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
    private int lastAllowedMoveMode = MOVE_MODE_NONE;

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
    private final Location lastMoveTarget = Location.getInstance();

    /**
     * The move animation that moves the map around. This animation is also used
     * to fetch the informations about the process of the animation so a
     * overlapped move can be requested in case its needed.
     */
    private final MoveAnimation moveAnimation = new MoveAnimation(
        Game.getDisplay());

    /**
     * <code>True</code> in case the character is currently moving, false if
     * not.
     */
    private boolean moving = false;

    /**
     * The player handler that is controlled by this movement handler.
     */
    private final Player parentPlayer;

    /**
     * A reference to the character of the player that is used to get some
     * informations about the player character, such as the current look at
     * direction.
     */
    private final Char playerCharacter;

    /**
     * The sound listener of the player that is required to be updated in case
     * the location or the direction of the player character changes.
     */
    private final SoundListener playerListener;

    /**
     * The variable stores if the location of the character got already updated
     * or not.
     */
    private boolean positionUpdated = false;

    /**
     * The list of direction to store to what directions the player wants to
     * turn to. This is needed to avoid that the client spams the server with
     * turn requests.
     */
    private final boolean[] requestedTurns = new boolean[Location.DIR_MOVE8];

    /**
     * This time stores when the requests of moves and turns are discarded in
     * order to allow the player to request more moves and turns. So nothing is
     * going to stand still in case some messages from the server are not
     * received correctly.
     */
    private long timeOfDiscard = 0L;

    private boolean walkTowards = false;

    private int walkTowardsDir = Location.DIR_ZERO;

    private int walkTowardsMode = -1;

    /**
     * Default constructor.
     * 
     * @param parent the player handler that is controlled by this movement
     *            handler
     */
    public PlayerMovement(final Player parent) {
        moveAnimation.addTarget(this, false);
        parentPlayer = parent;
        playerCharacter = parent.getCharacter();
        playerListener = parent.getSoundListener();
    }

    /**
     * Perform the move of a player character after the move was confirmed by
     * the player.
     * 
     * @param mode the moving method that was send by the server
     * @param target the target location of the character after the move
     * @param speed the speed of the move
     */
    public void acknowledgeMove(final int mode, final Location target,
        final int speed) {
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
     * Get the acknowledge of a turn of the player character.
     * 
     * @param direction the new direction of the player character after the turn
     */
    public void acknowledgeTurn(final int direction) {
        requestedTurns[direction] = false;
        playerCharacter.setDirection(direction);
        playerListener.setDirection(direction);
    }

    /**
     * This is called in case the animation got finished. This is needed so the
     * next animation can be started correctly in case it was requested.
     * 
     * @param finished <code>true</code> in case the animation is really done,
     *            false in case it got canceled
     */
    @Override
    public void animationFinished(final boolean finished) {
        positionUpdated = false;
        playerCharacter.getLocation().set(parentPlayer.getLocation());
        playerCharacter.updatePosition(0);
        Game.getPeople().checkVisibility();
        Game.getMap().checkInside();

        if (lastAllowedMove == Location.DIR_ZERO) {
            moving = false;
            return;
        }

        lastAllowedMove = Location.DIR_ZERO;
        performMove(lastAllowedMoveMode, lastMoveTarget, lastMoveSpeed);
    }

    /**
     * Stop the automated walking.
     */
    public void cancelAutoWalk() {
        autoPath = null;
        autoDest = null;
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
     * Get if the player character is currently moving.
     * 
     * @return <code>true</code> in case the character is currently moving
     */
    public boolean isMoving() {
        return moving;
    }

    /**
     * Request a move of the player character. This function will check if there
     * is already a move or not. In case there is one and depending on how far
     * the old move is processed already, its possible that a new move is
     * requested already earlier so the movement looks all in all smooth.
     * 
     * @param direction the direction the move shall be performed in
     * @param mode the mode of the move that shall be performed
     */
    public void requestMove(final int direction, final int mode) {
        requestMove(direction, mode, true);
    }

    /**
     * This function needs to be triggered in case a turn needs to be done.
     * 
     * @param direction the direction the player wants his character to look at
     */
    public void requestTurn(final int direction) {
        requestTurn(direction, true);
    }

    /**
     * Set current location of the animation. This is useless in this class and
     * this function is only implemented so this class is a valid animation
     * target.
     * 
     * @param posX the new x coordinate of the animation target
     * @param posY the new y coordinate of the animation target
     * @param posZ the new z coordinate of the animation target
     */
    @Override
    public void setPosition(final int posX, final int posY, final int posZ) {
        if (!positionUpdated
            && (moveAnimation.animationProgress() > POSITION_UPDATE_PROCESS)) {
            // update mini-map and world-map position
            final Location targetLoc = parentPlayer.getLocation();
            Game.getMap().getMinimap().setPlayerLocation(targetLoc);

            // process obstructed tiles
            // Game.getMap().checkObstructions();
            positionUpdated = true;
        } else if ((moveAnimation.timeRemaining() <= MOVEMENT_OVERLAP_TIME)
            && (lastAllowedMove == Location.DIR_ZERO)
            && (lastMoveRequest == Location.DIR_ZERO)) {
            if (walkTowards) {
                requestMove(walkTowardsDir, walkTowardsMode);
            } else {
                autoStep();
            }
        }
    }

    /**
     * Cleanup the movement handler and prepare everything for a proper
     * shutdown.
     */
    public void shutdown() {
        moveAnimation.removeTarget(this);
        stopWalkTowards();
        cancelAutoWalk();
        animationFinished(false);
    }

    public void stopWalkTowards() {
        GUI.getInstance().getMouseCursor()
            .setCursor(MarkerFactory.CRSR_NORMAL);
        walkTowards = false;
    }

    /**
     * Make the character automatically walking to a target location.
     * 
     * @param dest the location the character shall walk to
     */
    public void walkTo(final Location dest) {
        cancelAutoWalk();

        final Location loc = parentPlayer.getLocation();
        final MapTile walkTarget = Game.getMap().getMapAt(dest);
        if ((walkTarget != null) && (dest.getScZ() == loc.getScZ())
            && !walkTarget.isObstacle()) {
            Pathfinder.getInstance().findPath(loc, dest, this);
        }
    }

    /**
     * Walk towards a direction.
     * 
     * @param screenPosX the x location on the screen to walk to
     * @param screenPosY the y location on the screen to walk to
     */
    public void walkTowards(final int screenPosX, final int screenPosY) {
        final int xOffset = screenPosX - MapDisplayManager.MAP_CENTER_X;
        final int yOffset = screenPosY - MapDisplayManager.MAP_CENTER_Y;
        final int distance =
            FastMath.sqrt((xOffset * xOffset) + (yOffset * yOffset));

        if (distance <= 5) {
            return;
        }

        final double relXOffset = (float) xOffset / (float) distance;
        final double relYOffset = (float) yOffset / (float) distance;

        if (distance > 200) {
            walkTowardsMode = MOVE_MODE_RUN;
        } else if (distance < 30) {
            walkTowardsMode = MOVE_MODE_NONE;
        } else {
            walkTowardsMode = MOVE_MODE_WALK;
        }

        if (relXOffset > MOUSE_ANGLE) {
            GUI.getInstance().getMouseCursor()
                .setCursor(MarkerFactory.CRSR_WALK_SE);
            walkTowardsDir = Location.DIR_SOUTHEAST;
        } else if (relXOffset < -MOUSE_ANGLE) {
            GUI.getInstance().getMouseCursor()
                .setCursor(MarkerFactory.CRSR_WALK_NW);
            walkTowardsDir = Location.DIR_NORTHWEST;
        } else if (relYOffset > MOUSE_ANGLE) {
            GUI.getInstance().getMouseCursor()
                .setCursor(MarkerFactory.CRSR_WALK_NE);
            walkTowardsDir = Location.DIR_NORTHEAST;
        } else if (relYOffset < -MOUSE_ANGLE) {
            GUI.getInstance().getMouseCursor()
                .setCursor(MarkerFactory.CRSR_WALK_SW);
            walkTowardsDir = Location.DIR_SOUTHWEST;
        } else if ((xOffset > 0) && (yOffset > 0)) {
            GUI.getInstance().getMouseCursor()
                .setCursor(MarkerFactory.CRSR_WALK_E);
            walkTowardsDir = Location.DIR_EAST;
        } else if ((xOffset > 0) && (yOffset < 0)) {
            GUI.getInstance().getMouseCursor()
                .setCursor(MarkerFactory.CRSR_WALK_S);
            walkTowardsDir = Location.DIR_SOUTH;
        } else if ((xOffset < 0) && (yOffset < 0)) {
            GUI.getInstance().getMouseCursor()
                .setCursor(MarkerFactory.CRSR_WALK_W);
            walkTowardsDir = Location.DIR_WEST;
        } else if ((xOffset < 0) && (yOffset > 0)) {
            GUI.getInstance().getMouseCursor()
                .setCursor(MarkerFactory.CRSR_WALK_N);
            walkTowardsDir = Location.DIR_NORTH;
        }

        if (walkTowardsMode == MOVE_MODE_NONE) {
            walkTowards = false;
            requestTurn(walkTowardsDir, true);
            return;
        }

        if (walkTowards) {
            return;
        }

        walkTowards = true;
        cancelAutoWalk();

        requestTurn(walkTowardsDir, false);
        requestMove(walkTowardsDir, walkTowardsMode);
    }

    /**
     * Check if any turn is requested but yet not confirmed by the server.
     * 
     * @return <code>true</code> in case there is a turn that is requested from
     *         the server
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
        final MapTile tile = Game.getMap().getMapAt(stepDest);
        final Location loc = parentPlayer.getLocation();

        // check whether path is clear
        if (tile.isBlocked() || (loc.getDistance(stepDest) > 1)) {
            // recalculate route if blocked or char is off route
            walkTo(autoDest);
            return;
        }

        // execute walking step
        final int direction = loc.getDirection(stepDest);
        requestTurn(direction, false);
        requestMove(direction, PlayerMovement.MOVE_MODE_WALK, false);
    }

    /**
     * Check if pending requests need to be discarded in order to keep the stuff
     * working correctly.
     */
    private void discardCheck() {
        if ((timeOfDiscard > 0L)
            && (timeOfDiscard < System.currentTimeMillis())) {
            for (int dir = 0; dir < Location.DIR_MOVE8; ++dir) {
                requestedTurns[dir] = false;
            }
            lastMoveRequest = Location.DIR_ZERO;
            timeOfDiscard = 0L;
        }
    }

    /**
     * Perform a move of the player character. This function does not perform
     * any checks. It just does the move no matter what the status of everything
     * is.
     * 
     * @param mode the movement mode, allowed values are {@link #MOVE_MODE_WALK}
     *            and {@link #MOVE_MODE_RUN}
     * @param target the target location where the character shall be located at
     *            at the end of the move
     * @param speed the speed of the walk that determines how long the animation
     *            takes
     */
    private void performMove(final int mode, final Location target,
        final int speed) {
        final illarion.client.guiNG.ChatEditor editor =
            illarion.client.guiNG.GUI.getInstance().getChatEditor();
        if (editor.isVisible() && (editor.getTextLength() == 0)) {
            editor.setVisible(false);
        }
        if ((mode == MOVE_MODE_NONE)
            || playerCharacter.getLocation().equals(target)) {
            parentPlayer.updateLocation(target);
            playerCharacter.setLocation(target);
            playerListener.setLocation(target);
            animationFinished(true);
            Game.getDisplay().animationFinished(true);
            walkTowards = false;
            return;
        }

        int moveMode = Char.MOVE_WALK;
        if (mode == MOVE_MODE_RUN) {
            moveMode = Char.MOVE_RUN;
        }
        playerCharacter.moveTo(target, moveMode, speed);
        final int oldElevation = Game.getDisplay().getElevation();
        final int newElevation = Game.getMap().getElevationAt(target);
        final int xOffset =
            parentPlayer.getLocation().getDcX() - target.getDcX();
        final int yOffset =
            parentPlayer.getLocation().getDcY() - target.getDcY();
        moveAnimation.start(0, 0, oldElevation, xOffset, yOffset,
            newElevation, speed);

        parentPlayer.updateLocation(target);
        playerListener.setLocation(target);
        Game.getMusicBox().updatePlayerLocation();
    }

    /**
     * Request a move of the player character. This function will check if there
     * is already a move or not. In case there is one and depending on how far
     * the old move is processed already, its possible that a new move is
     * requested already earlier so the movement looks all in all smooth.
     * 
     * @param direction the direction the move shall be performed in
     * @param mode the mode of the move that shall be performed
     * @param stopAutoMove <code>true</code> in case this request shall remove
     *            the currently running automated movement
     */
    @SuppressWarnings("nls")
    private void requestMove(final int direction, final int mode,
        final boolean stopAutoMove) {
        if (mode == MOVE_MODE_PUSH) {
            throw new IllegalArgumentException("Pushed moves are not"
                + " supported by the player movement handler.");
        }
        if (stopAutoMove) {
            cancelAutoWalk();
        }
        if ((lastMoveRequest == Location.DIR_ZERO)
            && (lastAllowedMove == Location.DIR_ZERO)) {
            if (!moving) {
                lastMoveRequest = direction;
                timeOfDiscard =
                    System.currentTimeMillis() + TIME_UNTIL_DISCARD;
                sendMoveToServer(direction, mode);
            } else if (moveAnimation.timeRemaining() <= MOVEMENT_OVERLAP_TIME) {
                lastMoveRequest = direction;
                timeOfDiscard =
                    System.currentTimeMillis() + TIME_UNTIL_DISCARD;
                sendMoveToServer(direction, mode);
            }
        } else {
            discardCheck();
        }
    }

    /**
     * This function needs to be triggered in case a turn needs to be done.
     * 
     * @param direction the direction the player wants his character to look at
     * @param stopAutoMove <code>true</code> in case this request shall remove
     *            the currently running automated movement
     */
    private void requestTurn(final int direction, final boolean stopAutoMove) {
        if (stopAutoMove) {
            cancelAutoWalk();
        }
        if ((anyTurnRequested() || (playerCharacter.getDirection() != direction))
            && !requestedTurns[direction]) {
            requestedTurns[direction] = true;
            Game.getNet().sendCommand(
                CommandFactory.getInstance().getCommand(
                    CommandList.CMD_TURN_N + direction));
            timeOfDiscard = System.currentTimeMillis() + TIME_UNTIL_DISCARD;
        } else {
            discardCheck();
        }
    }

    /**
     * Send the movement command to the server.
     * 
     * @param direction the direction of the requested move
     * @param mode the mode of the requested move
     */
    private void sendMoveToServer(final int direction, final int mode) {
        final MoveCmd cmd =
            (MoveCmd) CommandFactory.getInstance().getCommand(
                CommandList.CMD_MOVE);
        cmd.setDirection(parentPlayer.getPlayerId(), direction);
        if (mode == MOVE_MODE_RUN) {
            cmd.setRunning();
        } else {
            cmd.setMoving();
        }
        Game.getNet().sendCommand(cmd);
    }
}
