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

import illarion.client.gui.DialogCraftingGui;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Server message: Crafting dialog update
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_DIALOG_CRAFTING_UPDATE)
public final class DialogCraftingUpdateMsg implements ServerReply {
    /**
     * This is the value of {@link #type} in case the update means that the crafting operation was started.
     */
    private static final int START = 0;

    /**
     * This is the value of {@link #type} in case the update means that the crafting operation is completed.
     */
    private static final int COMPLETE = 1;

    /**
     * This is the value of {@link #type} in case the update means that the crafting operation was aported.
     */
    private static final int ABORTED = 2;

    /**
     * The update type.
     */
    private int type;

    /**
     * The time in 1s/10 required to complete the task.
     */
    private int requiredTime;

    /**
     * The ID of this dialog
     */
    private int requestId;

    /**
     * The amount of remaining items that still need to be produced.
     */
    private int remaining;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        type = reader.readUByte();
        if (type == START) {
            remaining = reader.readUByte();
            requiredTime = reader.readUShort();
        }
        requestId = reader.readInt();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        DialogCraftingGui gui = World.getGameGui().getDialogCraftingGui();
        switch (type) {
            case START:
                gui.startProductionIndicator(requestId, remaining, requiredTime / 10.0);
                break;
            case COMPLETE:
                gui.finishProduction(requestId);
                break;
            case ABORTED:
                gui.abortProduction(requestId);
                break;
            default:
                return ServerReplyResult.Failed;
        }

        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        switch (type) {
            case START:
                return Utilities.toString(DialogCraftingUpdateMsg.class, "ID: " + requestId, "START",
                        "required time" + (requiredTime / 10.f) + 's', "remaining: " + remaining);
            case COMPLETE:
                return Utilities.toString(DialogCraftingUpdateMsg.class, "ID: " + requestId, "COMPLETED");
            case ABORTED:
                return Utilities.toString(DialogCraftingUpdateMsg.class, "ID: " + requestId, "ABORTED");
            default:
                return Utilities.toString(DialogCraftingUpdateMsg.class, "ID: " + requestId, "UNKNOWN");
        }
    }
}
