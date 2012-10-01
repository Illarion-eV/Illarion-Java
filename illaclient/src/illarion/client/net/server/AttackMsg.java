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
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.common.net.NetCommReader;

import java.io.IOException;

/**
 * Servermessage: Attack message received in caes the player is attacking someone.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_ATTACK)
public final class AttackMsg extends AbstractReply {
    /**
     * Decode the simple data the receiver got and prepare it for the execution.
     * Since simple messages contain no data, this function does nothing at all.
     *
     * @param reader the receiver that got the data from the server that needs
     *               to be decoded
     * @throws java.io.IOException thrown in case there was not enough data received to
     *                             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        // nothing to decode
    }

    /**
     * Execute the simple message and send the decoded data to the rest of the
     * client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @SuppressWarnings("nls")
    @Override
    public boolean executeUpdate() {
        return true;
    }

    /**
     * Get the data of this simple message as string.
     *
     * @return the string that contains the values that were decoded for this
     *         message
     * @see AbstractReply#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("attacking");
    }
}
