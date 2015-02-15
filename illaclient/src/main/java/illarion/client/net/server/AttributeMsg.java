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
import illarion.client.world.characters.CharacterAttribute;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Server message: Character attributes
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_ATTRIBUTE)
public final class AttributeMsg implements ServerReply {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(AttributeMsg.class);

    /**
     * The ID of the character this attribute update is related to.
     */
    private CharacterId targetCharacter;

    /**
     * The name of the received attribute.
     */
    private String attribute;

    /**
     * The value of the received attribute.
     */
    private int value;

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        targetCharacter = new CharacterId(reader);
        attribute = reader.readString();
        value = reader.readUShort();
    }

    @Override
    @Nonnull
    public ServerReplyResult execute() {
        //noinspection ConstantConditions
        for (CharacterAttribute charAttribute : CharacterAttribute.values()) {
            if (charAttribute.getServerName().equals(attribute)) {
                Char character = World.getPeople().getCharacter(targetCharacter);
                if (character != null) {
                    character.setAttribute(charAttribute, value);
                } else {
                    log.error("Received a attribute update for character {}, but this character is not around.",
                            targetCharacter);
                    return ServerReplyResult.Failed;
                }
                return ServerReplyResult.Success;
            }
        }

        log.error("Failed to match {} to any existing attribute.", attribute);
        return ServerReplyResult.Failed;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(AttributeMsg.class, targetCharacter, attribute, "New value" + value);
    }
}
