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

import org.apache.log4j.Logger;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.world.CombatHandler;
import illarion.client.world.Game;

/**
 * Servermessage: Generic simple message, used for messages that do not contain
 * any data and are just send to trigger events that are all time the same and
 * require parameters.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class SimpleMsg extends AbstractReply {
    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(SimpleMsg.class);

    /**
     * Default constructor for the simple message.
     * 
     * @param id the ID of the message this simple message shall use.
     */
    public SimpleMsg(final int id) {
        super(id);
    }

    /**
     * Create a new instance of the simple message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public SimpleMsg clone() {
        return new SimpleMsg(getId());
    }

    /**
     * Decode the simple data the receiver got and prepare it for the execution.
     * Since simple messages contain no data, this function does nothing at all.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
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
        // decide between multiple simple messages
        final int id = getId();
        switch (id) {
            case CommandList.MSG_ATTACK:
                // ignore this message
                break;

            case CommandList.MSG_TARGET_LOST:
                CombatHandler.getInstance().targetLost();
                break;

            case CommandList.MSG_MAP_COMPLETE:
                Game.getDisplay().setActive(true);
                Game.getLights().refresh();
                Game.getMap().checkInside();
                Game.getMap().getMinimap().finishUpdate();
                break;

            default:
                LOGGER.warn("received unknown simple command " + id);
        }
        return true;
    }

    /**
     * Get if the command is now ready to be executed.
     * 
     * @return <code>true</code> in case the command can be executed now
     */
    @Override
    public boolean processNow() {
        if (getId() == CommandList.MSG_MAP_COMPLETE) {
            // return Gui.getInstance().getClock().isSet();
        }
        return true;
    }

    /**
     * Get the data of this simple message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     * @see illarion.client.net.server.AbstractReply#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final int id = getId();
        switch (id) {
            case CommandList.MSG_ATTACK:
                return toString("attacking");

            case CommandList.MSG_TARGET_LOST:
                return toString("target lost");

            case CommandList.MSG_MAP_COMPLETE:
                return toString("Map complete");

            default:
                return toString("unknown");
        }
    }
}
