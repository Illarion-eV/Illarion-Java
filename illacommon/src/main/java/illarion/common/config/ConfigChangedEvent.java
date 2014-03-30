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

/**
 * This class defines the event that is send to the application using the event bus once a entry of the
 * configuration is altered.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ConfigChangedEvent {
    /**
     * The configuration that is used.
     */
    private final Config config;

    /**
     * The key inside the configuration that was altered.
     */
    private final String key;

    /**
     * Constructor that allows to set the config that triggered this event as well as the key that was changed.
     *
     * @param parentConfig the config that triggered this event
     * @param configKey the key that was changed
     */
    public ConfigChangedEvent(final Config parentConfig, final String configKey) {
        config = parentConfig;
        key = configKey;
    }

    /**
     * Get the configuration that is changed.
     *
     * @return the configuration that triggered this event
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Get the key that was changed inside the configuration.
     *
     * @return the key that was changed
     */
    public String getKey() {
        return key;
    }
}
