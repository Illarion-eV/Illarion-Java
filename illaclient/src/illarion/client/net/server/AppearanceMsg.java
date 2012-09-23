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
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.CharacterId;
import org.apache.log4j.Logger;
import org.newdawn.slick.Color;

import java.io.IOException;

/**
 * Servermessage: Character appearance (@link CommandList#MSG_APPEARANCE}).
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class AppearanceMsg extends AbstractReply {
    /**
     * The instance of the logger that is used to write out the data.
     */
    private static final Logger LOGGER = Logger.getLogger(AppearanceMsg.class);

    /**
     * Conversation value for the scale value received from the server and the
     * value the client actually uses.
     */
    private static final float SCALE_MOD = 100.f;

    /**
     * Attack mode constant for character wearing a distance weapon.
     */
    private static final int STATE_DISTANCE = 2;

    /**
     * Attack mode constant for character wearing a magical wand.
     */
    private static final int STATE_MAGIC = 3;

    /**
     * Attack mode constant for character wearing a melee weapon.
     */
    private static final int STATE_MELEE = 1;

    /**
     * Attack mode constant for character wearing no weapon.
     */
    private static final int STATE_PEACEFUL = 0;

    /**
     * The sprite color instance that is used to send the color values to the
     * other parts of the client.
     */
    private static final Color TEMP_COLOR = new Color(0);

    /**
     * Appearance of the character. This value contains the race and the gender
     * of the character.
     */
    private int appearance;

    /**
     * The current attack state of the character. Possible values are
     * {@link #STATE_PEACEFUL}, {@link #STATE_MELEE}, {@link #STATE_DISTANCE},
     * and {@link #STATE_MAGIC}.
     */
    private short attackMode;

    /**
     * The ID of the item on the characters back.
     */
    private int backItemID;

    /**
     * The ID of the beard of the character.
     */
    private short beardID;

    /**
     * The ID of the item on the characters breast.
     */
    private int breastItemID;

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
     * The ID of the item on the feet of the character.
     */
    private int feetItemID;

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
     * The ID of the item on the head.
     */
    private int headItemID;

    /**
     * The ID of the item in the left hand.
     */
    private int leftItemId;

    /**
     * The ID of the item on the legs of the character.
     */
    private int legsItemID;

    /**
     * The ID of the item in the right hand.
     */
    private int rightItemId;

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
     * The visibility bonus for that character.
     */
    private byte visibilityBonus;

    /**
     * Default constructor for the appearance message.
     */
    public AppearanceMsg() {
        super(CommandList.MSG_APPEARANCE);
    }

    /**
     * Create a new instance of the appearance message as recycle object.
     *
     * @return a new instance of this message object
     */
    @Override
    public AppearanceMsg clone() {
        return new AppearanceMsg();
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
    public void decode(final NetCommReader reader) throws IOException {
        charId = new CharacterId(reader);

        final int race = reader.readUShort();
        final boolean male = (int) reader.readByte() == 0x00;
        appearance = getAppearance(race, male);
        size = reader.readUByte();
        visibilityBonus = reader.readByte();
        hairID = reader.readUByte();
        beardID = reader.readUByte();
        hairColorRed = reader.readUByte();
        hairColorGreen = reader.readUByte();
        hairColorBlue = reader.readUByte();
        skinColorRed = reader.readUByte();
        skinColorGreen = reader.readUByte();
        skinColorBlue = reader.readUByte();
        headItemID = reader.readUShort();
        breastItemID = reader.readUShort();
        backItemID = reader.readUShort();
        leftItemId = reader.readUShort();
        rightItemId = reader.readUShort();
        legsItemID = reader.readUShort();
        feetItemID = reader.readUShort();
        attackMode = reader.readUByte();
        deadFlag = (reader.readUByte() == 1);
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
        final Char ch = World.getPeople().getCharacter(charId);

        // Character not found.
        if (ch == null) {
            return true;
        }

        // set name color from attack mode
        switch (attackMode) {
            case STATE_PEACEFUL:
                ch.setNameColor(Color.yellow);
                break;
            case STATE_MELEE:
                ch.setNameColor(Color.red);
                break;
            case STATE_DISTANCE:
                ch.setNameColor(Color.green);
                break;
            case STATE_MAGIC:
                ch.setNameColor(Color.blue);
                break;
            default:
                LOGGER.warn("invalid attack mode received " + attackMode);
        }

        ch.setScale(size / SCALE_MOD);
        ch.setVisibilityBonus(visibilityBonus);
        ch.setAppearance(appearance);
        ch.resetLight();
        ch.setWearingItem(AvatarClothManager.GROUP_HAIR, hairID);
        ch.setWearingItem(AvatarClothManager.GROUP_BEARD, beardID);
        ch.setWearingItem(AvatarClothManager.GROUP_CHEST, breastItemID);
        ch.setWearingItem(AvatarClothManager.GROUP_COAT, backItemID);
        ch.setWearingItem(AvatarClothManager.GROUP_HAT, headItemID);
        ch.setWearingItem(AvatarClothManager.GROUP_TROUSERS, legsItemID);
        ch.setWearingItem(AvatarClothManager.GROUP_SHOES, feetItemID);

        ch.setWearingItem(AvatarClothManager.GROUP_FIRST_HAND, rightItemId);
        ch.setWearingItem(AvatarClothManager.GROUP_SECOND_HAND, leftItemId);

        TEMP_COLOR.r = skinColorRed / 255.f;
        TEMP_COLOR.g = skinColorGreen / 255.f;
        TEMP_COLOR.b = skinColorBlue / 255.f;
        TEMP_COLOR.a = 1.f;

        if (skinColorRed != 255 || skinColorGreen != 255 || skinColorBlue != 255) {
            ch.setSkinColor(TEMP_COLOR);
        } else {
            ch.setSkinColor(null);
        }

        TEMP_COLOR.r = hairColorRed / 255.f;
        TEMP_COLOR.g = hairColorGreen / 255.f;
        TEMP_COLOR.b = hairColorBlue / 255.f;
        ch.setClothColor(AvatarClothManager.GROUP_HAIR, TEMP_COLOR);
        ch.setClothColor(AvatarClothManager.GROUP_BEARD, TEMP_COLOR);
        ch.setAlive(!deadFlag);
        ch.updateLight();

        ch.setVisible(World.getPlayer().canSee(ch));
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
                return 3;
            case 17:
                return 4;
            case 18:
                return 9;
            case 19:
                return 10;
            case 20:
                return 11;
            case 21:
                return 14;
            case 22:
                return 15;
            case 23:
                return 22;
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
                return 46;
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
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(charId);
        builder.append(" app=");
        builder.append(appearance);
        builder.append(" size=");
        builder.append(size);
        builder.append(" weapon=");
        builder.append(attackMode);
        builder.append(" vis=");
        builder.append(visibilityBonus);
        return toString(builder.toString());
    }
}
