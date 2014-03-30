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
import illarion.client.world.CharMovementMode;
import illarion.common.net.NetCommWriter;
import illarion.common.types.CharacterId;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Client Command: Request a move or a push ({@link CommandList#CMD_MOVE}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("ClassNamingConvention")
@Immutable
public final class MoveCmd extends AbstractCommand {
    /**
     * Byte flag for a simple move.
     */
    private static final byte MODE_MOVE = 0x0B;

    /**
     * Byte flag for a pushing.
     */
    private static final byte MODE_PUSH = 0x0C;

    /**
     * Byte flag for a running move.
     */
    private static final byte MODE_RUN = 0x0D;

    /**
     * The character ID of the char that shall move.
     */
    @Nonnull
    private final CharacterId charId;

    /**
     * The direction the character moves to.
     */
    private final short direction;

    /**
     * Set the movement type. Possible values are {@link #MODE_MOVE} and
     * {@link #MODE_PUSH}.
     */
    private final byte mode;

    /**
     * Default constructor for the move command.
     */
    public MoveCmd(@Nonnull final CharacterId charId, @Nonnull final CharMovementMode mode, final int direction) {
        super(CommandList.CMD_MOVE);

        this.charId = charId;
        this.direction = (short) direction;
        switch (mode) {
            case Walk:
                this.mode = MODE_MOVE;
                break;
            case Run:
                this.mode = MODE_RUN;
                break;
            case Push:
                this.mode = MODE_PUSH;
                break;
            default:
                throw new IllegalArgumentException("Invalid move mode!");
        }
    }

    /**
     * Encode the data of this move command and put the values into the buffer.
     *
     * @param writer the interface that allows writing data to the network
     * communication system
     */
    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        charId.encode(writer);
        writer.writeUByte(direction);
        writer.writeByte(mode);
    }

    /**
     * Get the data of this move command as string.
     *
     * @return the data of this command as string
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(charId + " Direction: " + direction + " Mode: " + mode);
    }
}
