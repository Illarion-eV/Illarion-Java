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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This event is generated once a character is talking on the map. Its produces by the chat handler and contains all
 * required information in order to display the talked text properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CharTalkingEvent {
    @Nonnull
    private final ChatHandler.SpeechMode mode;
    @Nullable
    private final Char character;
    @Nonnull
    private final Location location;
    @Nonnull
    private final String text;
    @Nonnull
    private final String loggedText;

    public CharTalkingEvent(@Nonnull final ChatHandler.SpeechMode talkingMode, @Nullable final Char talkingCharacter,
                            @Nonnull final Location talkingAt, @Nonnull final String talkingText,
                            @Nonnull final String loggedTalkingText) {
        mode = talkingMode;
        character = talkingCharacter;
        location = talkingAt;
        text = talkingText;
        loggedText = loggedTalkingText;
    }

    @Nonnull
    public ChatHandler.SpeechMode getMode() {
        return mode;
    }

    @Nullable
    public Char getCharacter() {
        return character;
    }

    @Nonnull
    public Location getLocation() {
        return location;
    }

    @Nonnull
    public String getText() {
        return text;
    }

    @Nonnull
    public String getLoggedText() {
        return loggedText;
    }
}
