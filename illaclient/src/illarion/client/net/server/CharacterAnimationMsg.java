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
import illarion.client.world.Char;
import illarion.client.world.Game;

/**
 * Servermessage: Character animation (
 * {@link illarion.client.net.CommandList#MSG_CHARACTER_ANIMATION}).
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class CharacterAnimationMsg extends AbstractReply {
    /**
     * The ID of the animation that is shown.
     */
    private short animationId;

    /**
     * The ID of the character that is animated.
     */
    private long charId;

    /**
     * 
     */
    public CharacterAnimationMsg() {
        super(CommandList.MSG_CHARACTER_ANIMATION);
    }

    /**
     * Create a new instance of the character animation message as recycle
     * object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public CharacterAnimationMsg clone() {
        return new CharacterAnimationMsg();
    }

    /**
     * Decode the character animation data the receiver got and prepare it for
     * the execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        charId = reader.readUInt();
        animationId = reader.readUByte();
    }

    /**
     * Execute the message and send the decoded appearance data to the rest of
     * the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        final Char ch = Game.getPeople().getCharacter(charId);
        if (ch == null) {
            // Update for illegal character
            return true;
        }
        ch.startAnimation(animationId, Char.DEFAULT_ANIMATION_SPEED);
        return true;
    }

    /**
     * Get the data of this character animation message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Char: " + Long.toString(charId) + " - Animation ID: "
            + Integer.toString(animationId));
    }

}
