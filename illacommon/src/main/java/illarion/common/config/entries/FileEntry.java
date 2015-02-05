/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.common.config.entries;

import illarion.common.config.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Objects;

/**
 * This is a configuration entry that is used to set a file select entry in the  configuration. It will display a
 * short text field along with a button to search for the file. The default search directory will be the home directory
 * of the user.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class FileEntry implements ConfigEntry {
    /**
     * The configuration that is controlled by this text entry.
     */
    @Nullable
    private Config cfg;

    /**
     * The key in the configuration that is handled by this configuration.
     */
    @Nonnull
    private final String configEntry;

    /**
     * The directory that is displayed by default.
     */
    @Nonnull
    private final String dir;

    /**
     * The description of the select able files displayed in the file open
     * dialog.
     */
    @Nonnull
    private final String fileDesc;

    /**
     * The list of file names that are allowed to be selected
     */
    @Nonnull
    private final String fileEndings;

    /**
     * The name of the file that is created using this dialog.
     */
    @Nonnull
    private final String name;

    /**
     * Create a new configuration entry that is handled by this entry.
     *
     * @param entry the configuration key that is handled by this file entry
     * @param allowedFileEndings the list of file endings that are allowed as files to be chosen, as separator a
     *                           semicolon is used
     * @param fileDescription the description displayed of the files to be selected
     * @param defaultDir the default directory that is opened in case no file is selected
     * @param defaultName the default name of the file to be created using this file entry
     */
    public FileEntry(
            @Nonnull String entry,
            @Nonnull String allowedFileEndings,
            @Nonnull String fileDescription,
            @Nullable String defaultDir,
            @Nonnull String defaultName) {
        configEntry = entry;
        fileEndings = allowedFileEndings;
        fileDesc = fileDescription;
        dir = (defaultDir == null) ? Objects.requireNonNull(System.getProperty("user.home")) : defaultDir;
        name = defaultName;
    }

    /**
     * Get the directory that is displayed by default.
     *
     * @return the directory displayed by default
     */
    @Nonnull
    public String getDefaultDir() {
        return dir;
    }

    /**
     * Get the file description that is displayed in the file dialog.
     *
     * @return the file description of the dialog
     */
    @Nonnull
    public String getFileDesc() {
        return fileDesc;
    }

    /**
     * Get the list of file endings allowed to be used in this file chooser.
     *
     * @return the list of file entries
     */
    @Nonnull
    public String getFileEndings() {
        return fileEndings;
    }

    /**
     * Get the default name of the file to be created.
     *
     * @return the default name of the file to be created
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Get the value set in the configuration for this text entry.
     *
     * @return the configuration stored for this text entry
     */
    @Nullable
    public Path getValue() {
        if (cfg == null) {
            throw new IllegalStateException("Reference to configurations system is missing.");
        }
        return cfg.getPath(configEntry);
    }

    /**
     * Set the configuration handled by this configuration entry.
     *
     * @param config the configuration that is supposed to be handled by this
     * configuration entry
     */
    @Override
    public void setConfig(@Nonnull Config config) {
        cfg = config;
    }

    /**
     * Set the new value of the configuration entry that is controlled by this.
     *
     * @param newValue the new configuration value
     */
    public void setValue(@Nonnull Path newValue) {
        if (cfg == null) {
            throw new IllegalStateException("Reference to configurations system is missing.");
        }
        cfg.set(configEntry, newValue);
    }
}
