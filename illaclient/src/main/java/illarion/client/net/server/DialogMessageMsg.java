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

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * This server message is used to make the client showing a message dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_DIALOG_MSG)
public final class DialogMessageMsg implements ServerReply {
    /**
     * The title of the dialog window.
     */
    @Nullable
    private String title;

    /**
     * The content of the dialog.
     */
    @Nullable
    private String content;

    /**
     * The ID of the dialog that needs to be returned in order to inform the server that the window was closed.
     */
    @Nullable
    private int dialogId;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        title = reader.readString();
        content = reader.readString();
        dialogId = reader.readInt();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((title == null) || (content == null)) {
            throw new NotDecodedException();
        }

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        World.getGameGui().getDialogMessageGui().showMessageDialog(dialogId, title, content);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(DialogMessageMsg.class, "ID: " + dialogId, title);
    }
}
