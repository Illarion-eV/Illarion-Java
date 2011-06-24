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
package illarion.common.config;

import java.io.File;

/**
 * This interface offers reduced access to the configuration system. Accessing
 * this interface should be enough for the most cases.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface Config {
    /**
     * Add a listener to this configuration that monitors changes to the
     * configuration.
     * 
     * @param listener the listener that is called in case the configuration
     *            changes
     */
    void addListener(ConfigChangeListener listener);

    /**
     * Add a listener to this configuration that monitors changes to the
     * configuration. In this case the listener will only react on changes to to
     * one key of the configuration.
     * 
     * @param key the key that needs to be changed so cause a call of the
     *            listener
     * @param listener the listener that is called in case the configuration
     *            changes
     */
    void addListener(String key, ConfigChangeListener listener);

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a boolean value.
     * 
     * @param key the key of that value
     * @return the value that was read from the configuration or
     *         <code>false</code> in case no value is set
     */
    boolean getBoolean(String key);

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a byte value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or 0 in case
     *         there is no value set for this key
     */
    byte getByte(String key);

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a double value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>0</code> in case there is no value set for this key
     */
    double getDouble(String key);

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a File value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>null</code> in case there is no value set for this key
     */
    File getFile(String key);

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a float value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>0</code> in case there is no value set for this key
     */
    float getFloat(String key);

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a integer value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>0</code> in case there is no value set for this key
     */
    int getInteger(String key);

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a long value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>0</code> in case there is no value set for this key
     */
    long getLong(String key);

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a short value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>0</code> in case there is no value set for this key
     */
    short getShort(String key);

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a String value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>null</code> in case there is no value set for this key
     */
    String getString(String key);

    /**
     * Remove one entry from the configuration. That causes that the value is
     * not available at all any longer. Only use this function in case you are
     * absolutely sure what you are doing. This causes that not even the default
     * value is available anymore for that session unless its defined by hand
     * again.
     * 
     * @param key the key of the entry that is supposed to be removed
     */
    void remove(String key);

    /**
     * Remove a listener from the configuration system.
     * 
     * @param listener the listener to remove
     */
    void removeListener(ConfigChangeListener listener);

    /**
     * Remove a listener from the configuration system. In this case the
     * listener is removed from the monitor of one single key.
     * 
     * @param key the key the listener is supposed to be removed from
     * @param listener the listener to remove
     */
    void removeListener(String key, ConfigChangeListener listener);

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a boolean value.
     * 
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(String key, boolean value);

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a byte value.
     * 
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(String key, byte value);

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a double value.
     * 
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(String key, double value);

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a file.
     * 
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(String key, File value);

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a float value.
     * 
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(String key, float value);

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a integer value.
     * 
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(String key, int value);

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a long value.
     * 
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(String key, long value);

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a short value.
     * 
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(String key, short value);

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a String value.
     * 
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(String key, String value);
}
