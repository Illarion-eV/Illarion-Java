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

import java.io.File;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javolution.context.ObjectFactory;
import javolution.text.TextBuilder;
import javolution.util.FastTable;

import org.apache.log4j.Logger;

import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

import illarion.client.IllaClient;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.RequestAppearanceCmd;
import illarion.client.util.Lang;
import illarion.client.util.NamesTable;
import illarion.client.util.SessionMember;

import illarion.common.config.Config;
import illarion.common.config.ConfigChangeListener;
import illarion.common.util.Location;
import illarion.common.util.Reusable;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

/**
 * Handles all characters known to the client but the player character.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class People implements SessionMember, TableLoaderSink,
    ConfigChangeListener {
    /**
     * This helper class is used to create a procedure that checks and updates
     * the visibility for every character in the list.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class CheckVisibilityProcedure implements
        TObjectProcedure<Char> {
        /**
         * A public constructor so the parent class is able to create a instance
         * properly.
         */
        public CheckVisibilityProcedure() {
            // nothing to do
        }

        /**
         * This function is executed for each character on the list and updates
         * the visibility value.
         */
        @Override
        public boolean execute(final Char character) {
            character.setVisible(Game.getPlayer().canSee(character));
            return true;
        }
    }

    /**
     * This helper class the the procedure that helps at clipping the characters
     * on the map. Its used to check all characters on the map and remove those
     * that moved out of visible range.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private final class ClipCharactersProcedure implements
        TObjectProcedure<Char> {
        /**
         * A public constructor to allow the parent class to create a proper
         * instance.
         */
        public ClipCharactersProcedure() {
            // nothing to do
        }

        /**
         * This method is executed for each character on the list. It checks if
         * the character is still in visible range and deletes the character in
         * case not.
         */
        @Override
        public boolean execute(final Char character) {
            if (!Game.getPlayer().isOnScreen(character.getLocation(), 0)) {
                addCharacterToRemoveList(character);
            }
            return true;
        }
    }

    /**
     * This helper class is used to fetch a character from a location on the
     * map.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class GetCharacterAtProcedure implements Reusable,
        TObjectProcedure<Char> {
        /**
         * This is the factory class of the parent procedure that provides the
         * instances of the procedure in a proper thread safe way.
         * 
         * @author Martin Karing
         * @since 1.22
         * @version 1.22
         */
        private static final class GetCharacterAtProcedureFactory extends
            ObjectFactory<GetCharacterAtProcedure> {
            /**
             * Public constructor so the parent class is able to create a
             * instance.
             */
            public GetCharacterAtProcedureFactory() {
                // nothing to do
            }

            /**
             * Get a new instance of the class managed by this factory.
             * 
             * @return the new class
             */
            @Override
            protected GetCharacterAtProcedure create() {
                return new GetCharacterAtProcedure();
            }

        }

        /**
         * The used instance of the factory of this class. This factory creates
         * and stores the instances of this class.
         */
        private static final GetCharacterAtProcedureFactory FACTORY =
            new GetCharacterAtProcedureFactory();

        /**
         * The character that is found on the location.
         */
        private Char resultChar;

        /**
         * The location this procedure is excepted to search on.
         */
        private Location searchLoc;

        /**
         * A public constructor to allow the instances of this classes to be
         * created properly.
         */
        public GetCharacterAtProcedure() {
            // nothing to do
        }

        /**
         * Get a instance of this procedure. This instance is either newly
         * created or a old reused one. In any case the instance is prepared to
         * use the location set with the parameter.
         * 
         * @param loc the location the returned instance is supposed to use as
         *            search instance
         * @return the instance that is prepared to be used now
         */
        public static GetCharacterAtProcedure getInstance(final Location loc) {
            final GetCharacterAtProcedure result = FACTORY.object();
            result.searchLoc = loc;
            return result;
        }

        /**
         * Execute this procedure at one character. This will check if the
         * currently checked character is on the location the search is
         * performed on and cancels the search in case the character on the
         * location is found.
         */
        @Override
        public boolean execute(final Char character) {
            if (character.getLocation().equals(searchLoc)) {
                resultChar = character;
                return false;
            }
            return true;
        }

        /**
         * Get the result of the search operation.
         * 
         * @return the character found on the location or <code>null</code> in
         *         case no character is found
         */
        public Char getResult() {
            return resultChar;
        }

        /**
         * Recycle the instance of this procedure so it can be reused later.
         */
        @Override
        public void recycle() {
            reset();
            FACTORY.recycle(this);
        }

        /**
         * Reset this instance and prepare it for later reuse. This clears the
         * working references that are stored in this instance.
         */
        @Override
        public void reset() {
            searchLoc = null;
            resultChar = null;
        }
    }

    /**
     * This helper class is used to recycle all load character objects. This
     * happens in case the playing session ends.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class RecycleCharProcedure implements
        TObjectProcedure<Char> {
        /**
         * Public constructor to allow the parent class to create a instance of
         * this class properly.
         */
        public RecycleCharProcedure() {
            // nothing to do
        }

        /**
         * Execute the recycle on every character in the list this procedure is
         * called on.
         */
        @Override
        public boolean execute(final Char chara) {
            chara.recycle();
            return false;
        }
    }

    /**
     * This procedure is used to support the light updates. The method
     * {@link illarion.client.world.People#updateLight()} uses the class to
     * trigger a light update for all chars in the list.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class UpdateLightProcedure implements
        TObjectProcedure<Char> {
        /**
         * Public constructor so the parent class is able to create a proper
         * instance.
         */
        public UpdateLightProcedure() {
            // nothing to do
        }

        /**
         * This method is executed for every entry in the list. In this case for
         * the currently called entry a light update is triggered.
         */
        @Override
        public boolean execute(final Char character) {
            character.updateLight(Char.LIGHT_UPDATE);
            return true;
        }
    }

    /**
     * This procedure is used to support the name updates. The method
     * {@link illarion.client.world.People#updateLight()} uses the class to
     * trigger a name update for all chars in the list.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class UpdateNameProcedure implements
        TObjectProcedure<Char> {
        /**
         * The people instance that created this instance.
         */
        private final People parentPeople;

        /**
         * Public constructor so the parent class is able to create a proper
         * instance.
         * 
         * @param parent the parent class that is controlled by this procedure
         */
        public UpdateNameProcedure(final People parent) {
            parentPeople = parent;
        }

        /**
         * This method is executed for every entry in the list. In this case for
         * the currently called entry a name update is triggered.
         */
        @Override
        public boolean execute(final Char character) {
            parentPeople.updateName(character);
            return true;
        }
    }

    /**
     * Settings value for showing full names.
     */
    public static final int NAME_LONG = 2;

    /**
     * Settings value for showing shorten names.
     */
    public static final int NAME_SHORT = 1;

    /**
     * The key for the configuration where the name mode is stored.
     */
    @SuppressWarnings("nls")
    private static final String CFG_NAMEMODE_KEY = "showNameMode";

    /**
     * The key for the configuration where the current show ID flag is stored.
     */
    @SuppressWarnings("nls")
    private static final String CFG_SHOWID_KEY = "showIDs";

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
     * This is the format string that is displayed in the {@link #toString()}
     * function.
     */
    @SuppressWarnings("nls")
    private static final String TO_STRING_TEXT =
        "People Manager - %d$1 characters in storage";

    /**
     * The list of visible characters.
     */
    private final TLongObjectHashMap<Char> chars;

    /**
     * The lock that is used to secure the chars table properly.
     */
    private final ReentrantReadWriteLock charsLock;

    /**
     * This is the instance of
     * {@link illarion.client.world.People.CheckVisibilityProcedure} that is
     * used to update the visibility values of every character.
     */
    private final CheckVisibilityProcedure checkVisibilityHelper;

    /**
     * The instance of
     * {@link illarion.client.world.People.ClipCharactersProcedure} that is used
     * to clip the characters on the map.
     */
    private final ClipCharactersProcedure clipCharactersHelper;

    /**
     * Names of known characters.
     */
    private NamesTable names = null;

    /**
     * The character of the player. This one is handled seperated of the rest of
     * the characters.
     */
    private Char playerChar;

    /**
     * A list of characters that are going to be removed.
     */
    private final List<Char> removalList;

    /**
     * Check if the session of the people is already running properly.
     */
    private boolean running;

    /**
     * Settings if the ID of characters shall be shown or generic descriptors
     * like "Man", "Woman", etc.
     */
    private boolean showIDs;

    /**
     * Contains the status how the names of the characters shall be shown.
     */
    private int showMapNames;

    /**
     * The instance of the
     * {@link illarion.client.world.People.UpdateLightProcedure} class that is
     * used to trigger a light update for all characters.
     */
    private final UpdateLightProcedure updateLightHelper;

    /**
     * The instance of the
     * {@link illarion.client.world.People.UpdateNameProcedure} class that is
     * used to trigger a name update for all characters.
     */
    private final UpdateNameProcedure updateNameHelper;

    /**
     * Default constructor. Sets up all needed base variables to init the class.
     */
    public People() {
        updateLightHelper = new UpdateLightProcedure();
        showMapNames = IllaClient.getCfg().getInteger(CFG_NAMEMODE_KEY);
        showIDs = IllaClient.getCfg().getBoolean(CFG_SHOWID_KEY);
        IllaClient.getCfg().addListener(CFG_NAMEMODE_KEY, this);
        IllaClient.getCfg().addListener(CFG_SHOWID_KEY, this);

        removalList = new FastTable<Char>();
        chars = new TLongObjectHashMap<Char>();
        charsLock = new ReentrantReadWriteLock();
        clipCharactersHelper = new ClipCharactersProcedure();
        checkVisibilityHelper = new CheckVisibilityProcedure();
        updateNameHelper = new UpdateNameProcedure(this);
    }

    /**
     * This function checks a argument against <code>null</code> and throws a
     * exception in case the argument is <code>null</code>
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
     * Get a character on the client screen. If the character is not available,
     * its created and the appearance is requested from the server.
     * 
     * @param id the ID of the character
     * @return the character that was requested
     */
    public Char accessCharacter(final long id) {
        final Char chara = getCharacter(id);
        if (chara == null) {
            return createNewCharacter(id);
        }
        return chara;
    }

    /**
     * Add a character to the list of characters that are going to be removed at
     * the next run of {@link #cleanRemovalList()}.
     * 
     * @param removeChar the character that is going to be removed
     */
    public void addCharacterToRemoveList(final Char removeChar) {
        throwNullException(removeChar);
        throwPlayerCharacter(removeChar);
        removalList.add(removeChar);
    }

    /**
     * Clean up the removal list. All character from the list of character to
     * remove get removed and recycled. The list is cleared after calling this
     * function.
     */
    public void cleanRemovalList() {
        charsLock.writeLock().lock();
        try {
            if (!removalList.isEmpty()) {
                final int count = removalList.size();
                for (int i = 0; i < count; i++) {
                    removeCharacter(removalList.get(i).getCharId());
                }
                removalList.clear();
            }
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Act in case a change of the configuration occurred. This causes all
     * values dependent on the configuration to fit the configuration again.
     */
    @Override
    public void configChanged(final Config cfg, final String key) {
        throwNullException(cfg);
        throwNullException(key);

        if (key.equals(CFG_NAMEMODE_KEY)) {
            showMapNames = cfg.getInteger(CFG_NAMEMODE_KEY);
            chars.forEachValue(updateNameHelper);
            return;
        }
        if (key.equals(CFG_SHOWID_KEY)) {
            showIDs = cfg.getBoolean(CFG_SHOWID_KEY);
            return;
        }
    }

    /**
     * End a playing session by removing all characters properly and saving the
     * table files.
     */
    @Override
    public void endSession() {
        running = false;

        clear();

        names.saveTable();
        names = null;
    }

    /**
     * Get a character out of the list of the known characters.
     * 
     * @param id ID of the requested character
     * @return the character or null if it does not exist
     */
    public Char getCharacter(final long id) {
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

        final GetCharacterAtProcedure procedure =
            GetCharacterAtProcedure.getInstance(loc);

        charsLock.readLock().lock();
        try {
            chars.forEachValue(procedure);
        } finally {
            charsLock.readLock().unlock();
        }

        final Char result = procedure.getResult();
        procedure.recycle();
        return result;
    }

    /**
     * Check of there is a character below the cursor currently and get it.
     * 
     * @param x X-Coordinate of the mouse on the screen
     * @param y Y-Coordinate of the mouse on the screen
     * @return the character below the cursor or null
     */
    @Deprecated
    public Interactive getComponentAt(final int x, final int y) {
        return null;
    }

    /**
     * Get the current settings how the name of the characters is shown.
     * 
     * @return the current settings how the names are shown. Possible values are
     *         {@link #NO_NAME}, {@link #NAME_SHORT} or {@link #NAME_LONG}
     */
    public int getShowMapNames() {
        return showMapNames;
    }

    @Override
    public void initSession() {
        running = false;
    }

    /**
     * Introduce a character and store the name in the table or overwrite a
     * existing entry for this character.
     * 
     * @param id the ID of the character that shall get a name
     * @param name the name that the character shall get
     */
    public void introduce(final long id, final String name) {
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
     * Check if the people session is already started properly.
     * 
     * @return <code>true</code> in case the class is started
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Show the dialog for naming a character and fill in the name of the
     * character in case its known already. The dialog is not shown for monsters
     * and NPCs.
     * 
     * @param chara The character that shall get named
     */
    @SuppressWarnings("nls")
    public void nameCharacter(final Char chara) {
        throwNullException(chara);
        throwPlayerCharacter(chara);

        final long charID = chara.getCharId();

        if (chara.canHaveName()) {
            String name = names.getName(charID);
            // remove markers
            if ((name != null) && name.startsWith("'")) {
                name = name.substring(1, name.length() - 1);
            }

            // Gui.getInstance().getNameRequest()
            // .request(name, new TextResponse() {
            // @Override
            // public boolean checkText(final String text) {
            // return text.length() > 2;
            // }

            // @Override
            // public void textCancelled() {
            // // nothing to do
            // }

            // @Override
            // public void textConfirmed(final String text) {
            // introduce(charID, "'" + text + "'");
            // }
            // });
        }
    }

    /**
     * Process a record from the table containing the names and add the name to
     * the name storage of this class.
     * 
     * @param line current line that is handled
     * @param loader table loader that loads the name table
     * @return true at all times
     */
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        throwNullException(loader);

        names.addName(loader.getLong(0), loader.getString(1));

        return true;
    }

    /**
     * Remove a character from the game list and recycle the character reference
     * for later usage. Also clean up everything related to this character such
     * as the attacking marker.
     * 
     * @param id the ID of the character that shall be removed
     */
    public void removeCharacter(final long id) {
        throwPlayerCharacter(id);

        charsLock.writeLock().lock();
        try {
            final Char chara = chars.get(id);
            if (chara != null) {
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
     * Show a dialog that offers the possibility to send a GM report about a
     * special character to the gms.
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
        // cmd.setText(msg.toString());
        // Game.getNet().sendCommand(cmd);

        // }
        // });

    }

    /**
     * Save the table that stores the names of the characters known to the
     * current player character.
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
     * @see illarion.client.world.People#NO_NAME
     * @see illarion.client.world.People#NAME_SHORT
     * @see illarion.client.world.People#NAME_LONG
     */
    public void setShowMapNames(final int newShowMapNames) {
        showMapNames = newShowMapNames;
    }

    @Override
    public void shutdownSession() {
        // nothing to do
    }

    /**
     * Start a new session by loading the required name files.
     */
    @Override
    @SuppressWarnings({ "nls", "unused" })
    public void startSession() {
        final File nameTable =
            new File(Game.getPlayer().getPath(), "names.tbl");
        final File nameTableNew =
            new File(Game.getPlayer().getPath(), "names.dat");
        names = new NamesTable(nameTableNew);

        if (nameTable.exists() && nameTable.isFile()) {
            new TableLoader(nameTable, this);
            if (!nameTable.delete()) {
                LOGGER.error("Failed to delete old name table.");
            }
        }

        chars.clear();
        running = true;
    }

    /**
     * Change the name mode and toggle between no name, short name and full
     * name.
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
        return String.format(TO_STRING_TEXT, Integer.valueOf(chars.size()));
    }

    /**
     * Add a character to the list of known characters.
     * 
     * @param chara the character that shall be added
     */
    protected void addCharacter(final Char chara) {
        throwPlayerCharacter(chara);

        charsLock.writeLock().lock();
        try {
            chars.put(chara.getCharId(), chara);
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Check the visibility for all characters currently on the screen.
     */
    protected void checkVisibility() {
        charsLock.readLock().lock();
        try {
            chars.forEachValue(checkVisibilityHelper);
        } finally {
            charsLock.readLock().unlock();
        }
    }

    /**
     * Clear the list of characters and recycle all of them.
     */
    protected void clear() {
        if (CombatHandler.getInstance().isAttacking()) {
            CombatHandler.getInstance().standDown();
        }
        charsLock.writeLock().lock();
        try {
            cleanRemovalList();
            chars.forEachValue(new RecycleCharProcedure());
            chars.clear();
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Check all known characters if they are outside of the screen and hide
     * them from the screen. Save them still to the characters that are known to
     * left the screen.
     */
    protected void clipCharacters() {
        charsLock.writeLock().lock();
        try {
            chars.forEachValue(clipCharactersHelper);
            cleanRemovalList();
        } finally {
            charsLock.writeLock().unlock();
        }
    }

    /**
     * Force update of light values.
     */
    protected void updateLight() {
        if (playerChar != null) {
            updateLightHelper.execute(playerChar);
        }

        charsLock.readLock().lock();
        try {
            chars.forEachValue(updateLightHelper);
        } finally {
            charsLock.readLock().unlock();
        }
    }

    /**
     * Update the name of a character, regarding the long or short name
     * settings.
     * 
     * @param chara the character that shall be updated
     */
    protected void updateName(final Char chara) {
        if (showMapNames == NAME_SHORT) {
            chara.setName(getShortName(chara.getCharId()));
        } else {
            chara.setName(getName(chara.getCharId()));
        }
    }

    /**
     * This function creates a new character and requests the required
     * informations from the server.
     * 
     * @param id the ID of the character to be created
     * @return the created character
     */
    private Char createNewCharacter(final long id) {
        final Char chara = Char.create();
        chara.setCharId(id);
        updateName(chara);

        addCharacter(chara);

        // request appearance from server if char is not known
        final RequestAppearanceCmd cmd =
            (RequestAppearanceCmd) CommandFactory.getInstance().getCommand(
                CommandList.CMD_REQUEST_APPEARANCE);
        cmd.request(id);
        Game.getNet().sendCommand(cmd);
        return chara;
    }

    /**
     * Get the name of the character. Returns the last known name or someone
     * along with the ID of the character, in case its set that the IDs shall be
     * shown.
     * 
     * @param id ID of the character thats name is wanted
     * @return the name of the character or null
     */
    @SuppressWarnings("nls")
    private String getName(final long id) {
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
    private String getShortName(final long id) {
        String name = null;

        if (names != null) {
            name = names.getName(id);
        }

        // return the id
        if (name == null) {
            if (showIDs) {
                name = Long.toString(id);
            }
        } else { // return text up to first blank
            final int pos = name.indexOf(" ");
            if (pos > 0) {
                name = name.substring(0, pos);
            }
        }
        return name;
    }

    /**
     * Check if the location is the location of the player character.
     * 
     * @param id the location to check
     * @return <code>true</code> in case the player character is set and its
     *         location equals the location supplied by the argument
     */
    private boolean isPlayerCharacter(final Location loc) {
        return ((playerChar != null) && (playerChar.getLocation().equals(loc)));
    }

    /**
     * Check if the ID is the ID of the player character.
     * 
     * @param id the id to check
     * @return <code>true</code> in case the player character is set and its ID
     *         equals the ID supplied by the argument
     */
    private boolean isPlayerCharacter(final long id) {
        return ((playerChar != null) && (playerChar.getCharId() == id));
    }

    /**
     * This function throws a {@link java.lang.IllegalArgumentException} in case
     * the character supplied with the argument is the player character.
     * 
     * @param chara the character to check
     * @throws IllegalArgumentException in case the character in the argument is
     *             the player character
     */
    private void throwPlayerCharacter(final Char chara) {
        throwPlayerCharacter(chara.getCharId());
    }

    /**
     * This function throws a {@link java.lang.IllegalArgumentException} in case
     * the ID suppled by the argument is the ID of the player character
     * 
     * @param chara the ID to test
     * @throws IllegalArgumentException in case the argument contains the ID of
     *             the player character
     */
    @SuppressWarnings("nls")
    private void throwPlayerCharacter(final long id) {
        if (isPlayerCharacter(id)) {
            throw new IllegalArgumentException(
                "The player character can't be used here.");
        }
    }

}
