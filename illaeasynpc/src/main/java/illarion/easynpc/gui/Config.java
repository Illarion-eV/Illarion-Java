/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyNPC Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyNPC Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.gui;

import illarion.common.bug.CrashReporter;
import illarion.common.config.ConfigChangedEvent;
import illarion.common.config.ConfigDialog;
import illarion.common.config.ConfigSystem;
import illarion.common.config.entries.CheckEntry;
import illarion.common.config.entries.DirectoryEntry;
import illarion.common.config.entries.NumberEntry;
import illarion.common.config.entries.SelectEntry;
import illarion.common.util.DirectoryManager;
import illarion.easynpc.Lang;
import javolution.util.FastTable;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SkinInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * This class is used to store and to publish the settings used by the editor GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Config {
    /**
     * The class path of the look and feel that is used by default.
     */
    @SuppressWarnings("nls")
    public static final String DEFAULT_LOOK_AND_FEEL = "org.pushingpixels.substance.api.skin.OfficeSilver2007Skin";

    /**
     * The amount of last opened files that shall be stored.
     */
    public static final int LAST_OPEN_FILES_COUNT = 10;

    /**
     * The key in the property file for the auto build flag
     */
    @SuppressWarnings("nls")
    private static final String AUTO_BUILD_KEY = "autoBuild";

    /**
     * The property key value for the easyNPC script folder.
     */
    @SuppressWarnings("nls")
    private static final String EASY_NPC_FOLDER = "easyNpcFolder";

    /**
     * The singleton instance of this class.
     */
    private static final Config INSTANCE = new Config();

    /**
     * The key of the last files list on the configuration file
     */
    @SuppressWarnings("nls")
    private static final String LAST_FILES_KEY = "lastFiles";

    /**
     * The key for the height of the stored window.
     */
    @SuppressWarnings("nls")
    private static final String LAST_WINDOW_H = "lastWindowH";

    /**
     * The key for the extended state of the stored window.
     */
    @SuppressWarnings("nls")
    private static final String LAST_WINDOW_STATE = "lastWindowState";

    /**
     * The key for the width of the stored window.
     */
    @SuppressWarnings("nls")
    private static final String LAST_WINDOW_W = "lastWindowW";

    /**
     * The key for the x coordinate of the stored window.
     */
    @SuppressWarnings("nls")
    private static final String LAST_WINDOW_X = "lastWindowX";

    /**
     * The key for the y coordinate of the stored window.
     */
    @SuppressWarnings("nls")
    private static final String LAST_WINDOW_Y = "lastWindowY";

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Config.class);

    /**
     * The property key value for the luaNPC script folder.
     */
    @SuppressWarnings("nls")
    private static final String LUA_NPC_FOLDER = "luaNpcFolder";

    /**
     * The property key for the list of files that were open at the last time
     * the editor was running.
     */
    @SuppressWarnings("nls")
    private static final String OPEN_FILES = "openFiles";

    /**
     * The property key of the value that describes the state of the split pane
     * in the editor view.
     */
    @SuppressWarnings("nls")
    private static final String SPLIT_STATE = "splitState";

    /**
     * Get the key that is used to store the amount the undo operations.
     */
    @SuppressWarnings("nls")
    private static final String UNDO_COUNT_KEY = "undoCount";

    /**
     * The property key value for the name of the look and feel shall be used.
     */
    @SuppressWarnings("nls")
    private static final String USED_LOOK_AND_FEEL = "usedLookAndFeel";

    /**
     * The property key for the flag that says of the editor shall use syntax
     * highlighting or not.
     */
    @SuppressWarnings("nls")
    private static final String USE_SYNTAX_HIGHLIGHT = "useSyntaxHighlight";
    /**
     * The property key value for the use window decoration flag.
     */
    @SuppressWarnings("nls")
    private static final String USE_WINDOW_DECO = "useWindowDeco";

    /**
     * The buffered state of the auto build value. This is used because the auto
     * build state is likely requested really often.
     */
    private boolean autoBuildState;

    /**
     * The properties that store the values of this configuration.
     */
    @Nullable
    private ConfigSystem cfg;

    /**
     * The last generated list of opened files. When this is set to
     * <code>null</code> the list is generated fresh once its requested the next
     * time.
     */
    @Nullable
    private File[] lastOpenedFilesBuffer;

    /**
     * If this is set to true, the application requires to restart to take all
     * settings done.
     */
    private boolean requireRestart;

    /**
     * Private constructor to ensure that no instance but the singleton instance
     * is created.
     */
    private Config() {
        AnnotationProcessor.process(this);
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance
     */
    @Nonnull
    public static Config getInstance() {
        return INSTANCE;
    }

    /**
     * This function determines the user data directory and requests the folder
     * to store the editor data in case it is needed. It also performs checks to
     * see if the folder is valid.
     *
     * @return a string with the path to the folder or null in case no folder is
     * set
     */
    @SuppressWarnings("nls")
    @Nonnull
    private static String checkFolder() {
        if (!DirectoryManager.getInstance().isDirectorySet(DirectoryManager.Directory.User)) {
            JOptionPane.showMessageDialog(null, "Installation ist fehlerhaft. Bitte neu ausführen.\n\n" +
                    "Installation is corrupted, please run it again.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        final File userDir = DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User);
        assert userDir != null;
        return userDir.getAbsolutePath();
    }

    /**
     * Prepend this file to the list of last opened files.
     *
     * @param file the file to prepend
     */
    public void addLastOpenedFile(@Nonnull final File file) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        cfg.set(LAST_FILES_KEY, file.getAbsolutePath() + File.pathSeparator + cfg.getString(LAST_FILES_KEY));
        lastOpenedFilesBuffer = null;
    }

    @EventTopicSubscriber(topic = USED_LOOK_AND_FEEL)
    public void onConfigChanged(@Nonnull final String topic, final ConfigChangedEvent event) {
        if (topic.equals(USED_LOOK_AND_FEEL)) {
            SubstanceLookAndFeel.setSkin(getLookAndFeel());
            final int count = MainFrame.getInstance().getOpenTabs();

            for (int i = 0; i < count; i++) {
                MainFrame.getInstance().getScriptEditor(i).resetEditorKit();
            }
            SwingUtilities.updateComponentTreeUI(MainFrame.getInstance());
        }
    }

    @Nonnull
    @SuppressWarnings("nls")
    public ConfigDialog createDialog() {
        if (cfg == null) {
            throw new IllegalStateException("Configuration system not initialized yet.");
        }
        final ConfigDialog dialog = new ConfigDialog();
        dialog.setConfig(cfg);
        dialog.setMessageSource(Lang.getInstance());

        ConfigDialog.Page page;
        page = new ConfigDialog.Page("illarion.easynpc.gui.config.generalTab");
        page.addEntry(new ConfigDialog.Entry("illarion.easynpc.gui.config.easyNpcFolderLabel",
                                             new DirectoryEntry(EASY_NPC_FOLDER, null)));
        page.addEntry(new ConfigDialog.Entry("illarion.easynpc.gui.config.luaFolderLabel",
                                             new DirectoryEntry(LUA_NPC_FOLDER, null)));
        page.addEntry(new ConfigDialog.Entry("illarion.easynpc.gui.config.maxUndoLabel",
                                             new NumberEntry(UNDO_COUNT_KEY, 0, 10000)));
        page.addEntry(new ConfigDialog.Entry("illarion.easynpc.gui.config.errorReport",
                                             new SelectEntry(CrashReporter.CFG_KEY, SelectEntry.STORE_INDEX,
                                                             new Object[]{Lang.getMsg(
                                                                     "illarion.easynpc.gui.config.errorAsk"),
                                                                          Lang.getMsg(
                                                                                  "illarion.easynpc.gui.config.errorAlways"),
                                                                          Lang.getMsg(
                                                                                  "illarion.easynpc.gui.config.errorNever")})));
        dialog.addPage(page);

        page = new ConfigDialog.Page("illarion.easynpc.gui.config.lookAndFeelTab");
        page.addEntry(new ConfigDialog.Entry("illarion.easynpc.gui.config.useWindowDecoLabel",
                                             new CheckEntry(USE_WINDOW_DECO)));

        Collection<String> themeObject = new FastTable<>();
        Collection<String> themeLabel = new FastTable<>();

        for (final Entry<String, SkinInfo> skin : SubstanceLookAndFeel.getAllSkins().entrySet()) {
            themeObject.add(skin.getValue().getClassName());
            themeLabel.add(skin.getValue().getDisplayName());
        }

        page.addEntry(new ConfigDialog.Entry("illarion.easynpc.gui.config.usedThemeLabel",
                                             new SelectEntry(USED_LOOK_AND_FEEL, SelectEntry.STORE_VALUE,
                                                             themeObject.toArray(),
                                                             themeLabel.toArray(new String[themeLabel.size()]))));

        page.addEntry(new ConfigDialog.Entry("illarion.easynpc.gui.config.useSyntaxLabel",
                                             new CheckEntry(USE_SYNTAX_HIGHLIGHT)));
        dialog.addPage(page);

        return dialog;
    }

    /**
     * Check if auto building is enabled or not.
     *
     * @return <code>true</code> in case auto building is enabled
     */
    public boolean getAutoBuild() {
        return autoBuildState;
    }

    /**
     * Get the folder where to store the easyNPC scripts.
     *
     * @return the folder to store the easyNPC scripts
     */
    public String getEasyNpcFolder() {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return new File(System.getProperty("user.home")).toString();
        }
        final File easyNpcFolderFile = cfg.getFile(LUA_NPC_FOLDER);
        if (easyNpcFolderFile == null) {
            return new File(System.getProperty("user.home")).toString();
        }
        return easyNpcFolderFile.toString();
    }

    /**
     * Get the internal used config object.
     *
     * @return the internal used config object
     */
    @Nullable
    public illarion.common.config.Config getInternalCfg() {
        return cfg;
    }

    /**
     * Get the list of the last opened files.
     *
     * @return the list of last opened files
     */
    @Nullable
    public File[] getLastOpenedFiles() {
        if (lastOpenedFilesBuffer != null) {
            return lastOpenedFilesBuffer;
        }
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return new File[LAST_OPEN_FILES_COUNT];
        }
        final String fetchedListString = cfg.getString(LAST_FILES_KEY);
        if (fetchedListString == null) {
            return new File[LAST_OPEN_FILES_COUNT];
        }
        final String[] fetchedList = fetchedListString.split(File.pathSeparator);
        final File[] returnList = new File[LAST_OPEN_FILES_COUNT];
        final String[] cleanList = new String[LAST_OPEN_FILES_COUNT];

        int entryPos = 0;
        for (int i = 0; (i < fetchedList.length) && (i < LAST_OPEN_FILES_COUNT); i++) {
            final String workString = fetchedList[i];
            if (workString.length() < 5) {
                continue;
            }
            final File createdFile = new File(workString);
            if (createdFile.exists() && createdFile.isFile()) {
                final String absolutPath = createdFile.getAbsolutePath();
                boolean alreadyInsert = false;
                for (int j = 0; j < entryPos; j++) {
                    if (cleanList[j].equals(absolutPath)) {
                        alreadyInsert = true;
                        break;
                    }
                }
                if (alreadyInsert) {
                    continue;
                }
                returnList[entryPos] = createdFile;
                cleanList[entryPos] = createdFile.getAbsolutePath();
                entryPos++;
            }
        }

        if (entryPos == 0) {
            return returnList;
        }

        final StringBuffer cleanedResult = new StringBuffer();
        for (int i = 0; i < entryPos; i++) {
            cleanedResult.append(cleanList[i]);
            cleanedResult.append(File.pathSeparator);
        }
        cleanedResult.setLength(cleanedResult.length() - 1);
        cfg.set(LAST_FILES_KEY, cleanedResult.toString());
        lastOpenedFilesBuffer = returnList;

        return returnList;
    }

    /**
     * Read the last window state from the properties and set them to the
     * windows.
     *
     * @param comp The window that shall receive the stored settings
     */
    public void getLastWindowValue(@Nonnull final JFrame comp) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
        }
        if ((cfg == null) || (cfg.getInteger(LAST_WINDOW_X) <= 0) || (cfg.getInteger(LAST_WINDOW_Y) <= 0) ||
                (cfg.getInteger(LAST_WINDOW_W) <= 0) || (cfg.getInteger(LAST_WINDOW_H) <= 0) ||
                (cfg.getInteger(LAST_WINDOW_STATE) <= 0)) {
            final Dimension screenSize = comp.getToolkit().getScreenSize();
            final int width = (screenSize.width * 8) / 10;
            final int height = (screenSize.height * 8) / 10;
            comp.setBounds(width / 8, height / 8, width, height);

            return;
        }

        try {
            final Rectangle newBounds = new Rectangle();
            newBounds.x = cfg.getInteger(LAST_WINDOW_X);
            newBounds.y = cfg.getInteger(LAST_WINDOW_Y);
            newBounds.width = cfg.getInteger(LAST_WINDOW_W);
            newBounds.height = cfg.getInteger(LAST_WINDOW_H);

            final Rectangle testBounds = new Rectangle(new Point(0, 0), comp.getToolkit().getScreenSize());
            final Rectangle intersectionBounds = newBounds.intersection(testBounds);

            if (newBounds.equals(intersectionBounds)) {
                comp.setBounds(newBounds);
                comp.setExtendedState(cfg.getInteger(LAST_WINDOW_STATE));
                return;
            }
        } catch (@Nonnull final Exception e) {
            // nothing to do
        }

        final Dimension screenSize = comp.getToolkit().getScreenSize();
        final int width = (screenSize.width * 8) / 10;
        final int height = (screenSize.height * 8) / 10;
        comp.setBounds(width / 8, height / 8, width, height);
    }

    /**
     * Get the look and feel that shall be used.
     *
     * @return the class path of the look and feel that shall be used
     */
    @Nonnull
    public String getLookAndFeel() {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return DEFAULT_LOOK_AND_FEEL;
        }
        final String lookAndFeel = cfg.getString(USED_LOOK_AND_FEEL);
        if (lookAndFeel == null) {
            return DEFAULT_LOOK_AND_FEEL;
        }
        return lookAndFeel;
    }

    /**
     * Get the folder where to store the luaNPC scripts.
     *
     * @return the folder to store the luaNPC scripts
     */
    public String getLuaNpcFolder() {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return new File(System.getProperty("user.home")).toString();
        }
        final File luaNpcFolderFile = cfg.getFile(LUA_NPC_FOLDER);
        if (luaNpcFolderFile == null) {
            return new File(System.getProperty("user.home")).toString();
        }
        return luaNpcFolderFile.toString();
    }

    /**
     * Get the list of files that were open the last time the editor was
     * running.
     *
     * @return the list of file paths
     */
    @Nonnull
    public String[] getOldFiles() {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return new String[0];
        }
        final String files = cfg.getString(OPEN_FILES);
        if (files == null) {
            return new String[0];
        }
        return files.split(File.pathSeparator);
    }

    /**
     * Get the state of the split pane in the editor view.
     *
     * @return the state of the split pane in the editor view
     */
    public double getSplitPaneState() {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return 0.75;
        }
        final double value = cfg.getDouble(SPLIT_STATE);

        if ((value >= 0.1) && (value <= 0.9)) {
            return value;
        }
        return 0.75;
    }

    /**
     * Get the amount of undo operations that are supposed to be stored.
     *
     * @return the amount of undo operations
     */
    public int getUndoCount() {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return 200;
        }
        return cfg.getInteger(UNDO_COUNT_KEY);
    }

    /**
     * Get the flag if the editor is supposed to highlight the syntax
     *
     * @return <code>true</code> in case the syntax shall be highlighted
     */
    public boolean getUseSyntaxHighlighting() {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return true;
        }
        return cfg.getBoolean(USE_SYNTAX_HIGHLIGHT);
    }

    /**
     * Get the flag if the editor is supposed to decorate the windows.
     *
     * @return <code>true</code> in case the editor is expected to decorate the
     * windows
     */
    public boolean getUseWindowDecoration() {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return false;
        }
        return cfg.getBoolean(USE_WINDOW_DECO);
    }

    /**
     * Initialize the configuration class and load all configuration values.
     */
    @SuppressWarnings("nls")
    public void init() {
        final String folder = checkFolder();
        initLogging(folder);

        final File configFile = new File(folder, "easynpceditor.xcfgz");
        cfg = new ConfigSystem(configFile);

        cfg.setDefault(LAST_FILES_KEY, "");
        cfg.setDefault(EASY_NPC_FOLDER, new File(System.getProperty("user.home")));
        cfg.setDefault(LAST_FILES_KEY, "");
        cfg.setDefault(LAST_WINDOW_H, -1);
        cfg.setDefault(LAST_WINDOW_STATE, -1);
        cfg.setDefault(LAST_WINDOW_W, -1);
        cfg.setDefault(LAST_WINDOW_X, -1);
        cfg.setDefault(LAST_WINDOW_Y, -1);
        cfg.setDefault(USED_LOOK_AND_FEEL, DEFAULT_LOOK_AND_FEEL);
        cfg.setDefault(LUA_NPC_FOLDER, System.getProperty("user.home"));
        cfg.setDefault(OPEN_FILES, "");
        cfg.setDefault(SPLIT_STATE, 0.75d);
        cfg.setDefault(UNDO_COUNT_KEY, 100);
        cfg.setDefault(USE_SYNTAX_HIGHLIGHT, true);
        cfg.setDefault(USE_WINDOW_DECO, true);
        cfg.setDefault(AUTO_BUILD_KEY, true);
        cfg.setDefault(CrashReporter.CFG_KEY, CrashReporter.MODE_ASK);

        // init values
        autoBuildState = cfg.getBoolean(AUTO_BUILD_KEY);
    }

    /**
     * Save the configuration file to the filesystem of the local system.
     */
    public void save() {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        cfg.save();
    }

    /**
     * Set the new value for the auto building flag.
     *
     * @param autobuild the new value for the auto building flag
     */
    public void setAutoBuild(final boolean autobuild) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        cfg.set(AUTO_BUILD_KEY, autobuild);
        autoBuildState = autobuild;
    }

    /**
     * Set the folder where to store the easyNPC scripts.
     *
     * @param newFolder the folder where to store the easyNPC scripts
     */
    public void setEasyNpcFolder(@Nonnull final String newFolder) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        cfg.set(EASY_NPC_FOLDER, new File(newFolder));
    }

    /**
     * Set the required values of a window into the properties so it can be
     * restored after the restart.
     *
     * @param comp the window that is the source for the stored data
     */
    public void setLastWindowValues(@Nonnull final JFrame comp) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        cfg.set(LAST_WINDOW_X, comp.getBounds().x);
        cfg.set(LAST_WINDOW_Y, comp.getBounds().y);
        cfg.set(LAST_WINDOW_W, comp.getBounds().width);
        cfg.set(LAST_WINDOW_H, comp.getBounds().height);
        cfg.set(LAST_WINDOW_STATE, comp.getExtendedState());
    }

    /**
     * Set the class path of the look and feel that shall be used from the next
     * start of the editor on.
     *
     * @param lookAndFeel the class path of the look and feel
     */
    public void setLookAndFeel(@Nonnull final String lookAndFeel) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }

        if (getLookAndFeel().equals(lookAndFeel)) {
            return;
        }

        cfg.set(USED_LOOK_AND_FEEL, lookAndFeel);
    }

    /**
     * Set the folder where to store the luaNPC scripts.
     *
     * @param newFolder the folder where to store the easyNPC scripts
     */
    public void setLuaNpcFolder(final String newFolder) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        cfg.set(LUA_NPC_FOLDER, newFolder);
    }

    /**
     * Set the list of files that shall be opened the next time the editor is
     * started.
     *
     * @param files the files to open
     */
    public void setOldFiles(@Nonnull final String[] files) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        final StringBuffer buffer = new StringBuffer();
        for (final String file : files) {
            buffer.append(file);
            buffer.append(File.pathSeparator);
        }
        buffer.setLength(buffer.length() - 1);
        cfg.set(OPEN_FILES, buffer.toString());
    }

    /**
     * Save the state of the split pane to the configuration so its restored the
     * next time its load.
     *
     * @param state the state of the split pane
     */
    public void setSplitPaneState(final double state) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        cfg.set(SPLIT_STATE, state);
    }

    /**
     * Set the amount of undo operations that should be stored.
     *
     * @param count the amount of undo operations that should be stored
     */
    public void setUndoCount(final int count) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        if (count >= 0) {
            cfg.set(UNDO_COUNT_KEY, count);
        }
    }

    /**
     * Set the flag if the editor is expected to highlight the syntax.
     *
     * @param highlight <code>true</code> in case the editor is expected
     * highlight the syntax.
     */
    public void setUseSyntaxHighlighting(final boolean highlight) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        if (getUseSyntaxHighlighting() == highlight) {
            return;
        }

        cfg.set(USE_SYNTAX_HIGHLIGHT, highlight);
        requireRestart = true;
    }

    /**
     * Set the flag if the editor is expected to decorate the window or not.
     *
     * @param deco <code>true</code> in case the editor is expected to decorate
     * the window
     */
    public void setUseWindowDecoration(final boolean deco) {
        if (cfg == null) {
            LOGGER.error("Configuration system not initialized yet.");
            return;
        }
        if (getUseWindowDecoration() == deco) {
            return;
        }

        cfg.set(USE_WINDOW_DECO, deco);
        requireRestart = true;
    }

    /**
     * Check if the application needs to restart due changed settings and
     * display a message in this case.
     */
    @SuppressWarnings("nls")
    public void showRestartWarning() {
        if (!requireRestart) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                requireRestart = false;
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                              "Some of the settings changed require a " + "restart to take effect.",
                                              "Restart needed", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * Prepare the proper output of the log files.
     *
     * @param folder the folder the log file is written to
     */
    @SuppressWarnings("nls")
    private static void initLogging(final String folder) {
        final Properties tempProps = new Properties();
        try {
            tempProps.load(Config.class.getClassLoader().getResourceAsStream("logging.properties"));
            tempProps.put("log4j.appender.IllaLogfileAppender.file", folder + File.separator + "easynpc.log");
            tempProps.put("log4j.reset", "true");
            new PropertyConfigurator().doConfigure(tempProps, LOGGER.getLoggerRepository());
        } catch (@Nonnull final IOException ex) {
            System.err.println("Error setting up logging environment");
        }
    }

    @EventTopicSubscriber(topic = "autoCheckScript")
    public void onAutoBuildModeChangedEvent(final String topic, final ActionEvent event) {
        setAutoBuild(!getAutoBuild());
    }
}
