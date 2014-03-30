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

/**
 * Client Command: Request the appearance data of a unknown character ({@link CommandList#CMD_REQUEST_APPEARANCE}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class RequestAppearanceCmd extends AbstractCommand {
    /**
     * The ID of the characters who's appearance is needed.
     */
    @Nonnull
    private final CharacterId charId;

    /**
     * Default constructor for the request appearance command.
     *
     * @param characterId the ID of the character to request the appearance from
     */
    public RequestAppearanceCmd(@Nonnull final CharacterId characterId) {
        super(CommandList.CMD_REQUEST_APPEARANCE);

        charId = characterId;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        charId.encode(writer);
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString(charId.toString());
    }
}
