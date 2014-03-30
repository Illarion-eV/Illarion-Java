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
import illarion.common.types.CharacterId;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Client Command: Attacking a character ({@link CommandList#CMD_ATTACK}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@NotThreadSafe
public final class AttackCmd extends AbstractCommand {
    /**
     * The ID of the character that shall be attacked.
     */
    @Nonnull
    private final CharacterId charId;

    /**
     * The constructor of this command.
     *
     * @param targetCharId the ID of the character that is attacked
     */
    public AttackCmd(@Nonnull final CharacterId targetCharId) {
        super(CommandList.CMD_ATTACK);

        charId = targetCharId;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        charId.encode(writer);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString(charId.toString());
    }
}
