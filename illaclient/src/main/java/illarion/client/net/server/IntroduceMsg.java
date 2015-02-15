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
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Introduce character
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_INTRODUCE)
public final class IntroduceMsg implements ServerReply {
    /**
     * The ID of the character who is introduced.
     */
    @Nullable
    private CharacterId charId;

    /**
     * The name of the character.
     */
    @Nullable
    private String name;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        charId = new CharacterId(reader);
        name = reader.readString();
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if (name == null) {
            throw new NotDecodedException();
        }

        Char chara = World.getPeople().getCharacter(charId);
        if (chara != null) {
            chara.setName(name);
            return ServerReplyResult.Success;
        }
        return ServerReplyResult.Failed;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(IntroduceMsg.class, charId, name);
    }
}
