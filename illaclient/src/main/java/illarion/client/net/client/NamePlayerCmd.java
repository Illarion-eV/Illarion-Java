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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;
import illarion.common.types.CharacterId;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;

/**
 * Client Command: Name a player with custom name ({@link illarion.client.net.CommandList#CMD_NAME_PLAYER}).
 *
 * @author Andreas Grob &lt;vilarion@illarion.org&gt;
 */
@Immutable
public final class NamePlayerCmd extends AbstractCommand {
    @Nonnull
    private final CharacterId playerId;

    @Nonnull
    private final String customName;

    public NamePlayerCmd(@Nonnull CharacterId playerId, @Nonnull String customName) {
        super(CommandList.CMD_NAME_PLAYER);

        this.playerId = playerId;
        this.customName = customName;
    }

    /**
     * Encode the data of this command and put the values into the buffer.
     *
     * @param writer the interface that allows writing data to the network
     * communication system
     */
    @Override
    public void encode(@Nonnull NetCommWriter writer) throws IOException {
        playerId.encode(writer);
        writer.writeString(customName);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(playerId + " is named: " + customName);
    }
}
