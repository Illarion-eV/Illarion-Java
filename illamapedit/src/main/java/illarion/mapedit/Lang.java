/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit;

import illarion.common.util.MessageSource;
import illarion.mapedit.gui.MapEditorConfig;
import javolution.text.TextBuilder;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Localized text handler. Loads the localized messages and returns them if
 * requested, regarding the language settings of the client.
 *
 * @author Martin Karing
 * @version 1.01
 * @since 1.01
 */
@SuppressWarnings("nls")
public final class Lang implements MessageSource {
    /**
     * The string stores in the configuration for English language.
     */
    public static final String LOCALE_CFG_ENGLISH = "en";

    /**
     * The string stores in the configuration for German language.
     */
    public static final String LOCALE_CFG_GERMAN = "de";

    /**
     * The singleton instance of this class.
     */
    private static final Lang INSTANCE = new Lang();

    /**
     * The logger instance that handles the log output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Lang.class);

    /**
     * The file name of the message bundles the client loads for the language.
     */
    private static final String MESSAGE_BUNDLE = "messages";

    /**
     * The current local settings.
     */
    private Locale locale;

    /**
     * The storage of the localized messages. Holds the key for the string and
     * the localized full message.
     */
    private final ResourceBundle messages;

    /**
     * Constructor of Lang. Triggers the messages to load.
     */
    private Lang() {
        locale = MapEditorConfig.getInstance().getLanguage();
        messages = ResourceBundle.getBundle(MESSAGE_BUNDLE, locale, Lang.class.getClassLoader(),new UTF8Control());
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the instance of the class
     */
    @Nonnull
    public static Lang getInstance() {
        return INSTANCE;
    }

    /**
     * Get a localized message from a key.
     *
     * @param clazz The class that is accessing this text
     * @param key   The key of the localized message
     * @return the localized message or the key with surrounding < > in case the
     *         key was not found in the storage
     */
    public static String getMsg(@Nonnull final Class<?> clazz, final String key) {
        final TextBuilder builder = new TextBuilder();
        builder.append(clazz.getName());
        builder.append('.');
        builder.append(key);
        return getMsg(builder.toString());
    }

    /**
     * Get the localized message from a key.
     *
     * @param key The key of the localized message
     * @return the localized message or the key with surrounding &lt; &gt; in
     *         case the key was not found in the storage
     */
    public static String getMsg(final String key) {
        return INSTANCE.getMessage(key);
    }

    /**
     * Get the current local settings.
     *
     * @return the local object of the chosen local settings
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Get a localized message from a key.
     *
     * @param key The key of the localized message
     * @return the localized message or the key with surrounding &lt; &gt; in
     *         case the key was not found in the storage
     */
    @Override
    public String getMessage(final String key) {
        try {
            return messages.getString(key);
        } catch (@Nonnull final MissingResourceException e) {
            LOGGER.warn("Failed searching translated version of: " + key);
            return '<' + key + '>';
        }
    }

    /**
     * Check if a key contains a message.
     *
     * @param key the key that shall be checked
     * @return true in case a message was found
     */
    public boolean hasMsg(final String key) {
        try {
            messages.getString(key);
        } catch (@Nonnull final MissingResourceException e) {
            return false;
        }
        return true;
    }

    /**
     * Check if the client is currently running with the English language.
     *
     * @return true if the language is set to English
     */
    public boolean isEnglish() {
        return locale == Locale.ENGLISH;
    }

    /**
     * Check if the client is currently running with the German language.
     *
     * @return true if the language is set to German
     */
    public boolean isGerman() {
        return locale == Locale.GERMAN;
    }

    private class UTF8Control extends ResourceBundle.Control {
        @Nullable
        @Override
        public ResourceBundle newBundle(final String baseName, final Locale locale, final String format,
                                        @Nonnull final ClassLoader loader, final boolean reload) throws IOException {
            final String bundleName = toBundleName(baseName, locale);
            final String resourceName = toResourceName(bundleName, "properties");
            InputStream stream = null;
            if (reload) {
                final URL url = loader.getResource(resourceName);
                if (url != null) {
                    final URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream == null) {
                return null;
            }

            final ResourceBundle bundle;
            try {
                bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            } finally {
                stream.close();
            }
            return bundle;
        }
    }
}
