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

import java.awt.Point;
import java.io.File;

import org.apache.log4j.Logger;

import illarion.client.IllaClient;
import illarion.client.graphics.Avatar;
import illarion.client.util.Lang;

import illarion.common.config.Config;
import illarion.common.config.ConfigChangeListener;
import illarion.common.config.ConfigSystem;
import illarion.common.util.Bresenham;
import illarion.common.util.DirectoryManager;
import illarion.common.util.FastMath;
import illarion.common.util.Location;

import illarion.sound.SoundListener;
import illarion.sound.SoundManager;

/**
 * Main Class for the player controlled character.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class Player implements ConfigChangeListener {
    /**
     * Maximal value for the volume of the sound.
     */
    public static final float MAX_CLIENT_VOL = 100.f;

    /**
     * Encoding for a left turn.
     */
    public static final int TURN_LEFT = 1;

    /**
     * Encoding for a right turn.
     */
    public static final int TURN_RIGHT = -1;

    /**
     * The key in the configuration for the music on/off flag.
     */
    private static final String CFG_MUSIC_ON = "musicOn"; //$NON-NLS-1$

    /**
     * The key in the configuration for the music volume value.
     */
    private static final String CFG_MUSIC_VOL = "musicVolume"; //$NON-NLS-1$

    /**
     * The key in the configuration for the sound on/off flag.
     */
    private static final String CFG_SOUND_ON = "soundOn"; //$NON-NLS-1$

    /**
     * The key in the configuration for the sound volume value.
     */
    private static final String CFG_SOUND_VOL = "soundVolume"; //$NON-NLS-1$

    /**
     * Maximal distance to visible objects.
     */
    private static final int CLIP_DISTANCE = 14;

    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(Player.class);

    /**
     * The maximum range between 2 characters, that can look at each other.
     */
    private static final int MAXIMUM_LOOKING_AT_RANGE = 6;

    /**
     * The minium range between 2 characters, that can look at each other.
     */
    private static final int MINIMUM_LOOKING_AT_RANGE = 2;

    /**
     * Average value for the perception attribute.
     */
    private static final int PERCEPTION_AVERAGE = 8;

    /**
     * Share of one perception point on the coverage of a tile.
     */
    private static final int PERCEPTION_COVER_SHARE = 4;

    /**
     * The player specific configuration of the client.
     */
    private final ConfigSystem cfg;

    /**
     * The graphical representation of the character.
     */
    private final Char character;

    /**
     * The sound listener that receives all sound effects the player characters
     * hears.
     */
    private final SoundListener listener;

    /**
     * The current location of the server map for the player.
     */
    private final Location loc = Location.getInstance();

    /**
     * The player movement handler that takes care that the player character is
     * walking around.
     */
    private final PlayerMovement movementHandler;

    /**
     * The name of the players character.
     */
    private final String name;

    /**
     * The path to the folder of character specific stuff, like the map or the
     * names table.
     */
    private final File path;

    /**
     * The perception attribute of the character. This value is used for LOS
     * calculations.
     */
    private int perception;

    /**
     * The character ID of the player.
     */
    private long playerId;

    /**
     * Default constructor for the player.
     * 
     * @param newName the character name of the player playing this game
     */
    @SuppressWarnings("nls")
    protected Player(final String newName) {
        name = newName;

        path =
            new File(DirectoryManager.getInstance().getUserDirectory(), name);

        character = Char.create();

        if (!path.isDirectory() && !path.mkdir()) {
            IllaClient
                .fallbackToLogin(Lang.getMsg("error.character_settings"));
            cfg = null;
            movementHandler = null;
            listener = null;
            return;
        }

        cfg = new ConfigSystem(new File(path, "Player.xml.gz"));
        character.setName(name);
        character.setVisible(Char.VISIBILITY_MAX);
        Game.getPeople().setPlayerCharacter(character);

        // followed = null;
        listener = SoundManager.getInstance().getSoundListener();
        movementHandler = new PlayerMovement(this);

        IllaClient.getCfg().addListener(CFG_SOUND_ON, this);
        IllaClient.getCfg().addListener(CFG_SOUND_VOL, this);
        IllaClient.getCfg().addListener(CFG_MUSIC_ON, this);
        IllaClient.getCfg().addListener(CFG_MUSIC_VOL, this);

        updateListener();
    }

    /**
     * The monitor function that is notified in case the configuration changes
     * and triggers the required updates.
     */
    @Override
    public void configChanged(final Config config, final String key) {
        if (key.equals(CFG_SOUND_ON) || key.equals(CFG_SOUND_VOL)
            || key.equals(CFG_MUSIC_ON) || key.equals(CFG_MUSIC_VOL)) {
            updateListener();
        }
    }

    /**
     * Get the map level the player character is currently standing on.
     * 
     * @return The level the player character is currently standing on
     */
    public int getBaseLevel() {
        return loc.getScZ();
    }

    /**
     * Get the configuration interface for the character specific settings.
     * 
     * @return the configuration interface
     */
    public Config getCfg() {
        return cfg;
    }

    /**
     * Get the graphical representation of the players character.
     * 
     * @return The character of the player
     */
    public Char getCharacter() {
        return character;
    }

    /**
     * Get the location in the front of the character.
     * 
     * @return The location right in the front of the character
     * @deprecated Better use {@link #getFrontLocation(Location)} to avoid the
     *             creation of too many new objects
     */
    @Deprecated
    public Location getFrontLocation() {
        final Location front = Location.getInstance();
        getFrontLocation(front);

        return front;
    }

    /**
     * Get the location in the front of the character.
     * 
     * @param targetLoc the front location of the character is stored in this
     *            location object
     */
    public void getFrontLocation(final Location targetLoc) {
        targetLoc.set(loc);
        targetLoc.moveSC(character.getDirection());
    }

    /**
     * Get the current location of the character.
     * 
     * @return The current location of the character
     */
    public Location getLocation() {
        return loc;
    }

    /**
     * Get the movement handler of the player character that allows controls the
     * movement of the player.
     * 
     * @return the movement handler
     */
    public PlayerMovement getMovementHandler() {
        return movementHandler;
    }

    /**
     * Get the path to the player directory.
     * 
     * @return The path to the player directory
     */
    public File getPath() {
        return path;
    }

    /**
     * Get the current ID of the players character.
     * 
     * @return The ID of the player character
     */
    public long getPlayerId() {
        return playerId;
    }

    /**
     * Get the sound listener of the player in order to perform the needed
     * updates of its position and direction.
     * 
     * @return the sound listener of the player
     */
    public SoundListener getSoundListener() {
        return listener;
    }

    /**
     * Check if the player character is currently looking at a specified
     * character.
     * 
     * @param chara The character the player character could look at
     * @return true if the player character is looking at the character
     */
    public boolean isLookingAt(final Char chara) {
        final int dist =
            Math.abs(character.getDirection()
                - getLocation().getDirection(chara.getLocation()));
        return (dist < MINIMUM_LOOKING_AT_RANGE)
            || (dist > MAXIMUM_LOOKING_AT_RANGE);
    }

    /**
     * Determines whether the character is currently moving.
     * 
     * @return <code>true</code> if the character is moving
     * @deprecated directly get the information from the movement handler
     */
    @Deprecated
    public boolean isMoving() {
        return movementHandler.isMoving();
    }

    /**
     * Check of a position in server coordinates is on the screen of the player.
     * 
     * @param testLoc The location that shall be checked.
     * @param tolerance an additional tolerance added to the default clipping
     *            distance
     * @return true if the position is within the clipping distance and the
     *         tolerance
     */
    public boolean isOnScreen(final Location testLoc, final int tolerance) {
        final int limit = CLIP_DISTANCE + tolerance;
        return loc.getDistance(testLoc) < limit;
    }

    /**
     * Check if a ID is the ID of the player.
     * 
     * @param checkId the ID to be checked
     * @return true if it is the player, false if not
     */
    public boolean isPlayer(final long checkId) {
        return playerId == checkId;
    }

    /**
     * Change the location of the character. This will instantly change the
     * position on the map where the client is shown. Calling this function is a
     * result of any warp requested by the server.
     * 
     * @param newLoc new location of the character on the map
     */
    public void setLocation(final Location newLoc) {
        if (loc.equals(newLoc)) {
            return;
        }

        final boolean levelChange = (newLoc.getScZ() != loc.getScZ());

        // set logical location
        movementHandler.cancelAutoWalk();
        loc.set(newLoc);
        character.setLocation(newLoc);

        // clear away invisible characters
        if (levelChange) {
            Game.getPeople().clear();
        } else {
            Game.getPeople().clipCharacters();
        }

        listener.setLocation(newLoc);
    }

    /**
     * Set the value of the character attribute "perception".
     * 
     * @param value The new perception value
     */
    public void setPerception(final int value) {
        perception = value;
    }

    /**
     * Set the ID of the players character. This also causes a introducing of
     * the player character to the rest of the client, so the chat box for
     * example is able to show the name of the character without additional
     * affords.
     * 
     * @param newPlayerId the new ID of the player
     */
    public void setPlayerID(final long newPlayerId) {
        playerId = newPlayerId;
        character.setCharId(playerId);
        // character.setAppearance(appear);

        while (!Game.getPeople().isRunning()) {
            try {
                Thread.sleep(50);
            } catch (final InterruptedException e) {
                // does not matter
            }
        }
        Game.getPeople().introduce(playerId, name);

    }

    /**
     * Then this instance of player is removed its content needs to removed
     * correctly as well.
     */
    public void shutdown() {
        Game.getPeople().setPlayerCharacter(null);
        character.recycle();
        movementHandler.shutdown();
    }

    /**
     * Update the location that is bound to this player. This does not have any
     * side effects but the location of the player and the sound listener
     * changed.
     * 
     * @param newLoc the new location of the player
     */
    public void updateLocation(final Location newLoc) {
        if (loc.equals(newLoc)) {
            return;
        }

        loc.set(newLoc);
    }

    /**
     * Check how good the player character is able to see the target character.
     * 
     * @param chara The character that is checked
     * @return the visibility of the character in percent
     */
    protected int canSee(final Char chara) {
        if (isPlayer(chara.getCharId())) {
            return Char.VISIBILITY_MAX;
        }

        int visibility = Char.VISIBILITY_MAX;
        final Avatar avatar = chara.getAvatar();
        if (avatar != null) {
            visibility = avatar.getVisibility();
        }
        visibility += chara.getVisibilityBonus();

        return getVisibility(chara.getLocation(), visibility);
    }

    /**
     * Check if the player character can see a location on the map at all.
     * 
     * @param targetLoc The location that is checked for visibility
     * @return true if the location is visible
     */
    protected boolean canSee(final Location targetLoc) {
        return getVisibility(targetLoc, Char.VISIBILITY_MAX) > 0;
    }

    /**
     * Check if a location is at the same level as the player.
     * 
     * @param checkLoc The location that shall be checked
     * @return true if the location is at the same level as the player
     */
    protected boolean isBaseLevel(final Location checkLoc) {
        return loc.getScZ() == checkLoc.getScZ();
    }

    /**
     * Get the visibility of a target location for the players character.
     * 
     * @param targetLoc The location that is checked for visibility
     * @param limit The maximum value for the visibility
     * @return The visibility of the target location
     */
    private int getVisibility(final Location targetLoc, final int limit) {
        // target is at same level or above char
        final boolean visible =
            targetLoc.getScZ() <= character.getLocation().getScZ();
        // calculate line-of-sight
        if (visible && (character.getLocation().getDistance(targetLoc) < 30)) {
            // calculate intervening fields.
            // note that the order of fields may be inverted
            final Bresenham line = Bresenham.getInstance();
            line.calculate(loc, targetLoc);
            // examine line without start and end point
            final int length = line.getLength() - 1;
            MapTile tile = null;
            final GameMap map = Game.getMap();
            final Point point = new Point();
            int coverage =
                Game.getWeather().getVisiblity()
                    - ((perception - PERCEPTION_AVERAGE) * PERCEPTION_COVER_SHARE);
            // skip tile the character is standing on
            for (int i = 1; i < length; i++) {
                line.getPoint(i, point);
                tile = map.getMapAt(point.x, point.y, loc.getScZ());
                if (tile != null) {
                    coverage += tile.getCoverage();

                    if (coverage >= limit) {
                        return 0;
                    }
                }
                // include distance in calulation 1% per tile
                coverage++;
            }
            final int quality = limit - coverage;
            if (quality > (Char.VISIBILITY_MAX / 2)) {
                return Char.VISIBILITY_MAX;
            }
            return quality * 2;
        }
        return 0;
    }

    /**
     * Update the sound listener of this player.
     */
    private void updateListener() {
        float effVol = 0.f;
        if (IllaClient.getCfg().getBoolean(CFG_SOUND_ON)) {
            effVol =
                IllaClient.getCfg().getInteger(CFG_SOUND_VOL) / MAX_CLIENT_VOL;
        }

        float musVol = 0.f;
        if (IllaClient.getCfg().getBoolean(CFG_MUSIC_ON)) {
            musVol =
                IllaClient.getCfg().getInteger(CFG_MUSIC_VOL) / MAX_CLIENT_VOL;
        }

        if ((effVol > FastMath.FLT_EPSILON) || (musVol > FastMath.FLT_EPSILON)) {
            listener.setEffectVolume(effVol);
            listener.setMusicVolume(musVol);
            listener.activate();
        } else {
            listener.deactivate();
        }
    }
}
