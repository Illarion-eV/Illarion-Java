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

import illarion.client.gui.Tooltip;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Look at description of item in a showcase ({@link CommandList#MSG_LOOKAT_SHOWCASE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_LOOKAT_SHOWCASE)
public final class LookAtShowcaseMsg extends AbstractGuiMsg {
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
    @Nonnull
    private Tooltip tooltip;

    /**
     * Decode the showcase item look at text data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        containerId = reader.readUByte();
        slot = reader.readUByte();
        tooltip = new Tooltip(reader);
    }

    /**
     * Execute the showcase item look at text message and send the decoded data to the rest of the client.
     */
    @Override
    public void executeUpdate() {
        World.getGameGui().getContainerGui().showTooltip(containerId, slot, tooltip);
    }

    /**
     * Get the data of this showcase item look at text message as string.
     *
     * @return the string that contains the values that were decoded for this message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Container; " + containerId + " Slot: " + slot + ' ' + tooltip);
    }
}
