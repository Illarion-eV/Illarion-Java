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

import illarion.common.types.CharacterId;
import illarion.common.types.Location;

import javax.annotation.Nonnull;

/**
 * This is the event that is published in case a character is moved on the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharMoveEvent
        extends AbstractCharEvent {
    /**
     * The text that identifies this event.
     */
    public static final String EVENT = "CharMoveEvent";

    /**
     * The new location of the character.
     */
    private final Location loc;

    /**
     * Create a character move event that stores the ID of the moved character along with its new location.
     *
     * @param id          the ID of the character
     * @param newLocation the new location of the character
     */
    public CharMoveEvent(final CharacterId id, final Location newLocation) {
        super(id);
        loc = newLocation;
    }

    /**
     * Get the name of this event.
     *
     * @return the name of the event
     */
    @Nonnull
    @Override
    public String getEvent() {
        return EVENT;
    }

    /**
     * Get the new location of the character.
     *
     * @return the new character location
     */
    public Location getLocation() {
        return loc;
    }
}
