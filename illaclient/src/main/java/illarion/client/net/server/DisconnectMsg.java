/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.net.server;

import illarion.client.IllaClient;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.util.Lang;
import illarion.common.net.NetCommReader;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Disconnect by server (
 * {@link illarion.client.net.CommandList#MSG_DISCONNECT}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_DISCONNECT)
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
    private static final String[] REASONS = new String[]{null, "old_client", "already_logged_in", "wrong_pw",
                                                         "server_shutdown", "kicked", null, "no_place", "not_found",
                                                         null, "unstable", "no_account", "no_skillpack",
                                                         "corruput_inventory"};

    /**
     * The ID of the logout reason.
     */
    private short reason;

    /**
     * Decode the disconnect data the receiver got and prepare it for the
     * execution.
     *
     * @param reader the receiver that got the data from the server that needs
     * to be decoded
     * @throws IOException thrown in case there was not enough data received to
     * decode the full message
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
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

        IllaClient.sendDisconnectEvent(BUILDER.toString());
        return true;
    }

    /**
     * Get the data of this disconnect message as string.
     *
     * @return the string that contains the values that were decoded for this
     * message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Reason: " + Integer.toHexString(reason));
    }
}
