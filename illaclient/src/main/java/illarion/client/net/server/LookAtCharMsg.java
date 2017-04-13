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

import de.lessvoid.nifty.Nifty;
import illarion.client.gui.ChatGui;
import illarion.client.gui.controller.game.DialogHandler;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Look at description of a character
 * <p />
 * <b>This command is currently not in use.</b>
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_LOOKAT_CHAR)
public final class LookAtCharMsg implements ServerReply {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(LookAtCharMsg.class);

    /**
     * The ID of the character the look at text is related to.
     */
    private CharacterId charId;

    /**
     * The text that is the look at result.
     */
    @Nullable
    private String text;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        charId = new CharacterId(reader);
        text = reader.readString();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        log.warn("Executing a look at char message for {} with the text {}", charId, text);

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }
        if (text.isEmpty()){
            text = "No description received from the server.";
        }
        World.getUpdateTaskManager().addTaskForLater(
                (container1, delta1) -> World.getGameGui().getDialogInputGui().showCharacterDialog(charId, text));
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(LookAtCharMsg.class, charId, text);
    }
}
