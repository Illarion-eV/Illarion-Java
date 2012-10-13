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

import illarion.client.util.ChatHandler;
import illarion.client.world.Char;
import illarion.common.types.Location;

/**
 * This event is generated once a character is talking on the map. Its produces by the chat handler and contains all
 * required information in order to display the talked text properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharTalkingEvent {
    private final ChatHandler.SpeechMode mode;
    private final Char character;
    private final Location location;
    private final String text;
    private final String loggedText;

    public CharTalkingEvent(final ChatHandler.SpeechMode talkingMode, final Char talkingCharacter,
                            final Location talkingAt, final String talkingText, final String loggedTalkingText) {
        mode = talkingMode;
        character = talkingCharacter;
        location = talkingAt;
        text = talkingText;
        loggedText = loggedTalkingText;
    }

    public ChatHandler.SpeechMode getMode() {
        return mode;
    }

    public Char getCharacter() {
        return character;
    }

    public Location getLocation() {
        return location;
    }

    public String getText() {
        return text;
    }

    public String getLoggedText() {
        return loggedText;
    }
}
