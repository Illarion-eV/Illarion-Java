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
package illarion.client.resources.loaders;

import illarion.client.graphics.Avatar;
import illarion.client.graphics.AvatarInfo;
import illarion.client.resources.ResourceFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

import org.apache.log4j.Logger;
import org.newdawn.slick.Color;

/**
 * This class is used to load the character definitions from the resource table
 * that was created using the configuration tool. The class will create the
 * required character objects and send them to the character factory that takes
 * care for distributing those objects.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class CharacterLoader extends ResourceLoader<Avatar> implements
    TableLoaderSink {
    /**
     * The table index that stores the id of the animation this avatar shows.
     */
    private static final int TB_ANIMATION = 15;

    /**
     * The table index that stores the appearance of the avatar that is send by
     * the server to request the client to show this avatar.
     */
    private static final int TB_APPEARANCE = 6;

    /**
     * The table intex that stores the blue value of the avatar.
     */
    private static final int TB_BLUE = 18;

    /**
     * The table index that stores the direction the avatar is looking at.
     */
    private static final int TB_DIRECTION = 7;

    /**
     * The table index that stores the English description of the avatar.
     */
    private static final int TB_ENGLISH = 13;

    /**
     * The table index that stores the amount of frames of this avatar.
     */
    private static final int TB_FRAME = 2;

    /**
     * The table index that stores the German description of the avatar.
     */
    private static final int TB_GERMAN = 12;

    /**
     * The table intex that stores the green value of the avatar.
     */
    private static final int TB_GREEN = 17;

    /**
     * The table index of the character ID.
     */
    private static final int TB_ID = 0;

    /**
     * The table index that stores if the graphic should be horizontal mirrored
     * or not.
     */
    private static final int TB_MIRROR = 14;

    /**
     * The table index of the name base of the files of the avatar.
     */
    private static final int TB_NAME = 1;

    /**
     * The table index that stores the x offset of the avatar graphic.
     */
    private static final int TB_OFFX = 4;

    /**
     * The table index that stores the y offset of the avatar graphic.
     */
    private static final int TB_OFFY = 5;

    /**
     * The table intex that stores the red value of the avatar.
     */
    private static final int TB_RED = 16;

    /**
     * The table index that stores the length of the shadow of this avatar
     * graphic.
     */
    private static final int TB_SHADOW = 9;

    /**
     * The table index that stores the first and the last frame of a animation.
     */
    private static final int TB_STILL = 3;

    /**
     * The table index that stores the visibility bonus of this avatar.
     */
    private static final int TB_VISIBLE = 10;

    // /**
    // * The table intex that stores the alpha value of the avatar.
    // */
    // private static final int TB_ALPHA = 19;

    /**
     * The logger that is used to report error messages.
     */
    private final Logger logger = Logger.getLogger(ItemLoader.class);

    @Override
    public void load() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<Avatar> factory = getTargetFactory();

        factory.init();
        new TableLoader("Chars", this);
        factory.loadingFinished();
        AvatarInfo.cleanup();
    }

    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final int avatarId = loader.getInt(TB_ID);
        final String filename = loader.getString(TB_NAME);
        final int frameCount = loader.getInt(TB_FRAME);
        final int stillFrame = loader.getInt(TB_STILL);
        final int offsetX = loader.getInt(TB_OFFX);
        final int offsetY = loader.getInt(TB_OFFY);
        final int shadowOffset = loader.getInt(TB_SHADOW);
        final boolean mirror = loader.getBoolean(TB_MIRROR);
        final int direction = loader.getInt(TB_DIRECTION);
        final int appearance = loader.getInt(TB_APPEARANCE);
        final int visibleMod = loader.getInt(TB_VISIBLE);
        final String german = loader.getString(TB_GERMAN);
        final String english = loader.getString(TB_ENGLISH);
        final int animationID = loader.getInt(TB_ANIMATION);
        final int skinRed = loader.getInt(TB_RED);
        final int skinGreen = loader.getInt(TB_GREEN);
        final int skinBlue = loader.getInt(TB_BLUE);
        // final int skinAlpha = loader.getInt(TB_ALPHA);

        final AvatarInfo info =
            AvatarInfo.get(appearance, visibleMod, german, english);
        info.reportAnimation(animationID);

        final Color tmp_color = new Color(skinRed, skinGreen, skinBlue, 255);

        final Avatar avatar =
            new Avatar(avatarId, filename, frameCount, stillFrame, offsetX,
                offsetY, shadowOffset, info, mirror, tmp_color, direction);

        try {
            getTargetFactory().storeResource(avatar);
            avatar.activate(avatarId);
        } catch (final IllegalStateException ex) {
            logger.error("Failed adding avatar to internal factory. ID: "
                + Integer.toString(avatarId) + " - Filename: " + filename);
        }

        return true;
    }
}
