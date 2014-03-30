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

import illarion.common.types.CharacterId;

import javax.annotation.Nonnull;

/**
 * This event is published in case a character is removed from the map.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharRemovedEvent extends AbstractCharEvent {
    /**
     * The event string that is used to identify this event type.
     */
    public static final String EVENT = "RemovedCharEvent";

    /**
     * The constructor for this event that only contains the ID of the character that is removed from the screen.
     *
     * @param id the ID of the char to remove
     */
    public CharRemovedEvent(final CharacterId id) {
        super(id);
    }

    @Nonnull
    @Override
    public String getEvent() {
        return EVENT;
    }
}
