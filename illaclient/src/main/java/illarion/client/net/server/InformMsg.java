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

import illarion.client.gui.ChatGui;
import illarion.client.gui.GameGui;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.util.Lang;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import javolution.text.TextBuilder;
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
public final class InformMsg extends AbstractGuiMsg {
    /**
     * The logger that is used for the log output of this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(InformMsg.class);

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

    @Nonnull
    @Override
    public String toString() {
        final TextBuilder builder = new TextBuilder();
        builder.append("Type: ").append(informType);
        builder.append(" Text: ").append(informText);
        return toString(builder.toString());
    }

    @Override
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
        informType = reader.readUByte();
        informText = reader.readString();
    }

    @Override
    public boolean executeUpdate() {
        if (informText == null) {
            throw new IllegalStateException("Executing a inform message while the inform text is not set.");
        }

        final GameGui gui = World.getGameGui();
        switch (informType) {
            case SERVER:
                gui.getInformGui().showServerInform(informText);
                break;
            case BROADCAST:
                gui.getInformGui().showBroadcastInform(informText);
                gui.getChatGui()
                        .addChatMessage(Lang.getMsg("chat.broadcast") + ": " + informText, ChatGui.COLOR_DEFAULT);
                break;
            case GM:
                gui.getInformGui().showTextToInform(informText);
                gui.getChatGui().addChatMessage(Lang.getMsg("chat.textto") + ": " + informText, ChatGui.COLOR_DEFAULT);
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
                TextBuilder builder = new TextBuilder();
                builder.append("Received inform with unknown type: ").append(informType);
                builder.append("(").append(informText).append(")");
                LOGGER.warn(builder.toString());
        }
        return true;
    }
}
