/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.server;

import java.io.IOException;

import org.apache.log4j.Logger;

import illarion.client.graphics.AvatarClothManager;
import illarion.client.graphics.Colors;
import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.world.Char;
import illarion.client.world.Game;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * Servermessage: Character appearance (
 * {@link illarion.client.net.CommandList#MSG_APPEARANCE}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
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
    private static final SpriteColor TEMP_COLOR = Graphics.getInstance()
        .getSpriteColor();

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
    private long charId;

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
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        charId = reader.readUInt();
        appearance = reader.readUShort();
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
        final Char ch = Game.getPeople().getCharacter(charId);

        // Character not found.
        if (ch == null) {
            return true;
        }

        // set name color from attack mode
        switch (attackMode) {
            case STATE_PEACEFUL:
                ch.setNameColor(Colors.yellow);
                break;
            case STATE_MELEE:
                ch.setNameColor(Colors.red);
                break;
            case STATE_DISTANCE:
                ch.setNameColor(Colors.green);
                break;
            case STATE_MAGIC:
                ch.setNameColor(Colors.blue);
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

        TEMP_COLOR.set(skinColorRed, skinColorGreen, skinColorBlue);
        TEMP_COLOR.setAlpha(SpriteColor.COLOR_MAX);
        if (skinColorRed != 255 || skinColorGreen != 255 || skinColorBlue != 255) {
        	ch.setSkinColor(TEMP_COLOR);
        } else {
            ch.setSkinColor(null);
        }

        TEMP_COLOR.set(hairColorRed, hairColorGreen, hairColorBlue);
        ch.setClothColor(AvatarClothManager.GROUP_HAIR, TEMP_COLOR);
        ch.setClothColor(AvatarClothManager.GROUP_BEARD, TEMP_COLOR);
        ch.setAlive(!deadFlag);
        ch.updateLight();
        return true;
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
        builder.append("ID: ");
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
