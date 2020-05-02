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

import illarion.client.graphics.AvatarClothManager;
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
     * Appearance of the character. This value contains the race and the gender
     * of the character.
     */
    private int appearance;

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
     * The slots of the inventory that is required to display the paperdolling of this character.
     */
    @Nonnull
    private final ItemId[] itemSlots;

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

        int race = reader.readUShort();
        boolean male = reader.readUByte() == 0;
        appearance = getAppearance(race, male);
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

        character.setAppearance(appearance);
        character.setWearingItem(AvatarClothManager.GROUP_HAIR, hairID);
        character.setWearingItem(AvatarClothManager.GROUP_BEARD, beardID);

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

        character.setClothColor(AvatarClothManager.GROUP_HAIR, hairColor);
        character.setClothColor(AvatarClothManager.GROUP_BEARD, hairColor);
        character.setAttribute(CharacterAttribute.HitPoints, hitPoints);
        character.setAlive(!deadFlag);
        character.updateLight();

        return ServerReplyResult.Success;
    }

    /**
     * Get the appearance for a race and a gender.
     * TODO: This function is plain and utter crap. It needs to go away. Far away. Soon.
     *
     * @param race the race ID
     * @param male {@code true} in case the character is male
     * @return the appearance ID
     */
    private static int getAppearance(int race, boolean male) {
        switch (race) {
            case 0: //human
                return male ? 1 : 16;
            case 1: //dwarf
                return male ? 12 : 17;
            case 2: //halfling
                return male ? 24 : 25;
            case 3: //elf
                return male ? 20 : 19;
            case 4: //orc
                return male ? 13 : 18;
            case 5: //lizardman
                return 7;
            case 7: // column of resurrection
                return 3;
            case 9: //forest troll
                return 21;
            case 10: //mummy
                return 2;
            case 11: //skeleton
                return 5;
            case 12: //floating eye
                return 6;
            case 18: //sheep
                return 9;
            case 19: //spider
                return 10;
            case 24: //pig
                return 23;
            case 27: //wasp
                return 28;
            case 30: //golem
                return 31;
            case 37: //cow
                return 40;
            case 39: //wolf
                return 42;
            case 51: //bear
                return 51;
            case 52: //raptor
                return 52;
            case 53: //zombie
                return 53;
            case 54: //hellhound
                return 54;
            case 55: //imp
                return 55;
            case 56: //iron golem
                return 56;
            case 57: //ratman
                return 57;
            case 58: //dog
                return 58;
            case 59: //beetle
                return 59;
            case 60: //fox
                return 60;
            case 61: //slime
                return 61;
            case 62: //chicken
                return 62;
            case 63: //bone dragon
                return 63;
            case 111: //rat
                return 111;
            case 112: //black dragon
                return 112;
            case 113: //rabbit
                return 113;
            case 114: //Akaltut
                return 114;
            case 115: //fairy
                return 115;
            case 116: //deer
                return 116;
            case 117: //Ettin
                return 117;
            default:
                log.warn("Unexpected race id {}. Using appearance with the same ID by chance.", race);
                return race;
        }
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(AppearanceMsg.class, charId);
    }
}
