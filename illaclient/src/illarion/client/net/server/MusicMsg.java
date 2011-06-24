/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.server;

import java.io.IOException;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.world.Game;

/**
 * Servermessage: Play background music (
 * {@link illarion.client.net.CommandList#MSG_MUSIC}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class MusicMsg extends AbstractReply {
    /**
     * The ID of the song that shall be played.
     */
    private int song;

    /**
     * Default constructor for the play music message.
     */
    public MusicMsg() {
        super(CommandList.MSG_MUSIC);
    }

    /**
     * Create a new instance of the play music message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public MusicMsg clone() {
        return new MusicMsg();
    }

    /**
     * Decode the play music data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        song = reader.readUShort();
    }

    /**
     * Execute the play music message and send the decoded data to the rest of
     * the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        Game.getMusicBox().playMusicTrack(song);
        return true;
    }

    /**
     * Get the data of this play music message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("ID: " + song);
    }
}
