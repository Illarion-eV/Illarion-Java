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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.net.NetCommWriter;
import illarion.common.types.Direction;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This command is used to inform the server that the character turns towards a specified direction.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public final class TurnCmd extends AbstractCommand {
    /**
     * The direction the character is supposed to turn to.
     */
    @Nonnull
    private final Direction direction;

    /**
     * Default constructor for the turn message.
     *
     * @param direction the direction to turn to
     */
    public TurnCmd(@Nonnull Direction direction) {
        super(CommandList.CMD_TURN);

        this.direction = direction;
    }

    @Override
    public void encode(@Nonnull NetCommWriter writer) {
        direction.encode(writer);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Direction: " + direction);
    }
}
