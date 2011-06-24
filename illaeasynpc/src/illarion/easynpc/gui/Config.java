/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javolution.util.FastTable;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SkinInfo;

import illarion.easynpc.Lang;

import illarion.common.config.ConfigChangeListener;
import illarion.common.config.ConfigDialog;
import illarion.common.config.ConfigSystem;
import illarion.common.config.entries.CheckEntry;
import illarion.common.config.entries.DirectoryEntry;
import illarion.common.config.entries.NumberEntry;
import illarion.common.config.entries.SelectEntry;
import illarion.common.util.DirectoryManager;

/**
 * This class is used to store and to publish the settings used by the editor
 * GUI.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public final class Config implements ConfigChangeListener {
    /**
     * The class path of the look and feel that is used by default.
     */
    @SuppressWarnings("nls")
    public static final String defaultLookAndFeel =
        "org.pushingpixels.substance.api.skin.OfficeSilver2007Skin";

    /**
     * The amount of last opened files that shall be stored.
     */
    public static final int LAST_OPEN_FILES_COUNT = 10;

    /**
     * The key in the property file for the auto build flag
     */
    @SuppressWarnings("nls")
    private static final String autoBuildKey = "autoBuild";

    /**
     * The property key value for the easyNPC script folder.
     */
    @SuppressWarnings("nls")
    private static final String easyNpcFolder = "easyNpcFolder";

    /**
     * The singleton instance of this class.
     */
    private static final Config INSTANCE = new Config();

    /**
     * The key of the last files list on the configuration file
     */
    @SuppressWarnings("nls")
    private static final String lastFilesKey = "lastFiles";

    /**
     * The key for the height of the stored window.
     */
    @SuppressWarnings("nls")
    private static final String lastWindowH = "lastWindowH";

    /**
     * The key for the extended state of the stored window.
     */
    @SuppressWarnings("nls")
    private static final String lastWindowState = "lastWindowState";

    /**
     * The key for the width of the stored window.
     */
    @SuppressWarnings("nls")
    private static final String lastWindowW = "lastWindowW";

    /**
     * The key for the x coordinate of the stored window.
     */
    @SuppressWarnings("nls")
    private static final String lastWindowX = "lastWindowX";

    /**
     * The key for the y coordinate of the stored window.
     */
    @SuppressWarnings("nls")
    private static final String lastWindowY = "lastWindowY";

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Config.class);

    /**
     * The property key value for the luaNPC script folder.
     */
    @SuppressWarnings("nls")
    private static final String luaNpcFolder = "luaNpcFolder";

    /**
     * The property key for the list of files that were open at the last time
     * the editor was running.
     */
    @SuppressWarnings("nls")
    private static final String openFiles = "openFiles";

    /**
     * The property key of the value that describes the state of the split pane
     * in the editor view.
     */
    @SuppressWarnings("nls")
    private static final String splitState = "splitState";

    /**
     * Get the key that is used to store the amount the undo operations.
     */
    @SuppressWarnings("nls")
    private static final String undoCountKey = "undoCount";

    /**
     * The property key value for the name of the look and feel shall be used.
     */
    @SuppressWarnings("nls")
    private static final String usedLookAndFeel = "usedLookAndFeel";

    /**
     * The property key for the flag that says of the editor shall use syntax
     * highlighting or not.
     */
    @SuppressWarnings("nls")
    private static final String useSyntaxHighlight = "useSyntaxHighlight";
    /**
     * The property key value for the use window decoration flag.
     */
    @SuppressWarnings("nls")
    private static final String useWindowDeco = "useWindowDeco";

    /**
     * The buffered state of the auto build value. This is used because the auto
     * build state is likely requested really often.
     */
    private boolean autoBuildState;

    /**
     * The properties that store the values of this configuration.
     */
    private ConfigSystem cfg;

    /**
     * The file that holds the configuration.
     */
    private File configFile = null;

    /**
     * The last generated list of opened files. When this is set to
     * <code>null</code> the list is generated fresh once its requested the next
     * time.
     */
    private File[] lastOpenedFilesBuffer;

    /**
     * If this is set to true, the application requires to restart to take all
     * settings done.
     */
    private boolean requireRestart = false;

    /**
     * Private constructor to ensure that no instance but the singleton instance
     * is created.
     */
    private Config() {
        // nothing
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance
     */
    public static Config getInstance() {
        return INSTANCE;
    }

    /**
     * This function determines the user data directory and requests the folder
     * to store the editor data in case it is needed. It also performs checks to
     * see if the folder is valid.
     * 
     * @return a string with the path to the folder or null in case no folder is
     *         set
     */
    @SuppressWarnings("nls")
    private static String checkFolder() {
        if (!DirectoryManager.getInstance().hasUserDirectory()) {
            JOptionPane.showMessageDialog(null,
                "Installation ist fehlerhaft. Bitte neu ausführen.\n\n"
                    + "Installation is corrupted, please run it again.",
                "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        return DirectoryManager.getInstance().getUserDirectory()
            .getAbsolutePath();
    }

    /**
     * Prepend this file to the list of last opened files.
     * 
     * @param file the file to prepend
     */
    public void addLastOpenedFile(final File file) {
        cfg.set(lastFilesKey, file.getAbsolutePath() + File.pathSeparator
            + cfg.getString(lastFilesKey));
        lastOpenedFilesBuffer = null;
    }

    @Override
    public void configChanged(final illarion.common.config.Config cfg,
        final String key) {
        if (key.equals(usedLookAndFeel)) {
            SubstanceLookAndFeel
                .setSkin(Config.getInstance().getLookAndFeel());
            final int count = MainFrame.getInstance().getOpenTabs();

            for (int i = 0; i < count; i++) {
                MainFrame.getInstance().getScriptEditor(i).resetEditorKit();
            }
            SwingUtilities.updateComponentTreeUI(MainFrame.getInstance());
        }
    }

    @SuppressWarnings("nls")
    public ConfigDialog createDialog() {
        final ConfigDialog dialog = new ConfigDialog();
        dialog.setConfig(cfg);
        dialog.setMessageSource(Lang.getInstance());
        dialog.setDisplaySystem(ConfigDialog.DISPLAY_SWING);

        ConfigDialog.Page page;
        page = new ConfigDialog.Page("illarion.easynpc.gui.config.generalTab");
        page.addEntry(new ConfigDialog.Entry(
            "illarion.easynpc.gui.config.easyNpcFolderLabel",
            new DirectoryEntry(easyNpcFolder, null)));
        page.addEntry(new ConfigDialog.Entry(
            "illarion.easynpc.gui.config.luaFolderLabel", new DirectoryEntry(
                luaNpcFolder, null)));
        page.addEntry(new ConfigDialog.Entry(
            "illarion.easynpc.gui.config.maxUndoLabel", new NumberEntry(
                undoCountKey, 0, 10000)));
        page.addEntry(new ConfigDialog.Entry(
            "illarion.easynpc.gui.config.errorReport", new SelectEntry(
                illarion.common.bug.CrashReporter.CFG_KEY,
                SelectEntry.STORE_INDEX, Lang
                    .getMsg("illarion.easynpc.gui.config.errorAsk"), Lang
                    .getMsg("illarion.easynpc.gui.config.errorAlways"), Lang
                    .getMsg("illarion.easynpc.gui.config.errorNever"))));
        dialog.addPage(page);

        page =
            new ConfigDialog.Page("illarion.easynpc.gui.config.lookAndFeelTab");
        page.addEntry(new ConfigDialog.Entry(
            "illarion.easynpc.gui.config.useWindowDecoLabel", new CheckEntry(
                useWindowDeco)));

        FastTable<String> themeObject = FastTable.newInstance();
        FastTable<String> themeLabel = FastTable.newInstance();

        for (final Entry<String, SkinInfo> skin : SubstanceLookAndFeel
            .getAllSkins().entrySet()) {
            themeObject.add(skin.getValue().getClassName());
            themeLabel.add(skin.getValue().getDisplayName());
        }

        page.addEntry(new ConfigDialog.Entry(
            "illarion.easynpc.gui.config.usedThemeLabel", new SelectEntry(
                usedLookAndFeel, SelectEntry.STORE_VALUE, themeObject
                    .toArray(), themeLabel.toArray(new String[themeLabel
                    .size()]))));
        FastTable.recycle(themeObject);
        themeObject = null;

        FastTable.recycle(themeLabel);
        themeLabel = null;

        page.addEntry(new ConfigDialog.Entry(
            "illarion.easynpc.gui.config.useSyntaxLabel", new CheckEntry(
                useSyntaxHighlight)));
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
        return cfg.getFile(easyNpcFolder).toString();
    }

    /**
     * Get the internal used config object.
     * 
     * @return the internal used config object
     */
    public illarion.common.config.Config getInternalCfg() {
        return cfg;
    }

    /**
     * Get the list of the last opened files.
     * 
     * @return the list of last opened files
     */
    public File[] getLastOpenedFiles() {
        if (lastOpenedFilesBuffer != null) {
            return lastOpenedFilesBuffer;
        }
        final String[] fetchedList =
            cfg.getString(lastFilesKey).split(File.pathSeparator);
        final File[] returnList = new File[LAST_OPEN_FILES_COUNT];
        final String[] cleanList = new String[LAST_OPEN_FILES_COUNT];

        int entryPos = 0;
        for (int i = 0; (i < fetchedList.length)
            && (i < LAST_OPEN_FILES_COUNT); i++) {
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
        cfg.set(lastFilesKey, cleanedResult.toString());
        lastOpenedFilesBuffer = returnList;

        return returnList;
    }

    /**
     * Read the last window state from the properties and set them to the
     * windows.
     * 
     * @param comp The window that shall receive the stored settings
     */
    public void getLastWindowValue(final JFrame comp) {
        if ((cfg.getInteger(lastWindowX) <= 0)
            || (cfg.getInteger(lastWindowY) <= 0)
            || (cfg.getInteger(lastWindowW) <= 0)
            || (cfg.getInteger(lastWindowH) <= 0)
            || (cfg.getInteger(lastWindowState) <= 0)) {
            final Dimension screenSize = comp.getToolkit().getScreenSize();
            final int width = (screenSize.width * 8) / 10;
            final int height = (screenSize.height * 8) / 10;
            comp.setBounds(width / 8, height / 8, width, height);

            return;
        }

        try {
            final Rectangle newBounds = new Rectangle();
            newBounds.x = cfg.getInteger(lastWindowX);
            newBounds.y = cfg.getInteger(lastWindowY);
            newBounds.width = cfg.getInteger(lastWindowW);
            newBounds.height = cfg.getInteger(lastWindowH);

            final Rectangle testBounds =
                new Rectangle(new Point(0, 0), comp.getToolkit()
                    .getScreenSize());
            final Rectangle intersectionBounds =
                newBounds.intersection(testBounds);

            if (newBounds.equals(intersectionBounds)) {
                comp.setBounds(newBounds);
                comp.setExtendedState(cfg.getInteger(lastWindowState));
                return;
            }
        } catch (final Exception e) {
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
    public String getLookAndFeel() {
        return cfg.getString(usedLookAndFeel);
    }

    /**
     * Get the folder where to store the luaNPC scripts.
     * 
     * @return the folder to store the luaNPC scripts
     */
    public String getLuaNpcFolder() {
        return cfg.getFile(luaNpcFolder).toString();
    }

    /**
     * Get the list of files that were open the last time the editor was
     * running.
     * 
     * @return the list of file paths
     */
    public String[] getOldFiles() {
        return cfg.getString(openFiles).split(File.pathSeparator);
    }

    /**
     * Get the state of the split pane in the editor view.
     * 
     * @return the state of the split pane in the editor view
     */
    public double getSplitPaneState() {
        final double value = cfg.getDouble(splitState);

        if ((value >= 0.1d) && (value <= 0.9d)) {
            return value;
        }
        return 0.75d;
    }

    /**
     * Get the amount of undo operations that are supposed to be stored.
     * 
     * @return the amount of undo operations
     */
    public int getUndoCount() {
        return cfg.getInteger(undoCountKey);
    }

    /**
     * Get the flag if the editor is supposed to highlight the syntax
     * 
     * @return <code>true</code> in case the syntax shall be highlighted
     */
    public boolean getUseSyntaxHighlighting() {
        return cfg.getBoolean(useSyntaxHighlight);
    }

    /**
     * Get the flag if the editor is supposed to decorate the windows.
     * 
     * @return <code>true</code> in case the editor is expected to decorate the
     *         windows
     */
    public boolean getUseWindowDecoration() {
        return cfg.getBoolean(useWindowDeco);
    }

    /**
     * Initialize the configuration class and load all configuration values.
     */
    @SuppressWarnings("nls")
    public void init() {
        final String folder = checkFolder();
        initLogging(folder);

        configFile = new File(folder, "easynpceditor.xcfgz");
        cfg = new ConfigSystem(configFile);

        cfg.setDefault(lastFilesKey, "");
        cfg.setDefault(easyNpcFolder,
            new File(System.getProperty("user.home")));
        cfg.setDefault(lastFilesKey, "");
        cfg.setDefault(lastWindowH, -1);
        cfg.setDefault(lastWindowState, -1);
        cfg.setDefault(lastWindowW, -1);
        cfg.setDefault(lastWindowX, -1);
        cfg.setDefault(lastWindowY, -1);
        cfg.setDefault(usedLookAndFeel, defaultLookAndFeel);
        cfg.setDefault(luaNpcFolder, System.getProperty("user.home"));
        cfg.setDefault(openFiles, "");
        cfg.setDefault(splitState, 0.75d);
        cfg.setDefault(undoCountKey, 100);
        cfg.setDefault(useSyntaxHighlight, true);
        cfg.setDefault(useWindowDeco, true);
        cfg.setDefault(autoBuildKey, true);
        cfg.setDefault(illarion.common.bug.CrashReporter.CFG_KEY,
            illarion.common.bug.CrashReporter.MODE_ASK);

        cfg.addListener(this);

        // init values
        autoBuildState = cfg.getBoolean(autoBuildKey);
    }

    /**
     * Save the configuration file to the filesystem of the local system.
     */
    public void save() {
        cfg.save();
    }

    /**
     * Set the new value for the auto building flag.
     * 
     * @param autobuild the new value for the auto building flag
     */
    public void setAutoBuild(final boolean autobuild) {
        cfg.set(autoBuildKey, autobuild);
        autoBuildState = autobuild;
    }

    /**
     * Set the folder where to store the easyNPC scripts.
     * 
     * @param newFolder the folder where to store the easyNPC scripts
     */
    public void setEasyNpcFolder(final String newFolder) {
        cfg.set(easyNpcFolder, new File(newFolder));
    }

    /**
     * Set the required values of a window into the properties so it can be
     * restored after the restart.
     * 
     * @param comp the window that is the source for the stored data
     */
    public void setLastWindowValues(final JFrame comp) {
        cfg.set(lastWindowX, comp.getBounds().x);
        cfg.set(lastWindowY, comp.getBounds().y);
        cfg.set(lastWindowW, comp.getBounds().width);
        cfg.set(lastWindowH, comp.getBounds().height);
        cfg.set(lastWindowState, comp.getExtendedState());
    }

    /**
     * Set the class path of the look and feel that shall be used from the next
     * start of the editor on.
     * 
     * @param lookAndFeel the class path of the look and feel
     */
    public void setLookAndFeel(final String lookAndFeel) {
        if (getLookAndFeel().equals(lookAndFeel)) {
            return;
        }

        cfg.set(usedLookAndFeel, lookAndFeel);
    }

    /**
     * Set the folder where to store the luaNPC scripts.
     * 
     * @param newFolder the folder where to store the easyNPC scripts
     */
    public void setLuaNpcFolder(final String newFolder) {
        cfg.set(luaNpcFolder, newFolder);
    }

    /**
     * Set the list of files that shall be opened the next time the editor is
     * started.
     * 
     * @param files the files to open
     */
    public void setOldFiles(final String[] files) {
        final StringBuffer buffer = new StringBuffer();
        for (final String file : files) {
            buffer.append(file);
            buffer.append(File.pathSeparator);
        }
        buffer.setLength(buffer.length() - 1);
        cfg.set(openFiles, buffer.toString());
    }

    /**
     * Save the state of the split pane to the configuration so its restored the
     * next time its load.
     * 
     * @param state the state of the split pane
     */
    public void setSplitPaneState(final double state) {
        cfg.set(splitState, state);
    }

    /**
     * Set the amount of undo operations that should be stored.
     * 
     * @param count the amount of undo operations that should be stored
     */
    public void setUndoCount(final int count) {
        if (count >= 0) {
            cfg.set(undoCountKey, count);
        }
    }

    /**
     * Set the flag if the editor is expected to highlight the syntax.
     * 
     * @param highlight <code>true</code> in case the editor is expected
     *            highlight the syntax.
     */
    public void setUseSyntaxHighlighting(final boolean highlight) {
        if (getUseSyntaxHighlighting() == highlight) {
            return;
        }

        cfg.set(useSyntaxHighlight, highlight);
        requireRestart = true;
    }

    /**
     * Set the flag if the editor is expected to decorate the window or not.
     * 
     * @param deco <code>true</code> in case the editor is expected to decorate
     *            the window
     */
    public void setUseWindowDecoration(final boolean deco) {
        if (getUseWindowDecoration() == deco) {
            return;
        }

        cfg.set(useWindowDeco, deco);
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
                    "Some of the settings changed require a restart to "
                        + "take effect.", "Restart needed",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * Prepare the proper output of the log files.
     * 
     * @param folder the folder the log file is written to
     */
    @SuppressWarnings("nls")
    private void initLogging(final String folder) {
        final Properties tempProps = new Properties();
        try {
            tempProps.load(Config.class.getClassLoader().getResourceAsStream(
                "logging.properties"));
            tempProps.put("log4j.appender.IllaLogfileAppender.file", folder
                + File.separator + "easynpc.log");
            tempProps.put("log4j.reset", "true");
            new PropertyConfigurator().doConfigure(tempProps,
                LOGGER.getLoggerRepository());
        } catch (final IOException ex) {
            System.err.println("Error setting up logging environment");
        }
    }
}
