/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
package illarion.easyquest.gui;

import illarion.common.config.ConfigDialog;
import illarion.common.config.ConfigDialog.Entry;
import illarion.common.config.ConfigDialog.Page;
import illarion.common.config.ConfigSystem;
import illarion.common.config.entries.DirectoryEntry;
import illarion.common.config.entries.TextEntry;
import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import illarion.easyquest.Lang;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Config {

    /**
     * The amount of last opened files that shall be stored.
     */
    public static final int LAST_OPEN_FILES_COUNT = 10;

    /**
     * The property key value for the easyQuest folder.
     */
    private static final String easyQuestFolder = "easyQuestFolder";

    /**
     * The singleton instance of this class.
     */
    private static final Config INSTANCE = new Config();

    /**
     * The key of the last files list on the configuration file
     */
    private static final String lastFilesKey = "lastFiles";

    /**
     * The property key value for the export folder.
     */
    private static final String exportFolder = "exportFolder";

    /**
     * The property key for the list of files that were open at the last time
     * the editor was running.
     */
    private static final String openFiles = "openFiles";

    private static final String character = "character";
    private static final String password = "password";

    /**
     * The properties that store the values of this configuration.
     */
    @Nonnull
    private ConfigSystem cfg;

    /**
     * The last generated list of opened files. When this is set to
     * {@code null} the list is generated fresh once its requested the next
     * time.
     */
    @Nullable
    private List<Path> lastOpenedFilesBuffer;

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
    private static Path checkFolder() {
        return DirectoryManager.getInstance().getDirectory(Directory.User);
    }

    /**
     * Prepend this file to the list of last opened files.
     *
     * @param file the file to prepend
     */
    public void addLastOpenedFile(@Nonnull Path file) {
        cfg.set(lastFilesKey, file.toAbsolutePath() + File.pathSeparator + cfg.getString(lastFilesKey));
        lastOpenedFilesBuffer = null;
    }

    @Nonnull
    public ConfigDialog createDialog() {
        ConfigDialog dialog = new ConfigDialog();
        dialog.setConfig(cfg);
        dialog.setMessageSource(Lang.getInstance());

        Page page;
        page = new Page("illarion.easyquest.gui.config.generalTab");
        page.addEntry(new Entry("illarion.easyquest.gui.config.easyQuestFolderLabel",
                                             new DirectoryEntry(easyQuestFolder, null)));
        page.addEntry(new Entry("illarion.easyquest.gui.config.exportFolderLabel",
                                             new DirectoryEntry(exportFolder, null)));
        dialog.addPage(page);

        page = new Page("illarion.easyquest.gui.config.serverTab");
        page.addEntry(new Entry("illarion.easyquest.gui.config.characterLabel", new TextEntry(character)));
        page.addEntry(new Entry("illarion.easyquest.gui.config.passwordLabel", new TextEntry(password)));
        dialog.addPage(page);

        return dialog;
    }

    @Nonnull
    public Path getEasyQuestFolder() {
        Path folder = cfg.getPath(easyQuestFolder);
        assert folder != null;
        return folder;
    }

    public void setEasyQuestFolder(@Nonnull Path newFolder) {
        cfg.set(easyQuestFolder, newFolder);
    }

    @Nonnull
    public Collection<Path> getLastOpenedFiles() {
        if (lastOpenedFilesBuffer != null) {
            return Collections.unmodifiableList(lastOpenedFilesBuffer);
        }
        String lastFiles = cfg.getString(lastFilesKey);
        if (lastFiles == null || lastFiles.isEmpty()) {
            lastOpenedFilesBuffer = Collections.emptyList();
            return Collections.unmodifiableList(lastOpenedFilesBuffer);
        }
        String[] fetchedList = lastFiles.split(File.pathSeparator);
        List<Path> returnList = Arrays.asList(new Path[Math.min(fetchedList.length, LAST_OPEN_FILES_COUNT)]);

        int entryPos = 0;
        for (int i = 0; (i < fetchedList.length) && (i < LAST_OPEN_FILES_COUNT); i++) {
            String workString = fetchedList[i];
            if (workString.length() < 5) {
                continue;
            }
            Path createdFile = Paths.get(workString);
            if (Files.isRegularFile(createdFile)) {
                if (returnList.contains(createdFile)) {
                    continue;
                }
                returnList.set(entryPos, createdFile);
                entryPos++;
            }
        }

        if (entryPos == 0) {
            return returnList;
        }

        StringBuilder cleanedResult = new StringBuilder();
        for (int i = 0; i < entryPos; i++) {
            cleanedResult.append(returnList.get(i).toAbsolutePath());
            cleanedResult.append(File.pathSeparator);
        }
        cleanedResult.setLength(cleanedResult.length() - 1);
        cfg.set(lastFilesKey, cleanedResult.toString());
        lastOpenedFilesBuffer = returnList;

        return returnList;
    }

    @Nonnull
    public Path getExportFolder() {
        Path folder = cfg.getPath(exportFolder);
        assert folder != null;
        return folder;
    }

    public void setExportFolder(@Nonnull Path newFolder) {
        cfg.set(exportFolder, newFolder);
    }

    /**
     * Get the list of files that were open the last time the editor was
     * running.
     *
     * @return the list of file paths
     */
    @Nonnull
    public Collection<Path> getOldFiles() {
        String openFilesString = cfg.getString(openFiles);
        if (openFilesString == null || openFilesString.isEmpty()) {
            return Collections.emptyList();
        }
        String[] splitFiles = openFilesString.split(File.pathSeparator);
        Path[] paths = new Path[splitFiles.length];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = Paths.get(splitFiles[i]);
        }
        return Arrays.asList(paths);
    }

    /**
     * Set the list of files that shall be opened the next time the editor is
     * started.
     *
     * @param files the files to open
     */
    public void setOldFiles(@Nonnull Iterable<Path> files) {
        StringBuilder buffer = new StringBuilder();
        for (Path file : files) {
            buffer.append(file.toAbsolutePath());
            buffer.append(File.pathSeparator);
        }
        buffer.setLength(buffer.length() - 1);
        cfg.set(openFiles, buffer.toString());
    }

    /**
     * Initialize the configuration class and load all configuration values.
     */
    public void init() {
        Path folder = checkFolder();

        /*
      The file that holds the configuration.
     */
        Path configFile = folder.resolve("easyquesteditor.xcfgz");
        cfg = new ConfigSystem(configFile);

        cfg.setDefault(lastFilesKey, "");
        cfg.setDefault(easyQuestFolder, Paths.get(System.getProperty("user.home")));
        cfg.setDefault(lastFilesKey, "");
        cfg.setDefault(exportFolder, Paths.get(System.getProperty("user.home")));
        cfg.setDefault(openFiles, "");
        cfg.setDefault(character, "");
        cfg.setDefault(password, "");
    }

    /**
     * Save the configuration file to the filesystem of the local system.
     */
    public void save() {
        cfg.save();
    }

    @Nullable
    public String getCharacter() {
        return cfg.getString(character);
    }

    public void setCharacter(@Nonnull String newCharacter) {
        cfg.set(character, newCharacter);
    }

    @Nullable
    public String getPassword() {
        return cfg.getString(password);
    }

    public void setPassword(@Nonnull String newPassword) {
        cfg.set(password, newPassword);
    }
}
