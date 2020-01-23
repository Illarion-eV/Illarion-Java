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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * Client Command: Looking at a character ({@link CommandList#CMD_LOOKAT_CHAR}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LookAtCharCmd extends AbstractCommand {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(LookAtCharCmd.class);
    /**
     * FrameAnimationMode for looking in a polite way at a character. That leads to the point
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
     * @param lookAtCharId the ID of the char we want to look at
     * @param lookAtMode the mode of the look at so the method used to look at
     * the target character
     */
    public LookAtCharCmd(CharacterId lookAtCharId, int lookAtMode) {
        super(CommandList.CMD_LOOKAT_CHAR);
        charId = lookAtCharId;
        mode = (byte) lookAtMode;
    }

    /**
     * Get the data of this look at character command as string.
     *
     * @return the data of this command as string
     */
    @Nonnull
    @Override
    public String toString() {
        return toString(charId + " mode: " + mode);
    }

    /**
     * Encode the data of this look at character command and put the values into the buffer.
     *
     * @param writer the interface that allows writing data to the network communication system
     */
    @Override
    public void encode(@Nonnull NetCommWriter writer) {
        charId.encode(writer);
        writer.writeByte(mode);
        log.debug("Encoding a look at char message   for {} with the mode {}", charId, mode);
    }
}
