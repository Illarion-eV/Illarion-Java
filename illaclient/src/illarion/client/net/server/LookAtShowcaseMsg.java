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
import illarion.client.net.server.events.ContainerItemLookAtEvent;
import illarion.common.net.NetCommReader;
import illarion.common.util.Money;
import org.bushe.swing.event.EventBus;

import java.io.IOException;

/**
 * Servermessage: Look at description of item in a showcase ({@link CommandList#MSG_LOOKAT_SHOWCASE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_LOOKAT_SHOWCASE)
public final class LookAtShowcaseMsg extends AbstractItemLookAtMsg {
    /**
     * Showcase this message is related to.
     */
    private short sc;

    /**
     * The slot in the showcase that message is related to.
     */
    private short slot;

    /**
     * Decode the showcase item look at text data the receiver got and prepare
     * it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs
     *               to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *                     decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        sc = reader.readUByte();
        slot = reader.readUByte();
        decodeLookAt(reader);
    }

    /**
     * Execute the showcase item look at text message and send the decoded data
     * to the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        EventBus.publish(new ContainerItemLookAtEvent(sc, slot, name, rareness, description, craftedBy,
                new Money(worth), weight, qualityText, durabilityText, durabilityValue, amethystLevel, diamondLevel,
                emeraldLevel, rubyLevel, obsidianLevel, sapphireLevel, topazLevel, bonus));
        return true;
    }

    /**
     * Get the data of this showcase item look at text message as string.
     *
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Showcase: ");
        builder.append(sc);
        builder.append(" Slot: ");
        builder.append(slot);
        return toString(builder);
    }
}
