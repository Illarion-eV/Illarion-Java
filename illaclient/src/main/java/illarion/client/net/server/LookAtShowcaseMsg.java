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

import illarion.client.gui.Tooltip;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Look at description of item in a container
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_LOOKAT_SHOWCASE)
public final class LookAtShowcaseMsg implements ServerReply {
    /**
     * Showcase this message is related to.
     */
    private short containerId;

    /**
     * The slot in the showcase that message is related to.
     */
    private short slot;

    /**
     * The tooltip that is supposed to be displayed.
     */
    @Nullable
    private Tooltip tooltip;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        containerId = reader.readUByte();
        slot = reader.readUByte();
        tooltip = new Tooltip(reader);
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if (tooltip == null) {
            throw new NotDecodedException();
        }

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        World.getGameGui().getContainerGui().showTooltip(containerId, slot, tooltip);
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(LookAtShowcaseMsg.class, "Container: " + containerId, "Slot: " + slot, tooltip);
    }
}
