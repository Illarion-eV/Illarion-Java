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
import illarion.client.world.World;
import illarion.common.net.NetCommReader;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_CARRY_LOAD)
public class CarryLoadMsg extends AbstractReply {
    private int currentLoad;
    private int maximumLoad;

    @Override
    public void decode(NetCommReader reader) throws IOException {
        currentLoad = reader.readUShort();
        maximumLoad = reader.readUShort();
    }

    @Override
    public boolean executeUpdate() {
        World.getPlayer().getCarryLoad().updateLoad(currentLoad, maximumLoad);
        return true;
    }

    @Nonnull
    @Override
    public String toString() {
        return toString("Current load: " + currentLoad + " - Maximum Load: " + maximumLoad);
    }
}
