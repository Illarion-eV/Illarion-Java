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
import illarion.common.util.FastMath;
import illarion.common.util.Range;

/**
 * This is a configuration entry that is used to display a number range entry in
 * the configuration dialog. So a simple yes/no option.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class NumberEntry implements ConfigEntry {
    /**
     * The configuration that is controlled by this text entry.
     */
    private Config cfg;

    /**
     * The key in the configuration that is handled by this configuration.
     */
    private final String configEntry;

    /**
     * The range the value is allowed to work in.
     */
    private final Range range;

    /**
     * Create a new configuration entry that is handled by this entry.
     * 
     * @param entry the configuration key that is handled by this text entry
     */
    public NumberEntry(final String entry) {
        this(entry, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Create a new configuration entry that is handled by this entry.
     * 
     * @param entry the configuration key that is handled by this text entry
     * @param minLimit the minimal value that is allowed for this entry
     * @param maxLimit the maximal value that is allowed for this entry
     */
    public NumberEntry(final String entry, final int minLimit,
        final int maxLimit) {
        configEntry = entry;
        range = new Range(minLimit, maxLimit);
    }

    /**
     * Get the range this number entry is allowed to use.
     * 
     * @return the range of this number entry
     */
    public Range getRange() {
        return range;
    }

    /**
     * Get the value set in the configuration for this check entry.
     * 
     * @return the configuration stored for this check entry
     */
    public int getValue() {
        return FastMath.clamp(cfg.getInteger(configEntry), range);
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
    public void setValue(final int newValue) {
        cfg.set(configEntry, FastMath.clamp(newValue, range));
    }
}
