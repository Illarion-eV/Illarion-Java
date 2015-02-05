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
package illarion.client.util;

import illarion.common.config.ConfigChangedEvent;
import illarion.common.util.MessageSource;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nonnull
    public static final String LOCALE_CFG = "locale";

    /**
     * The string stores in the configuration for English language.
     */
    @Nonnull
    public static final String LOCALE_CFG_ENGLISH = "en";

    /**
     * The string stores in the configuration for German language.
     */
    @Nonnull
    public static final String LOCALE_CFG_GERMAN = "de";

    /**
     * The singleton instance of this class.
     */
    @Nonnull
    private static final Lang INSTANCE = new Lang();

    /**
     * The logger instance that handles the log output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(Lang.class);

    /**
     * The file name of the message bundles the client loads for the language.
     */
    @Nonnull
    private static final String MESSAGE_BUNDLE = "messages";

    /**
     * The current local settings.
     */
    @Nonnull
    private Locale locale;

    /**
     * The storage of the localized messages. Holds the key for the string and
     * the localized full message.
     */
    @Nonnull
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
    @Nonnull
    @Contract(pure = true)
    public static Lang getInstance() {
        return INSTANCE;
    }

    /**
     * Get a localized message from a key.
     *
     * @param key The key of the localized message
     * @return the localized message or the key with surrounding < > in case the
     * key was not found in the storage
     */
    @Nonnull
    @Contract(pure = true)
    public static String getMsg(@Nonnull String key) {
        return INSTANCE.getMessage(key);
    }

    @EventTopicSubscriber(topic = LOCALE_CFG)
    public void onConfigChanged(String topic, @Nonnull ConfigChangedEvent event) {
        recheckLocale(event.getConfig().getString(LOCALE_CFG));
    }

    /**
     * Get the current local settings.
     *
     * @return the local object of the chosen local settings
     */
    @Nonnull
    @Contract(pure = true)
    public Locale getLocale() {
        return locale;
    }

    /**
     * Get a localized message from a key.
     *
     * @param key The key of the localized message
     * @return the localized message or the key with surrounding &lt; &gt; in
     * case the key was not found in the storage
     */
    @Override
    @Nonnull
    @Contract(pure = true)
    public String getMessage(@Nonnull String key) {
        try {
            return messages.getString(key).replace("\\n", "\n");
        } catch (@Nonnull MissingResourceException e) {
            log.warn("Failed searching translated version of: {}", key);
            return '<' + key + '>';
        }
    }

    /**
     * Check if a key contains a message.
     *
     * @param key the key that shall be checked
     * @return true in case a message was found
     */
    @Contract(pure = true)
    public boolean hasMsg(@Nonnull String key) {
        try {
            messages.getString(key);
        } catch (@Nonnull MissingResourceException e) {
            return false;
        }
        return true;
    }

    /**
     * Check if the client is currently running with the English language.
     *
     * @return true if the language is set to English
     */
    @Contract(pure = true)
    public boolean isEnglish() {
        return locale.equals(Locale.ENGLISH);
    }

    /**
     * Check if the client is currently running with the German language.
     *
     * @return true if the language is set to German
     */
    @Contract(pure = true)
    public boolean isGerman() {
        return locale.equals(Locale.GERMAN);
    }

    /**
     * Check if the language settings are still correct and reload the messages if needed.
     */
    public void recheckLocale(@Nullable String key) {
        if (LOCALE_CFG_GERMAN.equals(key)) {
            if (locale.equals(Locale.GERMAN)) {
                return;
            }
            locale = Locale.GERMAN;
        } else {
            if (locale.equals(Locale.ENGLISH)) {
                return;
            }
            locale = Locale.ENGLISH;
        }

        messages = ResourceBundle.getBundle(MESSAGE_BUNDLE, locale, Lang.class.getClassLoader());
    }
}
