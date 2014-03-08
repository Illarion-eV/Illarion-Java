/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;

/**
 * This interface offers reduced access to the configuration system. Accessing this interface should be enough for
 * the most cases.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Config {

    /**
     * Get one entry of the configuration file. In this case the value is read as a boolean value.
     *
     * @param key the key of that value
     * @return the value that was read from the configuration or <code>false</code> in case no value is set
     */
    boolean getBoolean(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a byte value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or 0 in case there is no value set for this key
     */
    byte getByte(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a double value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or <code>0</code> in case there is no value set
     * for this key
     */
    double getDouble(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a File value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or <code>null</code> in case there is no value set
     * for this key
     */
    @Deprecated
    @Nullable
    File getFile(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a Path value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or <code>null</code> in case there is no value set
     * for this key
     */
    @Nullable
    Path getPath(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a float value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or <code>0</code> in case there is no value set
     * for this key
     */
    float getFloat(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a integer value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or <code>0</code> in case there is no value set
     * for this key
     */
    int getInteger(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a long value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or <code>0</code> in case there is no value set
     * for this key
     */
    long getLong(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a short value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or <code>0</code> in case there is no value set
     * for this key
     */
    short getShort(@Nonnull String key);

    /**
     * Get one entry of the configuration file. In this case the value is read as a String value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or <code>null</code> in case there is no value set
     * for this key
     */
    @Nullable
    String getString(@Nonnull String key);

    /**
     * Save the current state of the configuration.
     */
    void save();

    /**
     * Remove one entry from the configuration. That causes that the value is not available at all any longer. Only
     * use this function in case you are absolutely sure what you are doing. This causes that not even the default
     * value is available anymore for that session unless its defined by hand again.
     *
     * @param key the key of the entry that is supposed to be removed
     */
    void remove(@Nonnull String key);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a boolean value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, boolean value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a byte value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, byte value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a double value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, double value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a file.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Deprecated
    void set(@Nonnull String key, @Nonnull File value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a path.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, @Nonnull Path value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a float value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, float value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a integer value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, int value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a long value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, long value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a short value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, short value);

    /**
     * Set one entry of the configuration file to a new value. In this case the value is a String value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    void set(@Nonnull String key, @Nonnull String value);
}
