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
import illarion.common.util.FastMath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * This is a configuration entry that is used to display a combo box in the
 * configuration dialog. So a selection of multiple possible values.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
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
    @Nonnull
    private static final String ERROR_STORE_TYPE = "Illegal store type chosen.";

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
     * The texts that are displayed for each entry.
     */
    @Nonnull
    private final String[] labels;

    /**
     * The options that are select able by the ComboBox described with this entry.
     */
    @Nonnull
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
    public SelectEntry(@Nonnull String entry, int store, @Nonnull Object... option) {
        configEntry = entry;
        storeValue = store;
        options = Arrays.copyOf(option, option.length);

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
    @SuppressWarnings("MethodCanBeVariableArityMethod")
    public SelectEntry(@Nonnull String entry, int store, @Nonnull Object[] option, @Nonnull String[] label) {
        configEntry = entry;
        storeValue = store;
        options = Arrays.copyOf(option, option.length);
        labels = Arrays.copyOf(label, label.length);
    }

    /**
     * Get the index selected of the currently stored list.
     *
     * @return the index that is stored in the GUI currently.
     */
    public int getIndex() {
        if (cfg == null) {
            throw new IllegalStateException("The reference to the config system was not set.");
        }
        switch (storeValue) {
            case STORE_INDEX:
                return FastMath.clamp(cfg.getInteger(configEntry), 0, options.length - 1);
            case STORE_VALUE:
                String value = cfg.getString(configEntry);
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
    @Nonnull
    public Collection<Object> getItems() {
        return Arrays.asList(options);
    }

    /**
     * Get the currently displayed label.
     *
     * @return the label that is supposed to be displayed currently.
     */
    @Nonnull
    public String getLabel() {
        if (cfg == null) {
            throw new IllegalStateException("The reference to the config system was not set.");
        }
        switch (storeValue) {
            case STORE_INDEX:
                return labels[FastMath.clamp(cfg.getInteger(configEntry), 0, options.length - 1)];
            case STORE_VALUE:
                String value = cfg.getString(configEntry);
                for (int i = 0; i < options.length; i++) {
                    if (options[i].toString().equals(value)) {
                        return labels[i];
                    }
                }
                return Objects.requireNonNull(labels[0]);
            default:
                throw new IllegalStateException(ERROR_STORE_TYPE);
        }
    }

    /**
     * Get the names of the items in this select entry in the proper order.
     *
     * @return a array of all possible values
     */
    @Nonnull
    public Collection<String> getLabels() {
        return Arrays.asList(labels);
    }

    /**
     * Get the value set in the configuration for this check entry.
     *
     * @return the configuration stored for this check entry
     */
    @Nonnull
    public Object getValue() {
        if (cfg == null) {
            throw new IllegalStateException("The reference to the config system was not set.");
        }
        switch (storeValue) {
            case STORE_INDEX:
                return options[FastMath.clamp(cfg.getInteger(configEntry), 0, options.length - 1)];
            case STORE_VALUE:
                String value = cfg.getString(configEntry);
                for (Object option : options) {
                    if (option.toString().equals(value)) {
                        return option;
                    }
                }
                return Objects.requireNonNull(options[0]);
            default:
                throw new IllegalStateException(ERROR_STORE_TYPE);
        }
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
            throw new IllegalStateException("The reference to the config system was not set.");
        }
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
    public void setValue(@Nonnull Object newValue) {
        if (cfg == null) {
            throw new IllegalStateException("The reference to the config system was not set.");
        }
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
