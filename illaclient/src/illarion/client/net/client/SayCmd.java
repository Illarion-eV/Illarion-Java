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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.client.util.ChatHandler;
import illarion.common.net.NetCommWriter;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Client Command: Send a spoken text or a emote or a text command (
 * {@link illarion.client.net.CommandList#CMD_SAY}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class SayCmd extends AbstractCommand {
    /**
     * The text that is send to the server.
     */
    @Nonnull
    private final String text;

    /**
     * Default constructor for the say text command.
     */
    public SayCmd(@Nonnull final ChatHandler.SpeechMode mode, @Nonnull final String text) {
        super(getCommandId(mode));

        this.text = text;
    }

    /**
     * Get the fitting command ID according to the speech mode.
     *
     * @param mode the speech mode
     * @return the command id
     * @throws IllegalArgumentException in case {@code mode} is not {@link ChatHandler.SpeechMode#normal} or
     *                                  {@link ChatHandler.SpeechMode#shout} or {@link ChatHandler.SpeechMode#whisper}
     */
    private static int getCommandId(@Nonnull final ChatHandler.SpeechMode mode) {
        switch (mode) {
            case normal:
                return CommandList.CMD_SAY;
            case shout:
                return CommandList.CMD_SHOUT;
            case whisper:
                return CommandList.CMD_WHISPER;
            default:
                throw new IllegalArgumentException("Illegal speech mode supplied.");
        }
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeString(text);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Text: " + text + " Mode: " + getId());
    }
}
