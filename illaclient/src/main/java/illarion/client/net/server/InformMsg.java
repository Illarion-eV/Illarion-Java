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

import illarion.client.gui.ChatGui;
import illarion.client.gui.GameGui;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.util.Lang;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * The inform message that is used to receive inform messages from the server.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
@ReplyMessage(replyId = CommandList.MSG_INFORM)
public final class InformMsg implements ServerReply {
    /**
     * The logger that is used for the log output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(InformMsg.class);

    private static final int SERVER = 0;
    private static final int BROADCAST = 1;
    private static final int GM = 2;
    private static final int SCRIPT_LOW_PRIORITY = 100;
    private static final int SCRIPT_MEDIUM_PRIORITY = 101;
    private static final int SCRIPT_HIGH_PRIORITY = 102;

    /**
     * The type of the inform.
     */
    private int informType;

    /**
     * The text of the inform.
     */
    @Nullable
    private String informText;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        informType = reader.readUByte();
        informText = reader.readString();
    }

    @Nonnull
    @Override
    @SuppressWarnings("OverlyLongMethod")
    public ServerReplyResult execute() {
        if (informText == null) {
            throw new NotDecodedException();
        }

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        GameGui gui = World.getGameGui();
        switch (informType) {
            case SERVER:
                gui.getInformGui().showServerInform(informText);
                break;
            case BROADCAST:
                gui.getInformGui().showBroadcastInform(informText);
                gui.getChatGui()
                        .addChatMessage(Lang.getMsg("chat.broadcast") + ": " + informText, ChatGui.COLOR_DEFAULT);
                World.getPlayer().getChatLog().logText(Lang.getMsg("chat.broadcast") + ": " + informText);
                break;
            case GM:
                gui.getInformGui().showTextToInform(informText);
                gui.getChatGui().addChatMessage(Lang.getMsg("chat.textto") + ": " + informText, ChatGui.COLOR_DEFAULT);
                World.getPlayer().getChatLog().logText(Lang.getMsg("chat.textto") + ": " + informText);
                break;
            case SCRIPT_LOW_PRIORITY:
                gui.getInformGui().showScriptInform(0, informText);
                break;
            case SCRIPT_MEDIUM_PRIORITY:
                gui.getInformGui().showScriptInform(1, informText);
                gui.getChatGui().addChatMessage(informText, ChatGui.COLOR_INFORM);
                break;
            case SCRIPT_HIGH_PRIORITY:
                gui.getInformGui().showScriptInform(2, informText);
                gui.getChatGui().addChatMessage(informText, ChatGui.COLOR_HIGH_INFORM);
                break;
            default:
                log.warn("Received inform with unknown type: {}" + '(' + "{}" + ')', informType, informText);
        }
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(InformMsg.class, informText);
    }
}
