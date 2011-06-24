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

/**
 * This is a configuration entry that is used to display a combo box in the
 * configuration dialog. So a selection of multiple possible values.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class SelectEntry implements ConfigEntry {
    /**
     * In case this value is set as store value the index of the entry is set as
     * integer value.
     */
    public static final int STORE_INDEX = 1;

    /**
     * In case this value is set as store value the string value of the entry is
     * set as string value.
     */
    public static final int STORE_VALUE = 2;

    /**
     * This is the message displayed in case the storage mode is set to a
     * illegal value.
     */
    @SuppressWarnings("nls")
    private static final String ERROR_STORE_TYPE =
        "Illegal store type chosen.";

    /**
     * The configuration that is controlled by this text entry.
     */
    private Config cfg;

    /**
     * The key in the configuration that is handled by this configuration.
     */
    private final String configEntry;

    /**
     * The texts that are displayed for each entry.
     */
    private final String[] labels;

    /**
     * The options that are select able by the ComboBox described with this
     * entry.
     */
    private final Object[] options;

    /**
     * The value that defines what values are stored.
     */
    private final int storeValue;

    /**
     * Create a new configuration entry that is handled by this entry.
     * 
     * @param entry the configuration key that is handled by this text entry
     * @param store the method used to store the values in the configuration
     * @param option the options to be displayed in this entry
     */
    public SelectEntry(final String entry, final int store,
        final Object... option) {
        configEntry = entry;
        storeValue = store;
        options = option;

        labels = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            labels[i] = options[i].toString();
        }
    }

    /**
     * Create a new configuration entry that is handled by this entry.
     * 
     * @param entry the configuration key that is handled by this text entry
     * @param store the method used to store the values in the configuration
     * @param option the options to be displayed in this entry
     * @param label the texts displayed for each entry
     */
    public SelectEntry(final String entry, final int store,
        final Object[] option, final String[] label) {
        configEntry = entry;
        storeValue = store;
        options = option;
        labels = label;
    }

    /**
     * Create a new configuration entry that is handled by this entry.
     * 
     * @param entry the configuration key that is handled by this text entry
     * @param option the options to be displayed in this entry
     */
    public SelectEntry(final String entry, final Object... option) {
        this(entry, STORE_INDEX, option);
    }

    /**
     * Create a new configuration entry that is handled by this entry.
     * 
     * @param entry the configuration key that is handled by this text entry
     * @param option the options to be displayed in this entry
     * @param label the texts displayed for each entry
     */
    public SelectEntry(final String entry, final Object[] option,
        final String[] label) {
        this(entry, STORE_INDEX, option, label);
    }

    /**
     * Get the index selected of the currently stored list.
     * 
     * @return the index that is stored in the GUI currently.
     */
    public int getIndex() {
        switch (storeValue) {
            case STORE_INDEX:
                return FastMath.clamp(cfg.getInteger(configEntry), 0,
                    options.length - 1);
            case STORE_VALUE:
                final String value = cfg.getString(configEntry);
                for (int i = 0; i < options.length; i++) {
                    if (options[i].toString().equals(value)) {
                        return i;
                    }
                }
                return 0;
            default:
                throw new IllegalStateException(ERROR_STORE_TYPE);
        }
    }

    /**
     * Get the items in this select entry in the proper order.
     * 
     * @return a array of all possible values
     */
    public Object[] getItems() {
        return options;
    }

    /**
     * Get the currently displayed label.
     * 
     * @return the label that is supposed to be displayed currently.
     */
    public String getLabel() {
        switch (storeValue) {
            case STORE_INDEX:
                return labels[FastMath.clamp(cfg.getInteger(configEntry), 0,
                    options.length - 1)];
            case STORE_VALUE:
                final String value = cfg.getString(configEntry);
                for (int i = 0; i < options.length; i++) {
                    if (options[i].toString().equals(value)) {
                        return labels[i];
                    }
                }
                return labels[0];
            default:
                throw new IllegalStateException(ERROR_STORE_TYPE);
        }
    }

    /**
     * Get the names of the items in this select entry in the proper order.
     * 
     * @return a array of all possible values
     */
    public String[] getLabels() {
        return labels;
    }

    /**
     * Get the value set in the configuration for this check entry.
     * 
     * @return the configuration stored for this check entry
     */
    public Object getValue() {
        switch (storeValue) {
            case STORE_INDEX:
                return options[FastMath.clamp(cfg.getInteger(configEntry), 0,
                    options.length - 1)];
            case STORE_VALUE:
                final String value = cfg.getString(configEntry);
                for (final Object option : options) {
                    if (option.toString().equals(value)) {
                        return option;
                    }
                }
                return options[0];
            default:
                throw new IllegalStateException(ERROR_STORE_TYPE);
        }
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
        switch (storeValue) {
            case STORE_INDEX:
                cfg.set(configEntry, newValue);
                break;
            case STORE_VALUE:
                cfg.set(configEntry, options[newValue].toString());
                break;
            default:
                throw new IllegalStateException(ERROR_STORE_TYPE);
        }
    }

    /**
     * Set the new value of the configuration entry that is controlled by this.
     * 
     * @param newValue the new configuration value
     */
    public void setValue(final Object newValue) {
        switch (storeValue) {
            case STORE_INDEX:
                for (int i = 0; i < options.length; i++) {
                    if (labels[i].equals(newValue.toString())) {
                        cfg.set(configEntry, i);
                        break;
                    }
                }
                break;
            case STORE_VALUE:
                cfg.set(configEntry, newValue.toString());
                break;
            default:
                throw new IllegalStateException(ERROR_STORE_TYPE);
        }
    }
}
