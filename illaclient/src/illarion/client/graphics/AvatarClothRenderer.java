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

import illarion.common.util.Location;

import illarion.graphics.SpriteColor;

/**
 * This class is able to trigger the rendering of the clothes of a avatar. The
 * render action is invoked in the order that is defined for the direction the
 * parent avatar is looking at.
 * 
 * @author Martin Karing
 * @since 1.22
 */
final class AvatarClothRenderer {
    /**
     * The definition of the orders that are used to render the clothes a
     * character wears. Each direction has a separated order that is stored in
     * this list.
     */
    private static final int[][] RENDER_DIR;

    static {
        RENDER_DIR =
            new int[Location.DIR_MOVE8][AvatarClothManager.GROUP_COUNT];

        int cnt = 0;
        int group = Location.DIR_NORTH;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_TROUSERS;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_CHEST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SHOES;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_COAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAIR;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_BEARD;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SECOND_HAND;

        cnt = 0;
        group = Location.DIR_NORTHEAST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SECOND_HAND;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_TROUSERS;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SHOES;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_CHEST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_COAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAIR;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_BEARD;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAT;

        cnt = 0;
        group = Location.DIR_EAST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_TROUSERS;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_CHEST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SHOES;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_COAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAIR;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_BEARD;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SECOND_HAND;

        cnt = 0;
        group = Location.DIR_SOUTHEAST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_TROUSERS;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SHOES;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_CHEST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_COAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAIR;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_BEARD;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SECOND_HAND;

        cnt = 0;
        group = Location.DIR_SOUTH;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_COAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_TROUSERS;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SHOES;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_CHEST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAIR;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_BEARD;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SECOND_HAND;

        cnt = 0;
        group = Location.DIR_SOUTHWEST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_COAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_TROUSERS;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SHOES;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_CHEST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SECOND_HAND;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAIR;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_BEARD;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAT;

        cnt = 0;
        group = Location.DIR_WEST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_COAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_TROUSERS;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SHOES;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_CHEST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAIR;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_BEARD;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SECOND_HAND;

        cnt = 0;
        group = Location.DIR_NORTHWEST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_TROUSERS;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SHOES;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_CHEST;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_COAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAIR;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_BEARD;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_HAT;
        RENDER_DIR[group][cnt++] = AvatarClothManager.GROUP_SECOND_HAND;
    }

    /**
     * The current x coordinate of the avatar on the screen.
     */
    private int avatarPosX;

    /**
     * The current y coordinate of the avatar on the screen.
     */
    private int avatarPosY;

    /**
     * The current z coordinate of the avatar on the screen.
     */
    private int avatarPosZ;

    /**
     * The list of clothes the avatar currently wears. This clothes are rendered
     * one by one when its requested.
     */
    private final AvatarCloth[] currentClothes =
        new AvatarCloth[AvatarClothManager.GROUP_COUNT];

    /**
     * The frame that is currently rendered.
     */
    private int currentFrame;

    /**
     * The light that is currently set to the clothes.
     */
    private SpriteColor currentLight;

    /**
     * The direction if the parent that defines the order that is used to render
     * the parts of the clothes.
     */
    private final int direction;

    /**
     * The layer this clothes are assigned to.
     */
    private int layer;

    /**
     * The amount of frames the parent animation stores.
     */
    private final int parentFrames;

    /**
     * The scaling value that applies to all cloth graphics.
     */
    private float scale = 1.f;

    /**
     * The copy constructor that is used to create a dublicate of this class in
     * order to get separated instances for each avatar that is needed.
     * 
     * @param org the instance of AvatarClothRenderer that shall be copied into
     *            a new instance
     */
    protected AvatarClothRenderer(final AvatarClothRenderer org) {
        direction = org.direction;
        parentFrames = org.parentFrames;
    }

    /**
     * Create a cloth renderer for a avatar that looks into a defined direction.
     * 
     * @param dir the direction this character is looking at.
     * @param frames the amount of frames the parent avatar animation contains
     */
    protected AvatarClothRenderer(final int dir, final int frames) {
        direction = dir;
        parentFrames = frames;
    }

