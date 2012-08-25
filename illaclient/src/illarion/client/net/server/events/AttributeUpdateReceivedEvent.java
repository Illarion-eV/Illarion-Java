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
package illarion.client.net.server.events;

import illarion.client.world.characters.CharacterAttribute;

/**
 * This event is fired in in case a attribute update was received from the server.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AttributeUpdateReceivedEvent {
    /**
     * The ID of the character this update is meant for.
     */
    private final long targetChar;

    /**
     * The attribute that is altered.
     */
    private final CharacterAttribute attribute;

    /**
     * The new value of the attribute.
     */
    private final int value;

    /**
     * Constructor of the attribute event that allows to set the required values.
     *
     * @param charId the ID of the character that is effected by this event
     * @param changedAttribute the attribute that is changed
     * @param newValue the new value of the attribute
     */
    public AttributeUpdateReceivedEvent(final long charId, final CharacterAttribute changedAttribute,
                                        final int newValue) {
        targetChar = charId;
        attribute = changedAttribute;
        value = newValue;
    }

    /**
     * Get the ID of the target character.
     *
     * @return the ID of the target character
     */
    public long getTargetCharId() {
        return targetChar;
    }

    /**
     * Get the attribute that was changed by the event.
     *
     * @return the attribute
     */
    public CharacterAttribute getAttribute() {
        return attribute;
    }

    /**
     * Get the new value of the attribute.
     *
     * @return the new value of the attribute
     */
    public int getValue() {
        return value;
    }
}
