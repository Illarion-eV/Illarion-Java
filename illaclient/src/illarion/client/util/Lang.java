/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.util;

import illarion.common.config.ConfigChangedEvent;
import illarion.common.util.MessageSource;
import org.apache.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Localized text handler. Loads the localized messages and returns them if
 * requested, regarding the language settings of the client.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@SuppressWarnings("nls")
public final class Lang implements MessageSource {
    /**
     * The string that is the key of the language settings in the configuration
     * file.
     */
    public static final String LOCALE_CFG = "locale";

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
    private ResourceBundle messages;

    /**
     * Constructor of the game. Triggers the messages to load.
     */
    private Lang() {
        locale = Locale.ENGLISH;

        messages = ResourceBundle.getBundle(MESSAGE_BUNDLE, locale, Thread.currentThread().getContextClassLoader());
        AnnotationProcessor.process(this);
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the instance of the class
     */
    public static Lang getInstance() {
        return INSTANCE;
    }

    /**
     * Get a localized message from a key.
     *
     * @param key The key of the localized message
     * @return the localized message or the key with surrounding < > in case the
     *         key was not found in the storage
     */
    public static String getMsg(final String key) {
        return INSTANCE.getMessage(key);
    }

    @EventTopicSubscriber(topic = LOCALE_CFG)
    public void onConfigChanged(final String topic, final ConfigChangedEvent event) {
        recheckLocale(event.getConfig().getString(LOCALE_CFG));
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
            return messages.getString(key).replace("\\n", "\n");
        } catch (final MissingResourceException e) {
            LOGGER.warn("Failed searching translated version of: " + key);
            return "<" + key + ">";
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
        } catch (final MissingResourceException e) {
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

    /**
     * Check if the language settings are still correct and reload the messages if needed.
     */
    private void recheckLocale(final String key) {
        if (key.equals(LOCALE_CFG_GERMAN)) {
            if (locale == Locale.GERMAN) {
                return;
            }
            locale = Locale.GERMAN;
        } else {
            if (locale == Locale.ENGLISH) {
                return;
            }
            locale = Locale.ENGLISH;
        }

        messages = ResourceBundle.getBundle(MESSAGE_BUNDLE, locale, Lang.class.getClassLoader());
    }
}
