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

/**
 * This event is triggered in case the name of a character is changed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharNameEvent
        extends AbstractCharEvent {
    public static final String EVENT = "CharNameEvent";
    private final CharSequence name;

    /**
     * Create the event and set the ID and the new name of the character that was changed.
     *
     * @param id the ID of the character
     * @param newName the new name of the character
     */
    public CharNameEvent(final long id, final CharSequence newName) {
        super(id);
        name = newName;
    }

    /**
     * Get the new name of the character.
     *
     * @return the name of the character
     */
    public CharSequence getName() {
        return name;
    }

    @Override
    public String getEvent() {
        return EVENT;
    }
}
