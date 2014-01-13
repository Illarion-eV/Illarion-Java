/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyQuest Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyQuest Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.gui;

import illarion.common.config.ConfigChangeListener;
import illarion.common.config.ConfigDialog;
import illarion.common.config.ConfigSystem;
import illarion.common.config.entries.DirectoryEntry;
import illarion.common.config.entries.TextEntry;
import illarion.common.util.DirectoryManager;
import illarion.easyquest.Lang;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.io.File;
import java.nio.file.Path;

public final class Config implements ConfigChangeListener {

    /**
     * The amount of last opened files that shall be stored.
     */
    public static final int LAST_OPEN_FILES_COUNT = 10;

    /**
     * The property key value for the easyQuest folder.
     */
    @SuppressWarnings("nls")
    private static final String easyQuestFolder = "easyQuestFolder";

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
     * The property key value for the export folder.
     */
    @SuppressWarnings("nls")
    private static final String exportFolder = "exportFolder";

    /**
     * The property key for the list of files that were open at the last time
     * the editor was running.
     */
    @SuppressWarnings("nls")
    private static final String openFiles = "openFiles";

    private static final String character = "character";
    private static final String password = "password";

    /**
     * The properties that store the values of this configuration.
     */
    @Nullable
    private ConfigSystem cfg;

    /**
     * The file that holds the configuration.
     */
    @Nullable
    private File configFile = null;

    /**
     * The last generated list of opened files. When this is set to
     * <code>null</code> the list is generated fresh once its requested the next
     * time.
     */
    @Nullable
    private File[] lastOpenedFilesBuffer;

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
    @Nonnull
    @SuppressWarnings("nls")
    private static String checkFolder() {
        if (!DirectoryManager.getInstance().isDirectorySet(DirectoryManager.Directory.User)) {
            JOptionPane.showMessageDialog(null, "Installation ist fehlerhaft. Bitte neu ausführen.\n\n" +
                    "Installation is corrupted, please run it again.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        return DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User).getAbsolutePath();
    }

    /**
     * Prepend this file to the list of last opened files.
     *
     * @param file the file to prepend
     */
    public void addLastOpenedFile(@Nonnull final Path file) {
        cfg.set(lastFilesKey, file.toAbsolutePath() + File.pathSeparator + cfg.getString(lastFilesKey));
        lastOpenedFilesBuffer = null;
    }

    @Nonnull
    @SuppressWarnings("nls")
    public ConfigDialog createDialog() {
        final ConfigDialog dialog = new ConfigDialog();
        dialog.setConfig(cfg);
        dialog.setMessageSource(Lang.getInstance());

        ConfigDialog.Page page;
        page = new ConfigDialog.Page("illarion.easyquest.gui.config.generalTab");
        page.addEntry(new ConfigDialog.Entry("illarion.easyquest.gui.config.easyQuestFolderLabel",
                                             new DirectoryEntry(easyQuestFolder, null)));
        page.addEntry(new ConfigDialog.Entry("illarion.easyquest.gui.config.exportFolderLabel",
                                             new DirectoryEntry(exportFolder, null)));
        dialog.addPage(page);

        page = new ConfigDialog.Page("illarion.easyquest.gui.config.serverTab");
        page.addEntry(new ConfigDialog.Entry("illarion.easyquest.gui.config.characterLabel", new TextEntry(character)));
        page.addEntry(new ConfigDialog.Entry("illarion.easyquest.gui.config.passwordLabel", new TextEntry(password)));
        dialog.addPage(page);

        return dialog;
    }

    public String getEasyQuestFolder() {
        return cfg.getFile(easyQuestFolder).toString();
    }

    @Nullable
    public illarion.common.config.Config getInternalCfg() {
        return cfg;
    }

    @Nullable
    public File[] getLastOpenedFiles() {
        if (lastOpenedFilesBuffer != null) {
            return lastOpenedFilesBuffer;
        }
        final String[] fetchedList = cfg.getString(lastFilesKey).split(File.pathSeparator);
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

        final StringBuilder cleanedResult = new StringBuilder();
        for (int i = 0; i < entryPos; i++) {
            cleanedResult.append(cleanList[i]);
            cleanedResult.append(File.pathSeparator);
        }
        cleanedResult.setLength(cleanedResult.length() - 1);
        cfg.set(lastFilesKey, cleanedResult.toString());
        lastOpenedFilesBuffer = returnList;

        return returnList;
    }

    public String getExportFolder() {
        return cfg.getFile(exportFolder).toString();
    }

    /**
     * Get the list of files that were open the last time the editor was
     * running.
     *
     * @return the list of file paths
     */
    @Nonnull
    public String[] getOldFiles() {
        return cfg.getString(openFiles).split(File.pathSeparator);
    }

    /**
     * Initialize the configuration class and load all configuration values.
     */
    @SuppressWarnings("nls")
    public void init() {
        final String folder = checkFolder();

        configFile = new File(folder, "easyquesteditor.xcfgz");
        cfg = new ConfigSystem(configFile);

        cfg.setDefault(lastFilesKey, "");
        cfg.setDefault(easyQuestFolder, new File(System.getProperty("user.home")));
        cfg.setDefault(lastFilesKey, "");
        cfg.setDefault(exportFolder, System.getProperty("user.home"));
        cfg.setDefault(openFiles, "");
        cfg.setDefault(character, "");
        cfg.setDefault(password, "");

        cfg.addListener(this);
    }

    /**
     * Save the configuration file to the filesystem of the local system.
     */
    public void save() {
        cfg.save();
    }

    public void setEasyQuestFolder(@Nonnull final String newFolder) {
        cfg.set(easyQuestFolder, new File(newFolder));
    }

    public void setExportFolder(final String newFolder) {
        cfg.set(exportFolder, newFolder);
    }

    /**
     * Set the list of files that shall be opened the next time the editor is
     * started.
     *
     * @param files the files to open
     */
    public void setOldFiles(@Nonnull final String[] files) {
        final StringBuilder buffer = new StringBuilder();
        for (final String file : files) {
            buffer.append(file);
            buffer.append(File.pathSeparator);
        }
        buffer.setLength(buffer.length() - 1);
        cfg.set(openFiles, buffer.toString());
    }

    @Override
    public void configChanged(illarion.common.config.Config cfg, String key) {

    }

    public void setCharacter(final String newCharacter) {
        cfg.set(character, newCharacter);
    }

    public void setPassword(final String newPassword) {
        cfg.set(password, newPassword);
    }

    public String getCharacter() {
        return cfg.getFile(character).toString();
    }

    public String getPassword() {
        return cfg.getFile(password).toString();
    }
}
