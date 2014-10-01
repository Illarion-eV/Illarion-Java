/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.common.config;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.nio.file.StandardOpenOption.*;

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
    @Nonnull
    private static final String ENCODING = "UTF-8"; //$NON-NLS-1$

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigSystem.class);

    /**
     * The name of the root node in the XML file.
     */
    @Nonnull
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
    @Nonnull
    private final Path configFile;

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
    @Deprecated
    @SuppressWarnings("nls")
    public ConfigSystem(@Nonnull File source) {
        this(source.toPath());
    }

    public ConfigSystem(@Nonnull Path source) {
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
    public ConfigSystem(@Nonnull String source) {
        this(new File(source));
    }

    @Override
    public boolean getBoolean(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return false;
        }

        if (!(value instanceof Boolean)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return false;
        }

        return (Boolean) value;
    }

    @Override
    public byte getByte(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Byte)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0;
        }

        return (Byte) value;
    }

    @Override
    public double getDouble(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return 0.d;
        }

        if (!(value instanceof Double)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0.d;
        }

        return (Double) value;
    }

    @Nullable
    @Override
    @Deprecated
    public File getFile(@Nonnull String key) {
        Path path = getPath(key);
        return path == null ? null : path.toFile();
    }

    @Nullable
    @Override
    public Path getPath(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return null;
        }

        if (!(value instanceof String)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return null;
        }

        return Paths.get(value.toString());
    }

    @Override
    public float getFloat(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return 0.f;
        }

        if (!(value instanceof Float)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0.f;
        }

        return (Float) value;
    }

    @Override
    public int getInteger(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Integer)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0;
        }

        return (Integer) value;
    }

    @Override
    public long getLong(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Long)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0;
        }

        return (Long) value;
    }

    @Nullable
    public Object getObject(String key) {
        lock.readLock().lock();
        Object value;
        try {
            value = configEntries.get(key);
        } finally {
            lock.readLock().unlock();
        }

        if (value == null) {
            LOGGER.warn("No config entry found for: {}", key);
            return null;
        }

        return value;
    }

    @Override
    public short getShort(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return 0;
        }

        if (!(value instanceof Short)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return 0;
        }

        return (Short) value;
    }

    @Nullable
    @Override
    public String getString(@Nonnull String key) {
        Object value = getObject(key);

        if (value == null) {
            return null;
        }

        if (!(value instanceof String)) {
            LOGGER.warn("Illegal config entry for: {}", key);
            return null;
        }

        return value.toString();
    }

    @Override
    public void remove(@Nonnull String key) {
        configEntries.remove(key);
    }

    private interface ConfigTypeConverter {
        String getString(@Nonnull Object object);

        Object getObject(@Nonnull String string);
    }

    private abstract static class AbstractConfigTypeConverter implements ConfigTypeConverter {
        @Override
        public final String getString(@Nonnull Object object) {
            return object.toString();
        }
    }

    private enum ConfigTypes {
        BooleanEntry("bool", Boolean.class, new AbstractConfigTypeConverter() {
            @Override
            public Boolean getObject(@Nonnull String string) {
                return Boolean.valueOf(string);
            }
        }),
        ByteEntry("byte", Byte.class, new AbstractConfigTypeConverter() {
            @Override
            public Byte getObject(@Nonnull String string) {
                return Byte.valueOf(string);
            }
        }),
        DoubleEntry("double", Double.class, new AbstractConfigTypeConverter() {
            @Override
            public Double getObject(@Nonnull String string) {
                return Double.valueOf(string);
            }
        }),
        FileEntry("file", Path.class, new ConfigTypeConverter() {
            @Nonnull
            @Override
            public String getString(@Nonnull Object object) {
                return ((Path) object).toAbsolutePath().toString();
            }

            @Nonnull
            @Override
            public Path getObject(@Nonnull String string) {
                return Paths.get(string);
            }
        }),
        FloatEntry("float", Float.class, new AbstractConfigTypeConverter() {
            @Override
            public Float getObject(@Nonnull String string) {
                return Float.valueOf(string);
            }
        }),
        IntegerEntry("int", Integer.class, new AbstractConfigTypeConverter() {
            @Override
            public Integer getObject(@Nonnull String string) {
                return Integer.valueOf(string);
            }
        }),
        LongEntry("long", Long.class, new AbstractConfigTypeConverter() {
            @Override
            public Long getObject(@Nonnull String string) {
                return Long.valueOf(string);
            }
        }),
        ShortEntry("short", Short.class, new AbstractConfigTypeConverter() {
            @Override
            public Short getObject(@Nonnull String string) {
                return Short.valueOf(string);
            }
        }),
        StringEntry("string", String.class, new AbstractConfigTypeConverter() {
            @Nonnull
            @Override
            public String getObject(@Nonnull String string) {
                return string;
            }
        });

        private String typeName;
        private Class<?> typeClass;
        private ConfigTypeConverter converter;

        ConfigTypes(
                @Nonnull String typeName, @Nonnull Class<?> typeClass, @Nonnull ConfigTypeConverter converter) {
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

    @Override
    @SuppressWarnings("nls")
    public void save() {
        if (!changed) {
            return; // no changes applied
        }

        if (Files.isDirectory(configFile)) {
            LOGGER.warn("Configuration not saved: config file set to illegal value.");
            return;
        }

        lock.writeLock().lock();
        try (OutputStream out = new GZIPOutputStream(
                Files.newOutputStream(configFile, CREATE, TRUNCATE_EXISTING, WRITE))) {
            XmlSerializer serializer = XmlPullParserFactory.newInstance().newSerializer();
            serializer.setOutput(out, ENCODING);

            serializer.startDocument(ENCODING, true);
            serializer.startTag(null, ROOT_NAME);

            for (Map.Entry<String, Object> entry : configEntries.entrySet()) {
                String key = entry.getKey();
                Class<?> valueClass = entry.getValue().getClass();
                String value = null;
                String type = null;
                for (ConfigTypes configType : ConfigTypes.values()) {
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
            out.flush();
            changed = false;
        } catch (@Nonnull IOException e) {
            LOGGER.error("Configuration not saved: error accessing config file.");
        } catch (@Nonnull XmlPullParserException e) {
            LOGGER.error("Configuration not saved: Error creating XML serializer");
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void set(@Nonnull String key, boolean value) {
        set(key, Boolean.valueOf(value));
    }

    @Override
    public void set(@Nonnull String key, byte value) {
        set(key, Byte.valueOf(value));
    }

    @Override
    public void set(@Nonnull String key, double value) {
        set(key, Double.valueOf(value));
    }

    @Deprecated
    @Override
    public void set(@Nonnull String key, @Nonnull File value) {
        set(key, value.toPath());
    }

    public void set(@Nonnull String key, @Nonnull Path value) {
        set(key, value.toAbsolutePath().toString());
    }

    @Override
    public void set(@Nonnull String key, float value) {
        set(key, Float.valueOf(value));
    }

    @Override
    public void set(@Nonnull String key, int value) {
        set(key, Integer.valueOf(value));
    }

    @Override
    public void set(@Nonnull String key, long value) {
        set(key, Long.valueOf(value));
    }

    /**
     * Set one entry of the configuration file to a new value. In this case the
     * value is a Object value.
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void set(String key, @Nonnull Object value) {
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

    @Override
    public void set(@Nonnull String key, short value) {
        set(key, Short.valueOf(value));
    }

    @Override
    public void set(@Nonnull String key, @Nonnull String value) {
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
    public void setDefault(@Nonnull String key, boolean value) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(@Nonnull String key, byte value) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(@Nonnull String key, double value) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    @Deprecated
    public void setDefault(@Nonnull String key, @Nonnull File value) {
        setDefault(key, value.toPath());
    }

    /**
     * Set the default value for a key. In this case the value is a Path value.
     * Setting default values does basically the same as setting the normal
     * values, but only in case the key has no value yet.
     * <p>
     * <b>Note:</b> This method is not exposed by the {@link Config} interface.
     * </p>
     *
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(@Nonnull String key, @Nonnull Path value) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(@Nonnull String key, float value) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(@Nonnull String key, int value) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(@Nonnull String key, long value) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(@Nonnull String key, short value) {
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
     * @param key the key the value is stored with
     * @param value the value that is stored along with the key
     */
    public void setDefault(@Nonnull String key, String value) {
        if (!(configEntries.get(key) instanceof String)) {
            set(key, value);
        }
    }

    /**
     * Load the configuration from the file system.
     */
    @SuppressWarnings("nls")
    private void loadConfig() {
        if (!Files.exists(configFile)) {
            return;
        }

        if (Files.isDirectory(configFile)) {
            LOGGER.warn("Configuration not loaded: Config file located at invalid location");
            return;
        }

        lock.writeLock().lock();
        try (InputStream in = new GZIPInputStream(Files.newInputStream(configFile, READ))) {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(in, ENCODING);

            Map<String, Object> loadedMap = new HashMap<>();

            int currentTag = parser.nextToken();
            while (currentTag != XmlPullParser.END_DOCUMENT) {
                if (currentTag == XmlPullParser.START_TAG && parser.getName().equals("entry")) {
                    String key = null;
                    String type = null;
                    String value = null;
                    int count = parser.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        String name = parser.getAttributeName(i);
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
                        for (ConfigTypes configType : ConfigTypes.values()) {
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
        } catch (@Nonnull FileNotFoundException e) {
            LOGGER.warn("Configuration not loaded: config file disappeared.");
        } catch (@Nonnull ClassCastException e) {
            LOGGER.error("Configuration not loaded: illegal config data.");
        } catch (@Nonnull IOException e) {
            LOGGER.error("Configuration not loaded: error accessing the file system.");
        } catch (@Nonnull XmlPullParserException e) {
            LOGGER.error("Error while creating XML pull parser.", e);
        } finally {
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
    private void reportChangedKey(@Nonnull String key) {
        changed = true;

        EventBus.publish(key, new ConfigChangedEvent(this, key));
    }
}
