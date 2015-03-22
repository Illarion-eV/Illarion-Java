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

/**
 * Client Command: Looking at a character ({@link CommandList#CMD_LOOKAT_CHAR}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LookatCharCmd extends AbstractCommand {
    /**
     * Mode for looking in a polite way at a character. That leads to the point
     * that the character you are looking at gets no message but you get only
     * limited information.
     */
    public static final int LOOKAT_POLITE = 0;

    /**
     * Staring at a character, leads to a message for the character you are
     * staring at. But this way you get far more information then by looking in
     * a polite way.
     */
    public static final int LOOKAT_STARE = 1;

    /**
     * The ID of the character we are looking at.
     */
    private CharacterId charId;

    /**
     * The mode that is used to look at the character. So looking in a normal
     * way at the character or staring at it. Possible values are
     * {@link #LOOKAT_POLITE} and {@link #LOOKAT_STARE}.
     */
    private byte mode;

    /**
     * Default constructor for the look at character command.
     */
    public LookatCharCmd() {
        super(CommandList.CMD_LOOKAT_CHAR);
    }

    /**
     * Encode the data of this look at character command and put the values into
     * the buffer.
     *
     * @param writer the interface that allows writing data to the network
     * communication system
     */
    @Override
    public void encode(@Nonnull NetCommWriter writer) {
        charId.encode(writer);
        writer.writeByte(mode);
    }

    /**
     * Set the target of the look at and the way the look at is done.
     *
     * @param lookAtCharId the ID of the char we want to look at
     * @param lookAtMode the mode of the look at so the method used to look at
     * the target character
     */
    public void examine(CharacterId lookAtCharId, int lookAtMode) {
        charId = lookAtCharId;
        mode = (byte) lookAtMode;
    }

    /**
     * Get the data of this look at character command as string.
     *
     * @return the data of this command as string
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(charId + " mode: " + mode);
    }
}
