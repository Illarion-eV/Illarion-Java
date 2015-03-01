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
import illarion.client.world.items.SelectionItem;
import illarion.common.net.NetCommReader;
import illarion.common.types.ItemId;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This server message is used to make the client show a selection dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_DIALOG_SELECTION)
public final class DialogSelectionMsg implements ServerReply {
    /**
     * The title of the dialog window.
     */
    @Nullable
    private String title;

    /**
     * The message that is displayed inside this selection dialog.
     */
    @Nullable
    private String message;

    /**
     * The ID of the dialog that needs to be returned in order to inform the server that the window was closed.
     */
    private int dialogId;

    /**
     * The items read from the dialog.
     */
    @Nullable
    private Collection<SelectionItem> items;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        title = reader.readString();
        message = reader.readString();
        int itemCount = reader.readByte();
        items = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            ItemId id = new ItemId(reader);
            String name = reader.readString();
            items.add(new SelectionItem(i, id, name));
        }
        dialogId = reader.readInt();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((title == null) || (message == null) || (items == null)) {
            throw new NotDecodedException();
        }

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        World.getGameGui().getDialogSelectionGui().showSelectionDialog(dialogId, title, message, items);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(DialogSelectionMsg.class, "ID: " + dialogId, title);
    }
}
