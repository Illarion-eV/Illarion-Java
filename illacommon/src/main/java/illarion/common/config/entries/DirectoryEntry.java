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
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Objects;

/**
 * This is a configuration entry that is used to set a directory select entry in
 * the configuration. It will display a short text field along with a button to
 * search for the directory. The default search directory will be the home
 * directory of the user.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DirectoryEntry implements ConfigEntry {
    /**
     * The configuration that is controlled by this directory entry.
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
     * Create a new configuration entry that is handled by this entry.
     *
     * @param entry the configuration key that is handled by this file entry
     * @param defaultDir the default directory that is opened in case no file is
     * selected
     */
    public DirectoryEntry(@Nonnull String entry, @Nullable String defaultDir) {
        configEntry = entry;
        dir = Objects.requireNonNull((defaultDir == null) ? System.getProperty("user.home") : defaultDir);
    }

    /**
     * Get the directory that is displayed by default.
     *
     * @return the directory displayed by default
     */
    @Nonnull
    @Contract(pure = true)
    public String getDefaultDir() {
        return dir;
    }

    /**
     * Get the value set in the configuration for this text entry.
     *
     * @return the configuration stored for this text entry
     */
    @Nullable
    @Contract(pure = true)
    public Path getValue() {
        if (cfg == null) {
            throw new IllegalStateException("There is no reference to the configuration system set.");
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
            throw new IllegalStateException("There is no reference to the configuration system set.");
        }
        cfg.set(configEntry, newValue);
    }
}
