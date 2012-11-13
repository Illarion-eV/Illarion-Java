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
import illarion.client.Login;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.RequestAppearanceCmd;
import illarion.client.util.Lang;
import illarion.client.util.NamesTable;
import illarion.client.world.events.CharRemovedEvent;
import illarion.common.config.Config;
import illarion.common.config.ConfigChangeListener;
import illarion.common.types.CharacterId;
import illarion.common.types.Location;
import illarion.common.util.DirectoryManager;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;
import javolution.text.TextBuilder;
import javolution.util.FastTable;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Handles all characters known to the client but the player character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class People
        implements TableLoaderSink, ConfigChangeListener {
    /**
     * The key for the configuration where the name mode is stored.
     */
    @SuppressWarnings("nls")
    public static final String CFG_NAMEMODE_KEY = "showNameMode";

    /**
     * The key for the configuration where the current show ID flag is stored.
     */
    @SuppressWarnings("nls")
    public static final String CFG_SHOWID_KEY = "showIDs";

    /**
     * Default Name of a enemy.
     */
    @SuppressWarnings("nls")
    private static final String ENEMY = "'" + Lang.getMsg("chat.enemy") + "'";

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(People.class);

    /**
     * Settings value for showing full names.
     */
    public static final int NAME_LONG = 2;

    /**
     * Settings value for showing shorten names.
     */
    public static final int NAME_SHORT = 1;

    /**
     * This is the format string that is displayed in the {@link #toString()} function.
     */
    @SuppressWarnings("nls")
    private static final String TO_STRING_TEXT = "People Manager - %d$1 characters in storage";

    /**
     * This function checks a argument against <code>null</code> and throws a exception in case the argument is
     * <code>null</code>
     *
     * @param arg the argument to test
     * @throws NullPointerException in case the argument is <code>null</code>.
     */
    @SuppressWarnings("nls")
    private static void throwNullException(final Object arg) {
        if (arg == null) {
            throw new NullPointerException("Argument must not be null.");
        }
    }

    /**
     * The list of visible characters.
     */
    private final Map<CharacterId, Char> chars;

    /**
     * The lock that is used to secure the chars table properly.
     */
    private final ReentrantReadWriteLock charsLock;

    /**
     * Names of known characters.
     */
    private NamesTable names;

    /**
     * The character of the player. This one is handled seperated of the rest of the characters.
     */
    private Char playerChar;

    /**
     * A list of characters that are going to be removed.
     */
    private final List<Char> removalList;

    /**
     * Settings if the ID of characters shall be shown or generic descriptors like "Man", "Woman", etc.
     */
    private boolean showIDs;

    /**
     * Contains the status how the names of the characters shall be shown.
     */
    private int showMapNames;

    /**
     * Default constructor. Sets up all needed base variables to init the class.
     */
    public People() {
        showMapNames = IllaClient.getCfg().getInteger(CFG_NAMEMODE_KEY);
        showIDs = IllaClient.getCfg().getBoolean(CFG_SHOWID_KEY);
        IllaClient.getCfg().addListener(CFG_NAMEMODE_KEY, this);
        IllaClient.getCfg().addListener(CFG_SHOWID_KEY, this);

        removalList = new FastTable<Char>();
        chars = new HashMap<CharacterId, Char>();
        charsLock = new ReentrantReadWriteLock();

        final File playerDir = new File(DirectoryManager.getInstance().getUserDirectory(),
                Login.getInstance().getLoginCharacter());
        final File nameTable = new File(playerDir, "names.tbl");
        final File nameTableNew = new File(playerDir, "names.dat");
        names = new NamesTable(nameTableNew);

        if (nameTable.exists() && nameTable.isFile()) {
            new TableLoader(nameTable, this);
            if (!nameTable.delete()) {
                LOGGER.error("Failed to delete old name table.");
            }
        }
    }

    /**
     * Get a character on the client screen. If the character is not available, its created and the appearance is
     * requested from the server.
     *
     * @param id the ID of the character
     * @return the character that was requested
     */
    public Char accessCharacter(final CharacterId id) {
        final Char chara = getCharacter(id);
        if (chara == null) {
            return createNewCharacter(id);
        }
        return chara;
    }

    /**
     * Add a character to the list of known characters.
     *
     * @param chara the character that shall be added
     */
    void addCharacter(final Char chara) {
        throwPlayerCharacter(chara);

        charsLock.writeLock().lock();
        try {
            chars.put(chara.getCharId(), chara);
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Add a character to the list of characters that are going to be removed at the next run of {@link
     * #cleanRemovalList()}.
     *
     * @param removeChar the character that is going to be removed
     */
    public void addCharacterToRemoveList(final Char removeChar) {
        throwNullException(removeChar);
        throwPlayerCharacter(removeChar);
        removalList.add(removeChar);
    }

    /**
     * Check the visibility for all characters currently on the screen.
     */
    void checkVisibility() {
        charsLock.readLock().lock();
        try {
            for (final Char character : chars.values()) {
                character.setVisible(World.getPlayer().canSee(character));
            }
        } finally {
            charsLock.readLock().unlock();
        }
    }

    /**
     * Clean up the removal list. All character from the list of character to remove get removed and recycled. The list
     * is cleared after calling this function.
     */
    public void cleanRemovalList() {
        charsLock.writeLock().lock();
        try {
            if (!removalList.isEmpty()) {
                for (final Char removeChar : removalList) {
                    removeCharacter(removeChar.getCharId());
                }
                removalList.clear();
            }
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Clear the list of characters and recycle all of them.
     */
    void clear() {
        if (CombatHandler.getInstance().isAttacking()) {
            CombatHandler.getInstance().standDown();
        }
        charsLock.writeLock().lock();
        try {
            cleanRemovalList();
            for (final Char character : chars.values()) {
                character.recycle();
            }
            chars.clear();
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Check all known characters if they are outside of the screen and hide them from the screen. Save them still to
     * the characters that are known to left the screen.
     */
    void clipCharacters() {
        charsLock.writeLock().lock();
        try {

            for (final Char character : chars.values()) {
                if (!World.getPlayer().isOnScreen(character.getLocation(), 0)) {
                    addCharacterToRemoveList(character);
                }
            }
            cleanRemovalList();
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Act in case a change of the configuration occurred. This causes all values dependent on the configuration to fit
     * the configuration again.
     */
    @Override
    public void configChanged(final Config cfg, final String key) {
        throwNullException(cfg);
        throwNullException(key);

        if (key.equals(CFG_NAMEMODE_KEY)) {
            showMapNames = cfg.getInteger(CFG_NAMEMODE_KEY);

            for (final Char character : chars.values()) {
                updateName(character);
            }
            return;
        }
        if (key.equals(CFG_SHOWID_KEY)) {
            showIDs = cfg.getBoolean(CFG_SHOWID_KEY);
        }
    }

    /**
     * This function creates a new character and requests the required information from the server.
     *
     * @param id the ID of the character to be created
     * @return the created character
     */
    private Char createNewCharacter(final CharacterId id) {
        final Char chara = Char.create();
        chara.setCharId(id);
        updateName(chara);

        addCharacter(chara);

        // request appearance from server if char is not known
        final RequestAppearanceCmd cmd = CommandFactory.getInstance().getCommand(CommandList.CMD_REQUEST_APPEARANCE,
                RequestAppearanceCmd.class);
        cmd.request(id);
        World.getNet().sendCommand(cmd);
        return chara;
    }

    /**
     * Get a character out of the list of the known characters.
     *
     * @param id ID of the requested character
     * @return the character or null if it does not exist
     */
    public Char getCharacter(final CharacterId id) {
        if (isPlayerCharacter(id)) {
            return playerChar;
        }

        charsLock.readLock().lock();
        try {
            return chars.get(id);
        } finally {
            charsLock.readLock().unlock();
        }
    }

    /**
     * Get the character on a special location on the map.
     *
     * @param loc the location the character is searched at
     * @return the character or null if not found
     */
    public Char getCharacterAt(final Location loc) {
        throwNullException(loc);

        if (isPlayerCharacter(loc)) {
            return playerChar;
        }

        charsLock.readLock().lock();
        try {

            for (final Char character : chars.values()) {
                if (character.getLocation().equals(loc)) {
                    return character;
                }
            }
        } finally {
            charsLock.readLock().unlock();
        }

        return null;
    }

    /**
     * Get the name of the character. Returns the last known name or someone along with the ID of the character,
     * in case its set that the IDs shall be shown.
     *
     * @param id ID of the character who's name is wanted
     * @return the name of the character or null
     */
    @SuppressWarnings("nls")
    private String getName(final CharacterId id) {
        String name = null;

        if (names != null) {
            name = names.getName(id);
        }

        if (name == null) {
            if (showIDs) {
                final TextBuilder buildName = TextBuilder.newInstance();
                buildName.setLength(0);
                buildName.append(Lang.getMsg("someone"));
                buildName.append(' ');
                buildName.append('(');
                buildName.append(id);
                buildName.append(')');
                name = buildName.toString();
                TextBuilder.recycle(buildName);
            }
        }
        return name;
    }

    /**
     * Return the short version of the name of a character.
     *
     * @param id ID of the characters whos short name is wanted
     * @return the short version of the name of the character
     */
    @SuppressWarnings("nls")
    private String getShortName(final CharacterId id) {
        String name = null;

        if (names != null) {
            name = names.getName(id);
        }

        // return the id
        if (name == null) {
            if (showIDs) {
                name = Long.toString(id.getValue());
            }
        } else { // return text up to first blank
            final int pos = name.indexOf(' ');
            if (pos > 0) {
                name = name.substring(0, pos);
            }
        }
        return name;
    }

    /**
     * Get the current settings how the name of the characters is shown.
     *
     * @return the current settings how the names are shown. Possible values are {@link #NAME_SHORT} or {@link
     *         #NAME_LONG}
     */
    public int getShowMapNames() {
        return showMapNames;
    }

    /**
     * Introduce a character and store the name in the table or overwrite a existing entry for this character.
     *
     * @param id   the ID of the character that shall get a name
     * @param name the name that the character shall get
     */
    public void introduce(final CharacterId id, final String name) {
        throwNullException(name);

        // update character with his name
        final Char chara = getCharacter(id);
        if (chara != null) {
            chara.setName(name);
        }

        // add name to list of known names
        names.addName(id, name);
    }

    /**
     * Check if the location is the location of the player character.
     *
     * @param loc the location to check
     * @return <code>true</code> in case the player character is set and its location equals the location supplied by
     *         the argument
     */
    private boolean isPlayerCharacter(final Location loc) {
        return (playerChar != null) && playerChar.getLocation().equals(loc);
    }

    /**
     * Check if the ID is the ID of the player character.
     *
     * @param id the id to check
     * @return <code>true</code> in case the player character is set and its ID equals the ID supplied by the argument
     */
    private boolean isPlayerCharacter(final CharacterId id) {
        return (playerChar != null) && playerChar.getCharId().equals(id);
    }

    /**
     * Process a record from the table containing the names and add the name to the name storage of this class.
     *
     * @param line   current line that is handled
     * @param loader table loader that loads the name table
     * @return true at all times
     */
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        throwNullException(loader);

        try {
            names.addName(new CharacterId(loader.getLong(0)), loader.getString(1));
        } catch (final IllegalArgumentException e) {
            LOGGER.warn("Illegal entry in old name file found: " + Long.toString(loader.getLong(0)) + " - " +
                    loader.getString(1));
        }

        return true;
    }

    /**
     * Remove a character from the game list and recycle the character reference for later usage. Also clean up
     * everything related to this character such as the attacking marker.
     *
     * @param id the ID of the character that shall be removed
     */
    public void removeCharacter(final CharacterId id) {
        throwPlayerCharacter(id);

        charsLock.writeLock().lock();
        try {
            final Char chara = chars.get(id);
            if (chara != null) {
                EventBus.publish(new CharRemovedEvent(id));
                // cancel attack when character is removed
                if (CombatHandler.getInstance().isAttacking(chara)) {
                    CombatHandler.getInstance().standDown();
                }
                chars.remove(id);
                chara.recycle();
            }
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Show a dialog that offers the possibility to send a GM report about a special character to the gms.
     *
     * @param chara The character that shall be reported
     */
    public void reportCharacter(final Char chara) {
        chara.getCharId();

        // Gui.getInstance().getReportRequest().request("", new TextResponse() {
        // private static final int MINIMAL_TEXT_LENGTH = 20;

        // @Override
        // public boolean checkText(final String text) {
        // return text.length() > MINIMAL_TEXT_LENGTH;
        // }

        // @Override
        // public void textCancelled() {
        // // nothing to do
        // }

        // @Override
        // public void textConfirmed(final String text) {
        // final StringBuffer msg = new StringBuffer("!gm report ");
        // msg.append(Long.toString(charID));
        // msg.append(" ");

        // final String name = names.getName(charID);
        // if (name != null) {
        // msg.append(name);
        // msg.append(" ");
        // }
        // msg.append(text);

        // // send say command to server
        // final SayCmd cmd =
        // (SayCmd) CommandFactory.getInstance().getCommand(
        // CommandList.CMD_SAY);
        // cmd.setCharacterName(msg.toString());
        // Game.getNet().sendCommand(cmd);

        // }
        // });

    }

    /**
     * Save the table that stores the names of the characters known to the current player character.
     */
    public void saveNames() {
        names.saveTable();
    }

    /**
     * Set the character of the player.
     *
     * @param character the character of the player
     */
    public void setPlayerCharacter(final Char character) {
        playerChar = character;
    }

    /**
     * Set the settings how the name of the characters is shown.
     *
     * @param newShowMapNames the new state of the settings
     * @see People#NAME_SHORT
     * @see People#NAME_LONG
     */
    public void setShowMapNames(final int newShowMapNames) {
        showMapNames = newShowMapNames;
    }

    /**
     * This function throws a {@link IllegalArgumentException} in case the character supplied with the
     * argument is the player character.
     *
     * @param chara the character to check
     * @throws IllegalArgumentException in case the character in the argument is the player character
     */
    private void throwPlayerCharacter(final Char chara) {
        throwPlayerCharacter(chara.getCharId());
    }

    /**
     * This function throws a {@link IllegalArgumentException} in case the ID suppled by the argument is the
     * ID of the player character
     *
     * @param id the ID to test
     * @throws IllegalArgumentException in case the argument contains the ID of the player character
     */
    @SuppressWarnings("nls")
    private void throwPlayerCharacter(final CharacterId id) {
        if (isPlayerCharacter(id)) {
            throw new IllegalArgumentException("The player character can't be used here.");
        }
    }

    /**
     * Change the name mode and toggle between no name, short name and full name.
     */
    public void toggleNameMode() {
        if (showMapNames == NAME_LONG) {
            IllaClient.getCfg().set(CFG_NAMEMODE_KEY, NAME_SHORT);
        } else {
            IllaClient.getCfg().set(CFG_NAMEMODE_KEY, NAME_LONG);
        }
    }

    /**
     * Get the string representation of this instance.
     */
    @Override
    public String toString() {
        return String.format(TO_STRING_TEXT, chars.size());
    }

    /**
     * Force update of light values.
     */
    void updateLight() {
        if (playerChar != null) {
            playerChar.updateLight(Char.LIGHT_UPDATE);
        }

        charsLock.readLock().lock();
        try {
            synchronized (GameMap.LIGHT_LOCK) {
                for (final Char character : chars.values()) {
                    character.updateLight(Char.LIGHT_UPDATE);
                }
            }
        } finally {
            charsLock.readLock().unlock();
        }
    }

    /**
     * Update the name of a character, regarding the long or short name settings.
     *
     * @param chara the character that shall be updated
     */
    void updateName(final Char chara) {
        if (showMapNames == NAME_SHORT) {
            chara.setName(getShortName(chara.getCharId()));
        } else {
            chara.setName(getName(chara.getCharId()));
        }
    }

}
