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
package illarion.client.graphics;

import org.apache.log4j.Logger;

import illarion.common.util.RecycleFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;
import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * The avatar factory loads and stores all graphical representations of
 * characters.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 */
public final class AvatarFactory extends RecycleFactory<Avatar> implements
    TableLoaderSink {
    /**
     * The ID of the avatar that is loaded by default in case the requested
     * avatar was not found.
     */
    private static final int DEFAULT_ID = 10450;

    /**
     * The singleton instance of this class.
     */
    private static final AvatarFactory INSTANCE = new AvatarFactory();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(AvatarFactory.class);

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
    
    /**
     * The table intex that stores the red value of the avatar.
     */
    private static final int TB_RED = 16;
    
    /**
     * The table intex that stores the green value of the avatar.
     */
    private static final int TB_GREEN = 17;
    
    /**
     * The table intex that stores the blue value of the avatar.
     */
    private static final int TB_BLUE = 18;
    
    /**
     * The table intex that stores the alpha value of the avatar.
     */
    private static final int TB_ALPHA = 19;

    /**
     * Constructor for the avatar factory. This sets up all storage tables that
     * are needed to store the instances of the avatars created by this function
     * and it starts loading the avatar table.
     */
    private AvatarFactory() {
        super();
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of the avatar factory
     */
    public static AvatarFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The initialisation function prepares all prototyped that are needed to
     * work with this function.
     */
    @SuppressWarnings("nls")
    public void init() {
        new TableLoader("Chars", this);
        // char is invisible by default
        mapDefault(DEFAULT_ID, 1);

        AvatarInfo.cleanup();
        finish();
    }

    /**
     * Decode the entries of one line that was read and create the avatar
     * prototypes regarding this values.
     * 
     * @param line the number of the line that is currently read
     * @param loader the table loader that loads the line and triggered this
     *            function, it offers the function to read the different table
     *            columns
     * @return true in case the table loader should go in reading, false to
     *         cancel the reading operations
     */
    @Override
    @SuppressWarnings("nls")
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
        final int skinAlpha = loader.getInt(TB_ALPHA);

        final AvatarInfo info =
            AvatarInfo.get(appearance, visibleMod, german, english);
        info.reportAnimation(animationID);
        
        final SpriteColor tmp_color = Graphics.getInstance().getSpriteColor();
        
        tmp_color.set(skinRed, skinGreen, skinBlue);
        
        final Avatar avatar =
            new Avatar(avatarId, filename, frameCount, stillFrame, offsetX,
                offsetY, shadowOffset, info, mirror, tmp_color, direction);

        try {
            register(avatar);
            avatar.activate(avatarId);
        } catch (final IllegalStateException ex) {
            LOGGER.error("Failed adding avatar to internal factory. ID: "
                + Integer.toString(avatarId) + " - Filename: " + filename);
        }

        return true;
    }
}
