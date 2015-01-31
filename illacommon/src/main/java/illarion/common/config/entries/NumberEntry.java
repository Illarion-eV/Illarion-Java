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
import illarion.common.types.Range;
import illarion.common.util.FastMath;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is a configuration entry that is used to display a number range entry in
 * the configuration dialog. So a simple yes/no option.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class NumberEntry implements ConfigEntry {
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
     * The range the value is allowed to work in.
     */
    @Nonnull
    private final Range range;

    /**
     * Create a new configuration entry that is handled by this entry.
     *
     * @param entry the configuration key that is handled by this text entry
     * @param minLimit the minimal value that is allowed for this entry
     * @param maxLimit the maximal value that is allowed for this entry
     */
    public NumberEntry(@Nonnull String entry, int minLimit, int maxLimit) {
        configEntry = entry;
        range = new Range(minLimit, maxLimit);
    }

    /**
     * Get the range this number entry is allowed to use.
     *
     * @return the range of this number entry
     */
    @Nonnull
    @Contract(pure = true)
    public Range getRange() {
        return range;
    }

    /**
     * Get the value set in the configuration for this check entry.
     *
     * @return the configuration stored for this check entry
     */
    @Contract(pure = true)
    public int getValue() {
        if (cfg == null) {
            throw new IllegalStateException("Reference to configuration system is not set.");
        }
        return FastMath.clamp(cfg.getInteger(configEntry), range);
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
    public void setValue(int newValue) {
        if (cfg == null) {
            throw new IllegalStateException("Reference to configuration system is not set.");
        }
        cfg.set(configEntry, FastMath.clamp(newValue, range));
    }
}
