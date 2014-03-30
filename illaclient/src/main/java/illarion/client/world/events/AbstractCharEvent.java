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
package illarion.client.world.events;

import illarion.client.world.World;
import illarion.common.types.CharacterId;

import javax.annotation.Nonnull;

/**
 * This class is the abstract event that is extended by all events that are related to a single character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractCharEvent {
    /**
     * The ID of the character this event is related to.
     */
    private final CharacterId charId;

    /**
     * Create this event and set the character ID of the character this event refers to.
     *
     * @param id the ID of the character
     */
    protected AbstractCharEvent(final CharacterId id) {
        charId = id;
    }

    /**
     * Get the ID of the character this event refers to that is stored in this event.
     *
     * @return the ID of the character
     */
    public final CharacterId getCharId() {
        return charId;
    }

    /**
     * Get the string that describes the implemented event that actually happened.
     *
     * @return a string that describes the actual event
     */
    @Nonnull
    public abstract String getEvent();

    /**
     * Check if this event refers to the character that is played by the local player.
     *
     * @return {@code true} if the character is the one that is played by the local player
     */
    public final boolean isPlayerCharacter() {
        return World.getPlayer().isPlayer(charId);
    }
}
