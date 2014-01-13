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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bushe.swing.event.EventBus;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This is the main class for the configuration system. It contains the storage for the configuration values and
 * allows to apply changes to those values.
 * <p>
 * This class is fully thread save as the access is synchronized using a read/write lock. So reading access will work
 * mostly without synchronization. How ever in case there are any changes done to the configuration or the
 * configuration is saved or load the other parts of the application can't access the data of this configuration and
 * are blocked until its save to read again.
 * </p>
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ConfigSystem implements Config {
    /**
     * The encoding used to encode the XML configuration files.
     */
    private static final String ENCODING = "UTF-8"; //$NON-NLS-1$

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigSystem.class);

    /**
     * The name of the root node in the XML file.
     */
    private static final String ROOT_NAME = "config"; //$NON-NLS-1$

    /**
     * This flag is set to {@code true} in case any changes where applied
     * to the configuration. Only in case those changes got applied the
     * configuration file needs to be saved at all.
     */
    private boolean changed;

    /**
     * The load entries of the configuration file.
     */
    @Nonnull
    private final Map<String, Object> configEntries;

    /**
     * The file that stores the configuration.
     */
    private final File configFile;

    /**
     * In this table the listeners are stored that monitor all keys of this
     * configuration.
     */
    @Nullable
    @Deprecated
    private Collection<ConfigChangeListener> configListeners;

    /**
     * In this map the listeners that monitor a single key of the configuration
     * system.
     */
    @Nullable
    @Deprecated
    private Map<String, Collection<ConfigChangeListener>> keyConfigListeners;

    /**
     * This lock is used to synchronize the access to the configuration system
     * properly. A read write lock is used here because most of the time the
     * configuration will be accessed reading.
     */
    @Nonnull
    private final ReadWriteLock lock;

    /**
     * Create a configuration object with a file as source. The configuration
     * system will try to load the data from this source.
     *
     * @param source The configuration file that is supposed to be load
     */
    @SuppressWarnings("nls")
    public ConfigSystem(final File source) {
        configFile = source;

        configEntries = new HashMap<>();

        lock = new ReentrantReadWriteLock();

        loadConfig();
        changed = false;
    }

    /**
     * Create a configuration object with a file as source. The configuration
     * system will try to load the data from this source.
     *
     * @param source The configuration file that is supposed to be load
     */
    public ConfigSystem(@Nonnull final String source) {
        this(new File(source));
    }

    /**
     * Add a listener to this configuration that monitors changes to the
     * configuration.
     *
     * @param listener the listener that is called in case the configuration
     *                 changes
     */
    @Override
    @Deprecated
    public void addListener(@Nonnull final ConfigChangeListener listener) {
        lock.writeLock().lock();
        try {
            if (configListeners == null) {
                configListeners = new ArrayList<>();
            } else if (configListeners.contains(listener)) {
                LOGGER.warn("Listener double added: " + listener.toString());
                return;
            }

            configListeners.add(listener);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Add a listener to this configuration that monitors changes to the
     * configuration. In this case the listener will only react on changes to to
     * one key of the configuration.
     *
     * @param key      the key that needs to be changed so cause a call of the
     *                 listener
     * @param listener the listener that is called in case the configuration
     *                 changes
     */
    @Override
    @Deprecated
    public void addListener(final String key,
                            @Nonnull final ConfigChangeListener listener) {
        lock.writeLock().lock();
        try {
            if (keyConfigListeners == null) {
                keyConfigListeners = new HashMap<>();
            }

            Collection<ConfigChangeListener> usedTable = keyConfigListeners.get(key);
            if (usedTable == null) {
                usedTable = new ArrayList<>();
                keyConfigListeners.put(key, usedTable);
            } else if (usedTable.contains(listener)) {
                LOGGER.warn("Listener double added: " + listener.toString());
                return;
            }

            usedTable.add(listener);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a boolean value.
     *
     * @param key the key of that value
     * @return the value that was read from the configuration or
     *         {@code false} in case no value is set
     */
    @Override
    public boolean getBoolean(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return false;
        }

        if (!(value instanceof Boolean)) {
            LOGGER.warn("Illegal config entry for: " + key);
            return false;
        }

        return (Boolean) value;
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a byte value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or 0 in case
     *         there is no value set for this key
     */
    @Override
    public byte getByte(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Byte)) {
            LOGGER.warn("Illegal config entry for: " + key);
            return 0;
        }

        return (Byte) value;
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a double value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         {@code 0} in case there is no value set for this key
     */
    @Override
    public double getDouble(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return 0.d;
        }

        if (!(value instanceof Double)) {
            LOGGER.warn("Illegal config entry for: " + key);
            return 0.d;
        }

        return (Double) value;
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a File value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         {@code null} in case there is no value set for this key
     */
    @Nullable
    @Override
    public File getFile(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return null;
        }

        if (!(value instanceof String)) {
            LOGGER.warn("Illegal config entry for: " + key);
            return null;
        }

        return new File(value.toString());
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a float value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         {@code 0} in case there is no value set for this key
     */
    @Override
    public float getFloat(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return 0.f;
        }

        if (!(value instanceof Float)) {
            LOGGER.warn("Illegal config entry for: " + key);
            return 0.f;
        }

        return (Float) value;
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a integer value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         {@code 0} in case there is no value set for this key
     */
    @Override
    public int getInteger(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Integer)) {
            LOGGER.warn("Illegal config entry for: " + key);
            return 0;
        }

        return (Integer) value;
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a long value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         {@code 0} in case there is no value set for this key
     */
    @Override
    public long getLong(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Long)) {
            LOGGER.warn("Illegal config entry for: " + key);
            return 0;
        }

        return (Long) value;
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a Object value.
     *
     * @param key the key of that value
     * @return the value that was read from the configuration or
     *         {@code null} in case no value is set
     */
    @Nullable
    public Object getObject(final String key) {
        lock.readLock().lock();
        Object value;
        try {
            value = configEntries.get(key);
        } finally {
            lock.readLock().unlock();
        }

        if (value == null) {
            LOGGER.warn("No config entry found for: " + key);
            return null;
        }

        return value;
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a short value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         {@code 0} in case there is no value set for this key
     */
    @Override
    public short getShort(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Short)) {
            LOGGER.warn("Illegal config entry for: " + key);
            return 0;
        }

        return (Short) value;
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a String value.
     *
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         {@code null} in case there is no value set for this key
     */
    @Nullable
    @Override
    public String getString(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return null;
        }

        if (!(value instanceof String)) {
            LOGGER.warn("Illegal config entry for: " + key);
            return null;
        }

        return value.toString();
    }

    /**
     * Remove one entry from the configuration. That causes that the value is
     * not available at all any longer. Only use this function in case you are
     * absolutely sure what you are doing. This causes that not even the default
     * value is available anymore for that session unless its defined by hand
     * again.
     *
     * @param key the key of the entry that is supposed to be removed
     */
    @Override
    public void remove(final String key) {
        configEntries.remove(key);
    }

    /**
     * Remove a listener from the configuration system.
     *
     * @param listener the listener to remove
     */
    @Override
    @Deprecated
    public void removeListener(@Nonnull final ConfigChangeListener listener) {
        lock.writeLock().lock();
        try {
            if (configListeners == null) {
                LOGGER.warn("Removed Listener not added: " + listener.toString());
                return;
            }

            if (configListeners.remove(listener)) {
                if (configListeners.isEmpty()) {
                    configListeners = null;
                }
            } else {
                LOGGER.warn("Removed Listener not added: " + listener.toString());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Remove a listener from the configuration system. In this case the
     * listener is removed from the monitor of one single key.
     *
     * @param key      the key the listener is supposed to be removed from
     * @param listener the listener to remove
     */
    @Override
    @Deprecated
    public void removeListener(final String key,
                               @Nonnull final ConfigChangeListener listener) {
        lock.writeLock().lock();
        try {
            if (keyConfigListeners == null) {
                LOGGER.warn("Removed Listener not added: " + listener.toString());
                return;
            }

            final Collection<ConfigChangeListener> usedTable = keyConfigListeners.get(key);

            if (usedTable == null) {
                LOGGER.warn("Removed Listener not added: " + listener.toString());
                return;
            }

            if (usedTable.remove(listener)) {
                if (usedTable.isEmpty()) {
                    keyConfigListeners.remove(key);

                    if (keyConfigListeners.isEmpty()) {
                        keyConfigListeners = null;
                    }
                }
            } else {
                LOGGER.warn("Removed Listener not added: " + listener.toString());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private interface ConfigTypeConverter {
        String getString(@Nonnull Object object);
        Object getObject(@Nonnull String string);
    }

    private abstract static class AbstractConfigTypeConverter implements ConfigTypeConverter {
        @Override
        public final String getString(@Nonnull final Object object) {
            return object.toString();
        }
    }

    private enum ConfigTypes {
        BooleanEntry("bool", Boolean.class, new AbstractConfigTypeConverter() {
            @Override
            public Boolean getObject(@Nonnull final String string) {
                return Boolean.valueOf(string);
            }
        }),
        ByteEntry("byte", Byte.class, new AbstractConfigTypeConverter() {
            @Override
            public Byte getObject(@Nonnull final String string) {
                return Byte.valueOf(string);
            }
        }),
        DoubleEntry("double", Double.class, new AbstractConfigTypeConverter() {
            @Override
            public Double getObject(@Nonnull final String string) {
                return Double.valueOf(string);
            }
        }),
        FileEntry("file", File.class, new ConfigTypeConverter() {
            @Nonnull
            @Override
            public String getString(@Nonnull final Object object) {
                return ((File) object).getPath();
            }
            @Nonnull
            @Override
            public File getObject(@Nonnull final String string) {
                return new File(string);
            }
        }),
        FloatEntry("float", Float.class, new AbstractConfigTypeConverter() {
            @Override
            public Float getObject(@Nonnull final String string) {
                return Float.valueOf(string);
            }
        }),
        IntegerEntry("int", Integer.class, new AbstractConfigTypeConverter() {
            @Override
            public Integer getObject(@Nonnull final String string) {
                return Integer.valueOf(string);
            }
        }),
        LongEntry("long", Long.class, new AbstractConfigTypeConverter() {
            @Override
            public Long getObject(@Nonnull final String string) {
                return Long.valueOf(string);
            }
        }),
        ShortEntry("short", Short.class, new AbstractConfigTypeConverter() {
            @Override
            public Short getObject(@Nonnull final String string) {
                return Short.valueOf(string);
            }
        }),
        StringEntry("string", String.class, new AbstractConfigTypeConverter() {
            @Nonnull
            @Override
            public String getObject(@Nonnull final String string) {
                return string;
            }
        });


        private String typeName;
        private Class<?> typeClass;
        private ConfigTypeConverter converter;

        ConfigTypes(@Nonnull final String typeName, @Nonnull final Class<?> typeClass,
                    @Nonnull final ConfigTypeConverter converter) {
            this.typeClass = typeClass;
            this.typeName = typeName;
            this.converter = converter;
        }

        public String getTypeName() {
            return typeName;
        }

        public Class<?> getTypeClass() {
            return typeClass;
        }

        public ConfigTypeConverter getConverter() {
            return converter;
        }
    }

    /**
     * Calling this function causes the configuration system to save its values. This only saves the configuration to
     * the file system. The data is still available in this class.
     */
    @Override
    @SuppressWarnings("nls")
    public void save() {
        if (!changed) {
            return; // no changes applied
        }
        if (configFile == null) {
            LOGGER.info("Configuration not saved: config file not set.");
            return;
        }

        if (configFile.exists() && !configFile.isFile()) {
            LOGGER.warn("Configuration not saved: config file set to illegal value.");
            return;
        }

        if (configFile.exists()) {
            if (!configFile.delete()) {
                LOGGER.warn("Configuration not saved: failed to access the config file.");
                return;
            }
        }

        lock.writeLock().lock();
        OutputStream stream = null;
        try {
            final XmlSerializer serializer = XmlPullParserFactory.newInstance().newSerializer();
            stream = new GZIPOutputStream(new FileOutputStream(configFile));
            serializer.setOutput(stream, ENCODING);

            serializer.startDocument(ENCODING, true);
            serializer.startTag(null, ROOT_NAME);

            for (Map.Entry<String, Object> entry : configEntries.entrySet()) {
                final String key = entry.getKey();
                final Class<?> valueClass = entry.getValue().getClass();
                String value = null;
                String type = null;
                for (final ConfigTypes configType : ConfigTypes.values()) {
                    if (configType.getTypeClass().equals(valueClass)) {
                        type = configType.getTypeName();
                        value = configType.getConverter().getString(entry.getValue());
                        break;
                    }
                }

                if (value == null || type == null) {
                    continue;
                }

                serializer.startTag(null, "entry");
                serializer.attribute(null, "key", key);
                serializer.attribute(null, "type", type);
                serializer.attribute(null, "value", value);
                serializer.endTag(null, "entry");
            }

            serializer.endTag(null, ROOT_NAME);
            serializer.endDocument();
            serializer.flush();
            stream.flush();
            changed = false;
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Configuration not saved: error accessing config file.");
        } catch (@Nonnull final XmlPullParserException e) {
            LOGGER.error("Configuration not saved: Error creating XML serializer");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (@Nonnull final IOException e) {
                    LOGGER.error("Error while closing the config file writer",
                            e);
                }
            }
            lock.writeLock().unlock();
        }
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a boolean value.
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Override
    public void set(final String key, final boolean value) {
        set(key, Boolean.valueOf(value));
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a byte value.
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Override
    public void set(final String key, final byte value) {
        set(key, Byte.valueOf(value));
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a double value.
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Override
    public void set(final String key, final double value) {
        set(key, Double.valueOf(value));
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a file.
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Override
    public void set(final String key, @Nonnull final File value) {
        set(key, value.getPath());
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a float value.
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Override
    public void set(final String key, final float value) {
        set(key, Float.valueOf(value));
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a integer value.
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Override
    public void set(final String key, final int value) {
        set(key, Integer.valueOf(value));
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a integer value.
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Override
    public void set(final String key, final long value) {
        set(key, Long.valueOf(value));
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a Object value.
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void set(final String key, @Nonnull final Object value) {
        lock.writeLock().lock();
        try {
            if (value.equals(configEntries.get(key))) {
                return;
            }

            configEntries.put(key, value);
            reportChangedKey(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a short value.
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Override
    public void set(final String key, final short value) {
        set(key, Short.valueOf(value));
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a String value.
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Override
    public void set(final String key, final String value) {
        set(key, (Object) value);
    }

    /**
     * Set the default value for a key. In this case the value is a boolean
     * value. Setting default values does basically the same as setting the
     * normal values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final boolean value) {
        if (!(configEntries.get(key) instanceof Boolean)) {
            set(key, value);
        }
    }

    /**
     * Set the default value for a key. In this case the value is a byte value.
     * Setting default values does basically the same as setting the normal
     * values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final byte value) {
        if (!(configEntries.get(key) instanceof Byte)) {
            set(key, value);
        }
    }

    /**
     * Set the default value for a key. In this case the value is a double
     * value. Setting default values does basically the same as setting the
     * normal values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final double value) {
        if (!(configEntries.get(key) instanceof Double)) {
            set(key, value);
        }
    }

    /**
     * Set the default value for a key. In this case the value is a File value.
     * Setting default values does basically the same as setting the normal
     * values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, @Nonnull final File value) {
        if (!(configEntries.get(key) instanceof String)) {
            set(key, value);
        }
    }

    /**
     * Set the default value for a key. In this case the value is a float value.
     * Setting default values does basically the same as setting the normal
     * values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final float value) {
        if (!(configEntries.get(key) instanceof Float)) {
            set(key, value);
        }
    }

    /**
     * Set the default value for a key. In this case the value is a integer
     * value. Setting default values does basically the same as setting the
     * normal values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final int value) {
        if (!(configEntries.get(key) instanceof Integer)) {
            set(key, value);
        }
    }

    /**
     * Set the default value for a key. In this case the value is a long value.
     * Setting default values does basically the same as setting the normal
     * values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final long value) {
        if (!(configEntries.get(key) instanceof Long)) {
            set(key, value);
        }
    }

    /**
     * Set the default value for a key. In this case the value is a short value.
     * Setting default values does basically the same as setting the normal
     * values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final short value) {
        if (!(configEntries.get(key) instanceof Short)) {
            set(key, value);
        }
    }

    /**
     * Set the default value for a key. In this case the value is a String
     * value. Setting default values does basically the same as setting the
     * normal values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key   the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final String value) {
        if (!(configEntries.get(key) instanceof String)) {
            set(key, value);
        }
    }

    /**
     * Load the configuration from the file system.
     */
    @SuppressWarnings("nls")
    private void loadConfig() {
        if (configFile == null) {
            LOGGER.info("Configuration not loaded: config file not set.");
            return;
        }

        if (!configFile.exists() || !configFile.isFile()) {
            LOGGER.warn("Configuration not loaded: config file not found.");
            return;
        }

        lock.writeLock().lock();
        InputStream stream = null;
        try {
            final XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            stream = new GZIPInputStream(new FileInputStream(configFile));
            parser.setInput(stream, ENCODING);

            final Map<String, Object> loadedMap = new HashMap<>();

            int currentTag = parser.nextToken();
            while (currentTag != XmlPullParser.END_DOCUMENT) {
                if (currentTag == XmlPullParser.START_TAG && parser.getName().equals("entry")) {
                    String key = null;
                    String type = null;
                    String value = null;
                    final int count = parser.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        final String name = parser.getAttributeName(i);
                        switch (name) {
                            case "key":
                                key = parser.getAttributeValue(i);
                                break;
                            case "type":
                                type = parser.getAttributeValue(i);
                                break;
                            case "value":
                                value = parser.getAttributeValue(i);
                                break;
                        }
                    }
                    if ((key != null) && (type != null) && (value != null)) {
                        Object realValue = null;
                        for (final ConfigTypes configType : ConfigTypes.values()) {
                            if (type.equals(configType.getTypeName())) {
                                realValue = configType.getConverter().getObject(value);
                                break;
                            }
                        }
                        if (realValue != null) {
                            loadedMap.put(key, realValue);
                        }
                    }
                }
                currentTag = parser.nextToken();
            }

            if (loadedMap.isEmpty()) {
                LOGGER.warn("Configuration not loaded: no config data load");
                return;
            }

            configEntries.putAll(loadedMap);
        } catch (@Nonnull final FileNotFoundException e) {
            LOGGER.warn("Configuration not loaded: config file disappeared.");
        } catch (@Nonnull final ClassCastException e) {
            LOGGER.error("Configuration not loaded: illegal config data.");
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Configuration not loaded: error accessing the file system.");
        } catch (@Nonnull final XmlPullParserException e) {
            LOGGER.error("Error while creating XML pull parser.", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (@Nonnull final IOException ignored) {
                }
            }
            lock.writeLock().unlock();
        }
    }

    /**
     * Report the change of a entry of the configuration to all listeners set in
     * this configuration.
     *
     * @param key the key that was changed
     */
    @SuppressWarnings("deprecation")
    private void reportChangedKey(final String key) {
        changed = true;

        EventBus.publish(key, new ConfigChangedEvent(this, key));

        if (configListeners != null) {
            for (@Nonnull final ConfigChangeListener listener : configListeners) {
                listener.configChanged(this, key);
            }
        }

        if (keyConfigListeners != null) {
            final Collection<ConfigChangeListener> list = keyConfigListeners.get(key);
            if (list != null) {
                for (@Nonnull final ConfigChangeListener listener : list) {
                    listener.configChanged(this, key);
                }
            }
        }
    }
}
