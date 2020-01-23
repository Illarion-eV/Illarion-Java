/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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

import illarion.client.graphics.AvatarClothManager.AvatarClothGroup;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.client.world.characters.CharacterAttribute;
import illarion.client.world.items.Inventory;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import illarion.common.types.ItemId;
import org.illarion.engine.graphic.Color;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Character appearance
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_APPEARANCE)
public final class AppearanceMsg implements ServerReply {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(AppearanceMsg.class);

    /**
     * Conversation value for the scale value received from the server and the value the client actually uses.
     */
    private static final float SCALE_MOD = 100.f;
    /**
     * The slots of the inventory that is required to display the paperdolling of this character.
     */
    @Nonnull
    private final ItemId[] itemSlots;
    /**
     * Appearance of the character. This value contains the race and the gender
     * of the character.
     */
    private int raceId;

    private int typeId;

    /**
     * The name of the character.
     */
    @Nullable
    private String name;
    /**
     * The custom given name of the character.
     */
    @Nullable
    private String customName;
    /**
     * The ID of the beard of the character.
     */
    private short beardID;
    /**
     * ID of the character this message is about.
     */
    @Nullable
    private CharacterId charId;
    /**
     * The dead flag of the character. {@code true} is dead, {@code false} is alive.
     */
    private boolean deadFlag;
    /**
     * The color of the hair
     */
    @Nullable
    private Color hairColor;
    /**
     * The ID of the hair the character has.
     */
    private short hairID;
    /**
     * Size modifier of the character.
     */
    private short size;
    /**
     * The color of the skin
     */
    @Nullable
    private Color skinColor;
    /**
     * The hit points of the character.
     */
    private int hitPoints;

    /**
     * Default constructor for the appearance message.
     */
    public AppearanceMsg() {
        itemSlots = new ItemId[Inventory.SLOT_COUNT];
    }

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        charId = new CharacterId(reader);
        name = reader.readString();
        customName = reader.readString();

        raceId = reader.readUShort();
        typeId = reader.readUByte();
        hitPoints = reader.readUShort();
        size = reader.readUByte();
        hairID = reader.readUByte();
        beardID = reader.readUByte();
        hairColor = new Color(reader);
        skinColor = new Color(reader);

        for (int i = 0; i < itemSlots.length; i++) {
            itemSlots[i] = new ItemId(reader);
        }

        deadFlag = reader.readUByte() == 1;
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if ((skinColor == null) || (hairColor == null)) {
            throw new NotDecodedException();
        }

        @Nullable Char character = World.getPeople().getCharacter(charId);

        // Character not found.
        if (character == null) {
            log.error("Received appearance message for non-existing character: {}", charId);
            return ServerReplyResult.Failed;
        }

        log.debug("Publishing appearance to: {}", character);

        character.setScale(size / SCALE_MOD);

        character.setName(name);
        character.setCustomName(customName);

        character.setAppearance(raceId, typeId);
        character.setWearingItem(AvatarClothGroup.Hair, hairID);
        character.setWearingItem(AvatarClothGroup.Beard, beardID);

        character.resetLightValue();
        for (int i = 0; i < itemSlots.length; i++) {
            character.setInventoryItem(i, itemSlots[i]);
        }
        character.updatePaperdoll();

        if (skinColor.equals(Color.WHITE)) {
            character.setSkinColor(null);
        } else {
            character.setSkinColor(skinColor);
        }

        character.setClothColor(AvatarClothGroup.Hair, hairColor);
        character.setClothColor(AvatarClothGroup.Beard, hairColor);
        character.setAttribute(CharacterAttribute.HitPoints, hitPoints);
        character.setAlive(!deadFlag);
        character.updateLight();

        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(AppearanceMsg.class, charId);
    }
}
