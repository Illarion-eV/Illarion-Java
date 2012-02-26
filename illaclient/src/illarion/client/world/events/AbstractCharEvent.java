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
package illarion.client.world.events;

import illarion.client.world.World;

/**
 * This class is the abstract event that is extended by all events that are related to a single character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractCharEvent {
    /**
     * The ID of the character this event is related to.
     */
    private final long charId;

    /**
     * Create this event and set the character ID of the character this event refers to.
     *
     * @param id the ID of the character
     */
    protected AbstractCharEvent(final long id) {
        charId = id;
    }

    /**
     * Get the ID of the character this event refers to that is stored in this event.
     *
     * @return the ID of the character
     */
    public final long getCharId() {
        return charId;
    }

    /**
     * Get the string that describes the implemented event that actually happened.
     *
     * @return a string that describes the actual event
     */
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
