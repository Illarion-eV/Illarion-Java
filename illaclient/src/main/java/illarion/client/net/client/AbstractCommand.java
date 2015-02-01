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

import illarion.common.net.NetCommWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;

/**
 * Default super class for all commands that get send to a server.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
@Immutable
public abstract class AbstractCommand {
    /**
     * The ID of the command.
     */
    private final int id;

    /**
     * The constructor of a command. This is used to set the ID of the command.
     *
     * @param commId the ID of the command
     */
    protected AbstractCommand(final int commId) {
        id = commId;
    }

    /**
     * Get the simple name of this class along with the parameters of the command.
     *
     * @param param the parameters of the command that shall be displayed along with the class name of the command,
     * in case this is {@code null} the command is assumed to be encoded without parameters
     * @return the string that contains the simple class name and the parameters
     */
    @Nonnull
    protected final String toString(@Nullable final String param) {
        return getClass().getSimpleName() + '(' + ((param == null) ? "" : param) + ')';
    }

    /**
     * Get the information about this object as a string.
     *
     * @return the data of the command as string
     */
    @Override
    @Nonnull
    public abstract String toString();

    /**
     * Encode data for transfer to server. Only for send commands.
     *
     * @param writer the byte buffer the values are added to from index 0 on
     */
    public abstract void encode(@Nonnull NetCommWriter writer) throws IOException;

    /**
     * Get the ID of this client command.
     *
     * @return the ID of the client command that is currently set.
     */
    public final int getId() {
        return id;
    }
}
