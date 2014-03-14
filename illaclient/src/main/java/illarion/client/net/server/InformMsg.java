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
import java.util.HashMap;
import java.util.Map;

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

    private static final Map<Integer, InformType> informTypeMap = new HashMap<>();
    static {
        for (InformType type : InformType.values()) {
            informTypeMap.put(type.value, type);
        }
    }

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

        if (informTypeMap.containsKey(informType)) {
            informTypeMap.get(informType).action(informText);
        }  else {
            TextBuilder builder = new TextBuilder();
            builder.append("Received inform with unknown type: ").append(informType);
            builder.append("(").append(informText).append(")");
            LOGGER.warn(builder.toString());
        }

        return true;
    }

    public enum InformType {
        Server(0) {
            @Override
            public void action(String informText) {
                final GameGui gui = World.getGameGui();
                gui.getInformGui().showServerInform(informText);
            }
        },
        Broadcast(1) {
            @Override
            public void action(String informText) {
                final GameGui gui = World.getGameGui();
                gui.getInformGui().showBroadcastInform(informText);
                gui.getChatGui().addChatMessage(String.format("%s: %s", Lang.getMsg("chat.broadcast"), informText),
                        ChatGui.COLOR_DEFAULT);
            }
        },
        GM(2) {
            @Override
            public void action(String informText) {
                final GameGui gui = World.getGameGui();
                gui.getInformGui().showTextToInform(informText);
                gui.getChatGui().addChatMessage(String.format("%s: %s", Lang.getMsg("chat.textto"), informText),
                        ChatGui.COLOR_DEFAULT);
            }
        },
        ScriptLowPriority(100) {
            @Override
            public void action(String informText) {
                final GameGui gui = World.getGameGui();
                gui.getInformGui().showScriptInform(0, informText);
            }
        },
        ScriptMediumPriority(101) {
            @Override
            public void action(String informText) {
                final GameGui gui = World.getGameGui();
                gui.getInformGui().showScriptInform(1, informText);
                gui.getChatGui().addChatMessage(informText, ChatGui.COLOR_INFORM);
            }
        },
        ScriptHighPriority(102) {
            @Override
            public void action(String informText) {
                final GameGui gui = World.getGameGui();
                gui.getInformGui().showScriptInform(2, informText);
                gui.getChatGui().addChatMessage(informText, ChatGui.COLOR_HIGH_INFORM);
            }
        };
        private int value;

        public abstract void action(String informText);

        private InformType(int value) {
            this.value = value;
        }
    }
}
