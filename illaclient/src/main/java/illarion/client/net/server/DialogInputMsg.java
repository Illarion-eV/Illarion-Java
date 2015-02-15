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
import java.io.IOException;

/**
 * Server message: Text Request
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_DIALOG_INPUT)
public final class DialogInputMsg implements ServerReply {
    /**
     * The title that is supposed to be displayed in the dialog.
     */
    private String title;

    /**
     * The description text that is displayed in this dialog.
     */
    private String description;

    /**
     * The flag if the text input is supposed to be multi-lined or not.
     */
    private boolean multiLine;

    /**
     * The maximal amount of characters that are valid to be input.
     */
    private int maxCharacters;

    /**
     * The ID of this request.
     */
    private int requestId;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        title = reader.readString();
        description = reader.readString();
        multiLine = reader.readByte() != 0;
        maxCharacters = reader.readUShort();
        requestId = reader.readInt();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((title == null) || (description == null)) {
            throw new IllegalStateException("Message is not decoded yet.");
        }
        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        World.getGameGui().getDialogInputGui().showInputDialog(requestId, title, description, maxCharacters, multiLine);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(DialogInputMsg.class, "ID: " + requestId, title, description);
    }
}
