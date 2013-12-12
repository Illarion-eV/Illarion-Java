/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.server;

import illarion.client.graphics.AvatarClothManager;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.util.Lang;
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.client.world.characters.CharacterAttribute;
import illarion.client.world.items.Inventory;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import illarion.common.types.ItemId;
import org.apache.log4j.Logger;
import org.illarion.engine.graphic.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Servermessage: Character appearance (@link CommandList#MSG_APPEARANCE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_APPEARANCE)
public final class AppearanceMsg extends AbstractGuiMsg {
    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(AppearanceMsg.class);

    /**
     * Conversation value for the scale value received from the server and the value the client actually uses.
     */
    private static final float SCALE_MOD = 100.f;

    /**
     * The sprite color instance that is used to send the color values to the
     * other parts of the client.
     */
    private static final Color TEMP_COLOR = new Color(Color.WHITE);

    /**
     * Appearance of the character. This value contains the race and the gender
     * of the character.
     */
    private int appearance;

    /**
     * The name of the character.
     */
    private String name;

    /**
     * The custom given name of the character.
     */
    private String customName;

    /**
     * The ID of the beard of the character.
     */
    private short beardID;

    /**
     * ID of the character this message is about.
     */
    private CharacterId charId;

    /**
     * The dead flag of the character. <code>true</code> is dead,
     * <code>false</code> is alive.
     */
    private boolean deadFlag;
    /**
     * The blue share of the color of the hair.
     */
    private short hairColorBlue;

    /**
     * The green share of the color of the hair.
     */
    private short hairColorGreen;

    /**
     * The red share of the color of the hair.
     */
    private short hairColorRed;

    /**
     * The ID of the hair the character has.
     */
    private short hairID;

    /**
     * Size modificator of the character.
     */
    private short size;

    /**
     * The blue share of the color of the beard.
     */
    private short skinColorBlue;

    /**
     * The green share of the color of the beard.
     */
    private short skinColorGreen;

    /**
     * The red share of the color of the beard.
     */
    private short skinColorRed;

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

    /**
     * Decode the appearance data the receiver got and prepare it for the
     * execution.
     *
     * @param reader the receiver that got the data from the server that needs
     *               to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *                     decode the full message
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader) throws IOException {
        charId = new CharacterId(reader);
        name = reader.readString();
        customName = reader.readString();

        final int race = reader.readUShort();
        final boolean male = reader.readUByte() == 0;
        appearance = getAppearance(race, male);
        hitPoints = reader.readUShort();
        size = reader.readUByte();
        hairID = reader.readUByte();
        beardID = reader.readUByte();
        hairColorRed = reader.readUByte();
        hairColorGreen = reader.readUByte();
        hairColorBlue = reader.readUByte();
        skinColorRed = reader.readUByte();
        skinColorGreen = reader.readUByte();
        skinColorBlue = reader.readUByte();

        for (int i = 0; i < itemSlots.length; i++) {
            itemSlots[i] = new ItemId(reader);
        }

        deadFlag = reader.readUByte() == 1;
    }

    /**
     * Execute the message and send the decoded appearance data to the rest of
     * the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @SuppressWarnings("nls")
    @Override
    public boolean executeUpdate() {
        @Nullable final Char character = World.getPeople().getCharacter(charId);

        // Character not found.
        if (character == null) {
            return true;
        }

        character.setScale(size / SCALE_MOD);

        if (name.isEmpty()) {
            character.setName(Lang.getMsg("chat.someone"));
        } else {
            character.setName(name);
        }

        character.setAppearance(appearance);
        character.resetLight();
        character.setWearingItem(AvatarClothManager.GROUP_HAIR, hairID);
        character.setWearingItem(AvatarClothManager.GROUP_BEARD, beardID);

        for (int i = 0; i < itemSlots.length; i++) {
            character.setInventoryItem(i, itemSlots[i]);
        }
        character.updatePaperdoll();


        if ((skinColorRed != Color.MAX_INT_VALUE) || (skinColorGreen != Color.MAX_INT_VALUE) ||
                (skinColorBlue != Color.MAX_INT_VALUE)) {
            TEMP_COLOR.setRed(skinColorRed);
            TEMP_COLOR.setGreen(skinColorGreen);
            TEMP_COLOR.setBlue(skinColorBlue);
            TEMP_COLOR.setAlpha(Color.MAX_INT_VALUE);
            character.setSkinColor(TEMP_COLOR);
        } else {
            character.setSkinColor(null);
        }

        TEMP_COLOR.setRed(hairColorRed);
        TEMP_COLOR.setGreen(hairColorGreen);
        TEMP_COLOR.setBlue(hairColorBlue);
        TEMP_COLOR.setAlpha(Color.MAX_INT_VALUE);
        character.setClothColor(AvatarClothManager.GROUP_HAIR, TEMP_COLOR);
        character.setClothColor(AvatarClothManager.GROUP_BEARD, TEMP_COLOR);
        character.setAttribute(CharacterAttribute.HitPoints, hitPoints);
        character.setAlive(!deadFlag);
        character.updateLight();

        character.setVisible(World.getPlayer().canSee(character));
        return true;
    }

    /**
     * Get the appearance for a race and a gender.
     * TODO: This function is plain and utter crap. It needs to go away. Far away. Soon.
     *
     * @param race the race ID
     * @param male {@code true} in case the character is male
     * @return the appearance ID
     */
    private static int getAppearance(final int race, final boolean male) {
        switch (race) {
            case 7:
                return 3;
            case 0:
                return male ? 1 : 16;
            case 5:
                return 7;
            case 1:
                return male ? 12 : 17;
            case 4:
                return male ? 13 : 18;
            case 3:
                return male ? 20 : 19;
            case 2:
                return male ? 24 : 25;
            case 9:
                return 21;
            case 10:
                return 2;
            case 11:
                return 5;
            case 12:
                return 6;
            case 13:
                return 8;
            case 14:
                return 14;
            case 15:
                return 73;
            case 17:
                return 4;
            case 18:
                return 9;
            case 19:
                return 10;
            case 20:
                return 11;
            case 21:
                return 110;
            case 22:
                return 110;
            case 23:
                return 110;
            case 24:
                return 23;
            case 25:
                return 47;
            case 26:
                return 27;
            case 27:
                return 28;
            case 28:
                return 29;
            case 30:
                return 31;
            case 31:
                return 32;
            case 32:
                return 33;
            case 33:
                return 34;
            case 34:
                return 35;
            case 37:
                return 40;
            case 38:
                return 36;
            case 39:
                return 42;
            case 40:
                return 37;
            case 41:
                return 38;
            case 42:
                return 39;
            case 43:
                return 43;
            case 48:
                return 48;
            case 49:
                return 44;
            case 50:
                return 45;
            case 51:
                return 51;
            case 52:
                return 52;
            case 53:
                return 53;
            case 54:
                return 54;
            case 55:
                return 55;
            case 56:
                return 56;
            case 57:
                return 57;
            case 58:
                return 58;
            case 59:
                return 59;
            case 60:
                return 60;
            case 61:
                return 61;
            case 62:
                return 62;
            case 63:
                return 63;
            case 64:
                return 64;
            case 65:
                return 65;
            case 66:
                return 66;
            case 67:
                return 67;
            case 68:
                return 68;
            case 69:
                return 69;
            case 70:
                return 70;
            case 71:
                return 71;
            case 72:
                return 72;
            case 73:
                return 73;
            case 74:
                return 74;
            case 75:
                return 75;
            case 76:
                return 76;
            case 77:
                return 91;
            case 78:
                return 77;
            case 79:
                return 78;
            case 80:
                return 79;
            case 81:
                return 80;
            case 82:
                return 81;
            case 83:
                return 82;
            case 84:
                return 83;
            case 85:
                return 84;
            case 86:
                return 85;
            case 87:
                return 86;
            case 88:
                return 87;
            case 89:
                return 88;
            case 90:
                return 89;
            case 91:
                return 90;
            case 92:
                return 92;
            case 93:
                return 93;
            case 94:
                return 94;
            case 95:
                return 95;
            case 96:
                return 96;
            case 97:
                return 97;
            case 98:
                return 98;
            case 99:
                return 99;
            case 100:
                return 100;
            case 101:
                return 101;
            case 102:
                return 102;
            case 103:
                return 103;
            case 104:
                return 104;
            case 105:
                return 105;
            case 106:
                return 106;
            case 107:
                return 107;
            case 108:
                return 108;
            default:
                return 1;
        }
    }

    /**
     * Get the data of this appearance message as string.
     *
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(charId);
        builder.append(" app=");
        builder.append(appearance);
        builder.append(" size=");
        builder.append(size);
        return toString(builder.toString());
    }
}
