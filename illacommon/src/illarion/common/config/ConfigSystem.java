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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javolution.util.FastComparator;
import javolution.util.FastMap;
import javolution.util.FastTable;
import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

/**
 * This is the main class for the configuration system. It contains the storage
 * for the configuration values and allows to apply changes to those values.
 * <p>
 * This class is fully thread save as the access is synchronized using a
 * read/write lock. So reading access will work mostly without synchronization.
 * How ever in case there are any changes done to the configuration or the
 * configuration is saved or load the other parts of the application can't
 * access the data of this configuration and are blocked until its save to read
 * again.
 * </p>
 * 
 * @serial exclude
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class ConfigSystem implements Config {
    /**
     * The string in this constant is displayed in case a value is requested
     * from the configuration that was not set.
     */
    private static final String CONFIG_ENTRY_NOT_SET =
        "No config entry found for: "; //$NON-NLS-1$

    /**
     * The string in this constant is displayed in case a value requested from
     * the configuration holds a invalid object.
     */
    private static final String CONFIG_ILLEGAL_ENTRY =
        "Illegal config entry for: "; //$NON-NLS-1$

    /**
     * This message is displayed in case a listener is added twice.
     */
    private static final String DOUBLE_ADD_LISTENER =
        "Listener double added: "; //$NON-NLS-1$

    /**
     * The encoding used to encode the XML configuration files.
     */
    private static final String ENCODING = "UTF-8"; //$NON-NLS-1$

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ConfigSystem.class);

    /**
     * This message is displayed in case a listener is removed from the
     * configuration system that was not added.
     */
    private static final String REMOVE_NOT_ADDED_LISTENER =
        "Removed Listener not added: "; //$NON-NLS-1$

    /**
     * The name of the root node in the XML file.
     */
    private static final String ROOT_NAME = "config"; //$NON-NLS-1$

    /**
     * The XML binding used to save and load the xml configuration files.
     */
    private final XMLBinding binding;

    /**
     * This flag is set to <code>true</code> in case any changes where applied
     * to the configuration. Only in case those changes got applied the
     * configuration file needs to be saved at all.
     */
    private boolean changed;

    /**
     * The load entries of the configuration file.
     */
    private final Map<String, Object> configEntries;

    /**
     * The file that stores the configuration.
     */
    private final File configFile;

    /**
     * In this table the listeners are stored that monitor all keys of this
     * configuration.
     */
    private FastTable<ConfigChangeListener> configListeners;

    /**
     * In this map the listeners that monitor a single key of the configuration
     * system.
     */
    private FastMap<String, FastTable<ConfigChangeListener>> keyConfigListeners;

    /**
     * This lock is used to synchronize the access to the configuration system
     * properly. A read write lock is used here because most of the time the
     * configuration will be accessed reading.
     */
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

        final FastMap<String, Object> map = new FastMap<String, Object>();
        map.setKeyComparator(FastComparator.STRING);

        configEntries = map;
        binding = new XMLBinding();
        binding.setClassAttribute("type"); //$NON-NLS-1$
        binding.setAlias(Byte.class, "byte");
        binding.setAlias(Short.class, "short");
        binding.setAlias(Integer.class, "int");
        binding.setAlias(Long.class, "long");
        binding.setAlias(String.class, "string");
        binding.setAlias(FastMap.class, "fastmap");

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
    public ConfigSystem(final String source) {
        this(new File(source));
    }

    /**
     * Add a listener to this configuration that monitors changes to the
     * configuration.
     * 
     * @param listener the listener that is called in case the configuration
     *            changes
     */
    @Override
    public void addListener(final ConfigChangeListener listener) {
        lock.writeLock().lock();
        try {
            if (configListeners == null) {
                configListeners = FastTable.newInstance();
            } else if (configListeners.contains(listener)) {
                LOGGER.warn(DOUBLE_ADD_LISTENER + listener.toString());
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
     * @param key the key that needs to be changed so cause a call of the
     *            listener
     * @param listener the listener that is called in case the configuration
     *            changes
     */
    @Override
    public void addListener(final String key,
        final ConfigChangeListener listener) {
        lock.writeLock().lock();
        try {
            if (keyConfigListeners == null) {
                keyConfigListeners = FastMap.newInstance();
            }

            FastTable<ConfigChangeListener> usedTable =
                keyConfigListeners.get(key);
            if (usedTable == null) {
                usedTable = FastTable.newInstance();
                keyConfigListeners.put(key, usedTable);
            } else if (usedTable.contains(listener)) {
                LOGGER.warn(DOUBLE_ADD_LISTENER + listener.toString());
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
     *         <code>false</code> in case no value is set
     */
    @Override
    public boolean getBoolean(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return false;
        }

        if (!(value instanceof Boolean)) {
            LOGGER.warn(CONFIG_ILLEGAL_ENTRY + key);
            return false;
        }

        return ((Boolean) value).booleanValue();
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
            LOGGER.warn(CONFIG_ILLEGAL_ENTRY + key);
            return 0;
        }

        return ((Byte) value).byteValue();
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a double value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>0</code> in case there is no value set for this key
     */
    @Override
    public double getDouble(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return 0.d;
        }

        if (!(value instanceof Double)) {
            LOGGER.warn(CONFIG_ILLEGAL_ENTRY + key);
            return 0.d;
        }

        return ((Double) value).doubleValue();
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a File value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>null</code> in case there is no value set for this key
     */
    @Override
    public File getFile(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return null;
        }

        if (!(value instanceof String)) {
            LOGGER.warn(CONFIG_ILLEGAL_ENTRY + key);
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
     *         <code>0</code> in case there is no value set for this key
     */
    @Override
    public float getFloat(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return 0.f;
        }

        if (!(value instanceof Float)) {
            LOGGER.warn(CONFIG_ILLEGAL_ENTRY + key);
            return 0.f;
        }

        return ((Float) value).floatValue();
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a integer value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>0</code> in case there is no value set for this key
     */
    @Override
    public int getInteger(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Integer)) {
            LOGGER.warn(CONFIG_ILLEGAL_ENTRY + key);
            return 0;
        }

        return ((Integer) value).intValue();
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a long value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>0</code> in case there is no value set for this key
     */
    @Override
    public long getLong(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Long)) {
            LOGGER.warn(CONFIG_ILLEGAL_ENTRY + key);
            return 0;
        }

        return ((Long) value).longValue();
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a Object value.
     * 
     * @param key the key of that value
     * @return the value that was read from the configuration or
     *         <code>null</code> in case no value is set
     */
    public Object getObject(final String key) {
        Object value;
        lock.readLock().lock();
        try {
            value = configEntries.get(key);
        } finally {
            lock.readLock().unlock();
        }

        if (value == null) {
            LOGGER.warn(CONFIG_ENTRY_NOT_SET + key);
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
     *         <code>0</code> in case there is no value set for this key
     */
    @Override
    public short getShort(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Short)) {
            LOGGER.warn(CONFIG_ILLEGAL_ENTRY + key);
            return 0;
        }

        return ((Short) value).shortValue();
    }

    /**
     * Get one entry of the configuration file. In this case the value is read
     * as a String value.
     * 
     * @param key the key of the value
     * @return the value that was read from the configuration file or
     *         <code>null</code> in case there is no value set for this key
     */
    @Override
    public String getString(final String key) {
        final Object value = getObject(key);

        if (value == null) {
            return null;
        }

        if (!(value instanceof String)) {
            LOGGER.warn(CONFIG_ILLEGAL_ENTRY + key);
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
    public void removeListener(final ConfigChangeListener listener) {
        lock.writeLock().lock();
        try {
            if (configListeners == null) {
                LOGGER.warn(REMOVE_NOT_ADDED_LISTENER + listener.toString());
                return;
            }

            if (configListeners.remove(listener)) {
                if (configListeners.isEmpty()) {
                    FastTable.recycle(configListeners);
                    configListeners = null;
                }
            } else {
                LOGGER.warn(REMOVE_NOT_ADDED_LISTENER + listener.toString());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Remove a listener from the configuration system. In this case the
     * listener is removed from the monitor of one single key.
     * 
     * @param key the key the listener is supposed to be removed from
     * @param listener the listener to remove
     */
    @Override
    public void removeListener(final String key,
        final ConfigChangeListener listener) {
        lock.writeLock().lock();
        try {
            if (keyConfigListeners == null) {
                LOGGER.warn(REMOVE_NOT_ADDED_LISTENER + listener.toString());
                return;
            }

            final FastTable<ConfigChangeListener> usedTable =
                keyConfigListeners.get(key);

            if (usedTable == null) {
                LOGGER.warn(REMOVE_NOT_ADDED_LISTENER + listener.toString());
                return;
            }

            if (usedTable.remove(listener)) {
                if (usedTable.isEmpty()) {
                    FastTable.recycle(usedTable);
                    keyConfigListeners.remove(key);

                    if (keyConfigListeners.isEmpty()) {
                        FastMap.recycle(keyConfigListeners);
                        keyConfigListeners = null;
                    }
                }
            } else {
                LOGGER.warn(REMOVE_NOT_ADDED_LISTENER + listener.toString());
                return;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Calling this function causes the configuration system to save its values.
     * This only saves the configuration to the file system. The data is still
     * available in this class.
     */
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
            LOGGER
                .warn("Configuration not saved: config file set to illegal value.");
            return;
        }

        if (configFile.exists()) {
            if (!configFile.delete()) {
                LOGGER
                    .warn("Configuration not saved: failed to access the config file.");
                return;
            }
        }

        XMLObjectWriter xmlWriter = null;
        lock.writeLock().lock();
        try {
            xmlWriter =
                XMLObjectWriter.newInstance(new GZIPOutputStream(
                    new FileOutputStream(configFile)), ENCODING);
            xmlWriter.setBinding(binding);
            xmlWriter.setIndentation("\t");

            xmlWriter.write(configEntries, ROOT_NAME);
            xmlWriter.flush();
        } catch (final XMLStreamException e) {
            LOGGER.error("Configuration not saved: config data invalid.");
            return;
        } catch (final IOException e) {
            LOGGER
                .error("Configuration not saved: error accessing config file.");
            return;
        } finally {
            if (xmlWriter != null) {
                try {
                    xmlWriter.close();
                } catch (final XMLStreamException e) {
                    LOGGER.error("Error while closing the config file writer",
                        e);
                }
                xmlWriter = null;
            }
            lock.writeLock().unlock();
        }
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a boolean value.
     * 
     * @param key the key the value is stored with
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
     * @param key the key the value is stored with
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
     * @param key the key the value is stored with
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Override
    public void set(final String key, final File value) {
        set(key, value.getPath());
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a float value.
     * 
     * @param key the key the value is stored with
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
     * @param key the key the value is stored with
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
     * @param key the key the value is stored with
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void set(final String key, final Object value) {
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
     * @param key the key the value is stored with
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
     * @param key the key the value is stored with
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final boolean value) {
        if (!configEntries.containsKey(key)) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final byte value) {
        if (!configEntries.containsKey(key)) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final double value) {
        if (!configEntries.containsKey(key)) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final File value) {
        if (!configEntries.containsKey(key)) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final float value) {
        if (!configEntries.containsKey(key)) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final int value) {
        if (!configEntries.containsKey(key)) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final long value) {
        if (!configEntries.containsKey(key)) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final short value) {
        if (!configEntries.containsKey(key)) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(final String key, final String value) {
        if (!configEntries.containsKey(key)) {
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

        XMLObjectReader xmlReader = null;
        lock.writeLock().lock();
        try {
            xmlReader =
                XMLObjectReader.newInstance(new GZIPInputStream(
                    new FileInputStream(configFile)), ENCODING);
            xmlReader.setBinding(binding);

            final Map<String, Object> loadedMap = xmlReader.read(ROOT_NAME);
            if (loadedMap == null) {
                LOGGER.warn("Configuration not loaded: no config data load");
                return;
            }

            configEntries.putAll(loadedMap);

            loadedMap.clear();
            if (loadedMap instanceof FastMap) {
                FastMap.recycle((FastMap<String, Object>) loadedMap);
            }
        } catch (final FileNotFoundException e) {
            LOGGER.warn("Configuration not loaded: config file disappeared.");
            return;
        } catch (final ClassCastException e) {
            LOGGER.error("Configuration not loaded: illegal config data.");
            return;
        } catch (final XMLStreamException e) {
            LOGGER.error("Configuration not loaded: config file invalid.");
            return;
        } catch (final IOException e) {
            LOGGER
                .error("Configuration not loaded: error accessing the file system.");
            return;
        } finally {
            if (xmlReader != null) {
                try {
                    xmlReader.close();
                } catch (final XMLStreamException e) {
                    LOGGER.error("Error while closing the config file reader",
                        e);
                }
                xmlReader = null;
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
    private void reportChangedKey(final String key) {
        changed = true;

        if (configListeners != null) {
            final int count = configListeners.size();
            for (int i = 0; i < count; i++) {
                configListeners.get(i).configChanged(this, key);
            }
        }

        if (keyConfigListeners != null) {
            final FastTable<ConfigChangeListener> list =
                keyConfigListeners.get(key);
            if (list != null) {
                final int count = list.size();
                for (int i = 0; i < count; i++) {
                    list.get(i).configChanged(this, key);
                }
            }
        }
    }
}
