/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Server message: Disconnect by server.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_DISCONNECT)
public final class DisconnectMsg implements ServerReply {
    /**
     * The ID of the logout reason.
     */
    private short reason;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        reason = reader.readUByte();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        IllaClient.sendDisconnectEvent(Lang.getMsg("logout") + '\n' + Lang.getMsg("logout.reason") + ' ' +
                getMessageForReason(reason), true);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Contract(pure = true)
    @SuppressWarnings({"SpellCheckingInspection", "OverlyComplexMethod"})
    private static String getMessageForReason(int reason) {
        switch (reason) {
            case 1:
                return Lang.getMsg("logout.old_client");
            case 2:
                return Lang.getMsg("logout.already_logged_in");
            case 3:
                return Lang.getMsg("logout.wrong_pw");
            case 4:
                return Lang.getMsg("logout.server_shutdown");
            case 5:
                return Lang.getMsg("logout.kicked");
            //6
            case 7:
                return Lang.getMsg("logout.no_place");
            case 8:
                return Lang.getMsg("logout.not_found");
            //9
            case 10:
                return Lang.getMsg("logout.unstable");
            case 11:
                return Lang.getMsg("logout.no_account");
            case 12:
                return Lang.getMsg("logout.no_skillpack");
            case 13:
                return Lang.getMsg("logout.corruput_inventory");
            default:
                return Lang.getMsg("logout.unknown") + ' ' + Integer.toHexString(reason);
        }
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(DisconnectMsg.class, getMessageForReason(reason));
    }
}
