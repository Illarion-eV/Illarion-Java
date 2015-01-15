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

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.MapTile;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.Location;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Graphic effect ( {@link CommandList#MSG_GRAPHIC_FX}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_GRAPHIC_FX)
public final class GraphicEffectMsg extends AbstractReply {
    /**
     * ID of the effect that shall be shown.
     */
    private int effectId;

    /**
     * The location the effect occurs on.
     */
    private transient Location loc;

    /**
     * Decode the effect data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws java.io.IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        loc = decodeLocation(reader);
        effectId = reader.readUShort();
    }

    /**
     * Execute the effect message and send the decoded data to the rest of the client.
     */
    @Override
    public void executeUpdate() {
        MapTile tile = World.getMap().getMapAt(loc);
        if (tile != null) {
            tile.showEffect(effectId);
        }
    }

    /**
     * Get the data of this effect message as string.
     *
     * @return the string that contains the values that were decoded for this message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Graphic Effect: " + effectId);
    }
}
