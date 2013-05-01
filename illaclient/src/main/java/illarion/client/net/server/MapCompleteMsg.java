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
import illarion.client.net.server.events.LoginFinishedEvent;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: This message is generated once the map is fully transferred.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_MAP_COMPLETE)
public final class MapCompleteMsg extends AbstractReply {
    /**
     * Decode the simple data the receiver got and prepare it for the execution.
     * Since simple messages contain no data, this function does nothing at all.
     *
     * @param reader the receiver that got the data from the server that needs
     *               to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *                     decode the full message
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
        World.getMapDisplay().setActive(true);
        World.getLights().refresh();
        World.getMap().checkInside();
        World.getMap().getMinimap().performFullUpdate();

        EventBus.publish(new LoginFinishedEvent());
        return true;
    }

    /**
     * Get the data of this simple message as string.
     *
     * @return the string that contains the values that were decoded for this
     *         message
     * @see illarion.client.net.server.AbstractReply#toString()
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Map complete");
    }
}
