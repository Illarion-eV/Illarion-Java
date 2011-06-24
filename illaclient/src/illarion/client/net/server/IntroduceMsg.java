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
 * Servermessage: Introduce character (
 * {@link illarion.client.net.CommandList#MSG_INTRODUCE}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class IntroduceMsg extends AbstractReply {
    /**
     * The ID of the character who is introduced.
     */
    private long charId;

    /**
     * The name of the character.
     */
    private String text;

    /**
     * Default constructor for the introduce message.
     */
    public IntroduceMsg() {
        super(CommandList.MSG_INTRODUCE);
    }

    /**
     * Create a new instance of the introduce message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public IntroduceMsg clone() {
        return new IntroduceMsg();
    }

    /**
     * Decode the introduce data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        charId = reader.readUInt();
        text = reader.readString();
    }

    /**
     * Execute the introduce message and send the decoded data to the rest of
     * the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        Game.getPeople().introduce(charId, text);
        return true;
    }

    /**
     * Clean the command up before recycling it.
     */
    @Override
    public void reset() {
        text = null;
    }

    /**
     * Get the data of this introduce message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Chat(");
        builder.append(charId);
        builder.append(") is named \"");
        builder.append(text);
        builder.append("\"");
        return toString(builder.toString());
    }
}
