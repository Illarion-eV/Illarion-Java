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

import illarion.client.IllaClient;
import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.util.Lang;
import illarion.client.world.Game;

/**
 * Servermessage: Disconnect by server (
 * {@link illarion.client.net.CommandList#MSG_DISCONNECT}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class DisconnectMsg extends AbstractReply {
    /**
     * String Builder that supports to build the needed error string for the
     * logout.
     */
    private static final StringBuilder BUILDER = new StringBuilder();

    /**
     * The list of the reasons for the logout from the server. This are the keys
     * for the translation.
     */
    @SuppressWarnings("nls")
    private static final String[] REASONS = new String[] { null, "old_client",
        "already_logged_in", "wrong_pw", "server_shutdown", "kicked", null,
        "no_place", "not_found", null, "unstable", "no_account",
        "no_skillpack", "corruput_inventory" };

    /**
     * The ID of the logout reason.
     */
    private short reason;

    /**
     * Default constructor for the disconnect message.
     */
    public DisconnectMsg() {
        super(CommandList.MSG_DISCONNECT);
    }

    /**
     * Create a new instance of the disconnect message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public DisconnectMsg clone() {
        return new DisconnectMsg();
    }

    /**
     * Decode the disconnect data the receiver got and prepare it for the
     * execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        reason = reader.readUByte();
    }

    /**
     * Execute the disconnect message and send the decoded data to the rest of
     * the client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @SuppressWarnings("nls")
    @Override
    public boolean executeUpdate() {
        BUILDER.setLength(0);
        BUILDER.append(Lang.getMsg("logout"));
        BUILDER.append("\n");
        BUILDER.append(Lang.getMsg("logout.reason"));
        BUILDER.append(" ");

        if (reason < REASONS.length) {
            BUILDER.append(Lang.getMsg("logout." + REASONS[reason]));
        } else {
            BUILDER.append(Lang.getMsg("logout.unknown"));
            BUILDER.append(Integer.toHexString(reason));
        }
        BUILDER.append("\n");
        BUILDER.append(Lang.getMsg("logout.char"));
        BUILDER.append(" ");
        BUILDER.append(Game.getInstance().getLogin());
        IllaClient.fallbackToLogin(BUILDER.toString());
        return true;
    }

    /**
     * Get the data of this disconnect message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Reason: " + Integer.toHexString(reason));
    }
}
