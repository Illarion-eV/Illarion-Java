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
package illarion.client.world.characters;

import javax.annotation.Nonnull;

/**
 * This enumerator contains the attributes of a character the client is able to monitor.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum CharacterAttribute {
    HitPoints("hitpoints"),
    ManaPoints("mana"),
    FoodPoints("foodlevel"),
    Strength("strength"),
    Agility("agility"),
    Dexterity("dexterity"),
    Constitution("constitution"),
    Intelligence("intelligence"),
    Essence("essence"),
    Willpower("willpower"),
    Perception("perception");

    /**
     * The name for the attribute that is used by the server.
     */
    @Nonnull
    private final String serverName;

    /**
     * Default constructor.
     *
     * @param serverName the name used by the server to refer to this attribute
     */
    CharacterAttribute(@Nonnull final String serverName) {
        this.serverName = serverName;
    }

    /**
     * Get the server name that is used to refer to this attribute.
     *
     * @return the name used by the server to refer to this attribute
     */
    @Nonnull
    public String getServerName() {
        return serverName;
    }
}
