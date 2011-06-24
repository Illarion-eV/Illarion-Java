/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.config.entries;

import illarion.common.config.Config;

/**
 * This is a configuration entry that is used to a simple text entry in the
 * configuration.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class TextEntry implements ConfigEntry {
    /**
     * The configuration that is controlled by this text entry.
     */
    private Config cfg;

    /**
     * The key in the configuration that is handled by this configuration.
     */
    private final String configEntry;

    /**
     * Create a new configuration entry that is handled by this entry.
     * 
     * @param entry the configuration key that is handled by this text entry
     */
    public TextEntry(final String entry) {
        configEntry = entry;
    }

    /**
     * Get the value set in the configuration for this text entry.
     * 
     * @return the configuration stored for this text entry
     */
    public String getValue() {
        return cfg.getString(configEntry);
    }

    /**
     * Set the configuration handled by this configuration entry.
     * 
     * @param config the configuration that is supposed to be handled by this
     *            configuration entry
     */
    @Override
    public void setConfig(final Config config) {
        cfg = config;
    }

    /**
     * Set the new value of the configuration entry that is controlled by this.
     * 
     * @param newValue the new configuration value
     */
    public void setValue(final String newValue) {
        cfg.set(configEntry, newValue);
    }
}