    /**
     * Set the alpha value of all clothes. This is used to perform a proper
     * fading out effect on all clothes.
     * 
     * @param newAlpha the new alpha value
     */
    public void setAlpha(final int newAlpha) {
        for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
            if (currentClothes[i] != null) {
                currentClothes[i].setAlpha(newAlpha);
            }
        }
    }

    /**
     * Set the frame that is currently rendered to all clothes.
     * 
     * @param frame the index of the frame that shall be rendered
     */
    public void setFrame(final int frame) {
        currentFrame = frame;
        for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
            final AvatarCloth currentCloth = currentClothes[i];
            if (currentCloth != null) {
                final int currentFrames = currentCloth.getFrames();
                if (currentFrames == parentFrames) {
                    currentCloth.setFrame(frame);
                } else if (currentFrames > 1) {
                    currentCloth
                        .setFrame((int) (((float) currentFrames * (float) frame) / parentFrames));
                }
            }
        }
    }

    /**
     * Set the light that effects the clothes. This sets the instance of the
     * light directly, so any change to the instance will be send to the clothes
     * as well. How ever in case the used instance changes, its needed to report
     * this to the clothes.
     * 
     * @param light the light object that is send to all currently set clothes
     */
    public void setLight(final SpriteColor light) {
        currentLight = light;
        for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
            if (currentClothes[i] != null) {
                currentClothes[i].setLight(light);
            }
        }
    }

    /**
     * Set the scaling value for all clothes so everything is rendered at the
     * proper size.
     * 
     * @param newScale the new scaling value to ensure that everything is
     *            rendered at the proper size
     */
    public void setScale(final float newScale) {
        scale = newScale;
        for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
            if (currentClothes[i] != null) {
                currentClothes[i].setScale(newScale);
            }
        }
    }

    /**
     * Change the base color of one cloth.
     * 
     * @param slot the slot that shall be changed
     * @param color the new color that shall be used as base color
     */
    protected void changeBaseColor(final int slot, final SpriteColor color) {
        if (currentClothes[slot] != null) {
            currentClothes[slot].changeBaseColor(color);
        }
    }

    /**
     * Clean up this cloth renderer. This will put all current clothes stored in
     * this renderer back into their factory.
     */
    protected void clear() {
        for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
            if (currentClothes[i] != null) {
                currentClothes[i].recycle();
            }
            currentClothes[i] = null;
        }
    }

    /**
     * Render all clothes in the correct order.
     */
    protected void render() {
        for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
            final int currentIndex = RENDER_DIR[direction][i];
            if (currentClothes[currentIndex] != null) {
                currentClothes[currentIndex].draw();
            }
        }
    }

    /**
     * Set on part of the clothes with a new cloth to wear. This cloth will be
     * rendered at the next run. The current cloth, if any is put back into its
     * factory.
     * 
     * @param group the group the item is a part of. So the location its shown
     *            at
     * @param item the item that shall be shown itself or <code>null</code> to
     *            remove the item
     */
    protected void setCloth(final int group, final AvatarCloth item) {
        if (currentClothes[group] != null) {
            currentClothes[group].recycle();
        }
        currentClothes[group] = item;

        if (item != null) {
            item.setLight(currentLight);
            item.setScreenPos(avatarPosX, avatarPosY, avatarPosZ, layer);
            item.setFrame(currentFrame);
            item.setScale(scale);
        }
    }

    /**
     * Set the screen position of all clothes that are currently defined in this
     * class. Its needed to call this function when ever the location changes or
     * a cloth is added.
     * 
     * @param newX the x coordinate on the screen of this object
     * @param newY the y coordinate on the screen of this object
     * @param newZ the z coordinate so the layer of this object
     * @param newLayer the global layer of this object
     */
    protected void setScreenLocation(final int newX, final int newY,
        final int newZ, final int newLayer) {
        avatarPosX = newX;
        avatarPosY = newY;
        avatarPosZ = newZ;
        layer = newLayer;
        for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
            if (currentClothes[i] != null) {
                currentClothes[i].setScreenPos(newX, newY, newZ, newLayer);
            }
        }
    }
}
