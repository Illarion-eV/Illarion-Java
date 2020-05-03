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
package illarion.client.graphics;

import illarion.common.types.Direction;
import illarion.common.types.DisplayCoordinate;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is able to trigger the rendering of the clothes of a avatar. The
 * render action is invoked in the order that is defined for the direction the
 * parent avatar is looking at.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class AvatarClothRenderer {
    /**
     * The definition of the orders that are used to render the clothes a
     * character wears. Each direction has a separated order that is stored in
     * this list.
     */
    @Nonnull
    private static final EnumMap<Direction, int[]> RENDER_DIR;

    static {
        RENDER_DIR = new EnumMap<>(Direction.class);

        int cnt = 0;
        int[] groupArray = new int[AvatarClothManager.GROUP_COUNT];
        RENDER_DIR.put(Direction.North, groupArray);
        groupArray[cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        groupArray[cnt++] = AvatarClothManager.GROUP_TROUSERS;
        groupArray[cnt++] = AvatarClothManager.GROUP_SHOES;
        groupArray[cnt++] = AvatarClothManager.GROUP_CHEST;
        groupArray[cnt++] = AvatarClothManager.GROUP_COAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAIR;
        groupArray[cnt++] = AvatarClothManager.GROUP_BEARD;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_SECOND_HAND;

        cnt = 0;
        groupArray = new int[AvatarClothManager.GROUP_COUNT];
        RENDER_DIR.put(Direction.NorthEast, groupArray);
        groupArray[cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        groupArray[cnt++] = AvatarClothManager.GROUP_SECOND_HAND;
        groupArray[cnt++] = AvatarClothManager.GROUP_TROUSERS;
        groupArray[cnt++] = AvatarClothManager.GROUP_SHOES;
        groupArray[cnt++] = AvatarClothManager.GROUP_CHEST;
        groupArray[cnt++] = AvatarClothManager.GROUP_COAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAIR;
        groupArray[cnt++] = AvatarClothManager.GROUP_BEARD;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAT;

        cnt = 0;
        groupArray = new int[AvatarClothManager.GROUP_COUNT];
        RENDER_DIR.put(Direction.East, groupArray);
        groupArray[cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        groupArray[cnt++] = AvatarClothManager.GROUP_TROUSERS;
        groupArray[cnt++] = AvatarClothManager.GROUP_SHOES;
        groupArray[cnt++] = AvatarClothManager.GROUP_CHEST;
        groupArray[cnt++] = AvatarClothManager.GROUP_COAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAIR;
        groupArray[cnt++] = AvatarClothManager.GROUP_BEARD;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_SECOND_HAND;

        cnt = 0;
        groupArray = new int[AvatarClothManager.GROUP_COUNT];
        RENDER_DIR.put(Direction.SouthEast, groupArray);
        groupArray[cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        groupArray[cnt++] = AvatarClothManager.GROUP_TROUSERS;
        groupArray[cnt++] = AvatarClothManager.GROUP_SHOES;
        groupArray[cnt++] = AvatarClothManager.GROUP_CHEST;
        groupArray[cnt++] = AvatarClothManager.GROUP_COAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAIR;
        groupArray[cnt++] = AvatarClothManager.GROUP_BEARD;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_SECOND_HAND;

        cnt = 0;
        groupArray = new int[AvatarClothManager.GROUP_COUNT];
        RENDER_DIR.put(Direction.South, groupArray);
        groupArray[cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        groupArray[cnt++] = AvatarClothManager.GROUP_COAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_TROUSERS;
        groupArray[cnt++] = AvatarClothManager.GROUP_SHOES;
        groupArray[cnt++] = AvatarClothManager.GROUP_CHEST;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAIR;
        groupArray[cnt++] = AvatarClothManager.GROUP_BEARD;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_SECOND_HAND;

        cnt = 0;
        groupArray = new int[AvatarClothManager.GROUP_COUNT];
        RENDER_DIR.put(Direction.SouthWest, groupArray);
        groupArray[cnt++] = AvatarClothManager.GROUP_COAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_TROUSERS;
        groupArray[cnt++] = AvatarClothManager.GROUP_SHOES;
        groupArray[cnt++] = AvatarClothManager.GROUP_CHEST;
        groupArray[cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        groupArray[cnt++] = AvatarClothManager.GROUP_SECOND_HAND;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAIR;
        groupArray[cnt++] = AvatarClothManager.GROUP_BEARD;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAT;

        cnt = 0;
        groupArray = new int[AvatarClothManager.GROUP_COUNT];
        RENDER_DIR.put(Direction.West, groupArray);
        groupArray[cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        groupArray[cnt++] = AvatarClothManager.GROUP_COAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_TROUSERS;
        groupArray[cnt++] = AvatarClothManager.GROUP_SHOES;
        groupArray[cnt++] = AvatarClothManager.GROUP_CHEST;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAIR;
        groupArray[cnt++] = AvatarClothManager.GROUP_BEARD;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_SECOND_HAND;

        cnt = 0;
        groupArray = new int[AvatarClothManager.GROUP_COUNT];
        RENDER_DIR.put(Direction.NorthWest, groupArray);
        groupArray[cnt++] = AvatarClothManager.GROUP_FIRST_HAND;
        groupArray[cnt++] = AvatarClothManager.GROUP_TROUSERS;
        groupArray[cnt++] = AvatarClothManager.GROUP_SHOES;
        groupArray[cnt++] = AvatarClothManager.GROUP_CHEST;
        groupArray[cnt++] = AvatarClothManager.GROUP_COAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAIR;
        groupArray[cnt++] = AvatarClothManager.GROUP_BEARD;
        groupArray[cnt++] = AvatarClothManager.GROUP_HAT;
        groupArray[cnt++] = AvatarClothManager.GROUP_SECOND_HAND;
    }

    /**
     * The current x coordinate of the avatar on the screen.
     */
    @Nullable
    private DisplayCoordinate avatarPos;

    /**
     * The list of clothes the avatar currently wears. This clothes are rendered
     * one by one when its requested.
     */
    @Nonnull
    private final AvatarCloth[] currentClothes;

    /**
     * The frame that is currently rendered.
     */
    private int currentFrame;

    /**
     * The light that is currently set to the clothes.
     */
    @Nullable
    private Color currentLight;

    /**
     * The direction if the parent that defines the order that is used to render the parts of the clothes.
     */
    @Nonnull
    private final Direction direction;

    /**
     * The amount of frames the parent animation stores.
     */
    private final int parentFrames;

    /**
     * The scaling value that applies to all cloth graphics.
     */
    private float scale;

    /**
     * The alpha value applied to the clothes.
     */
    private int clothAlpha;

    /**
     * This is the lock used to ensure the proper access on the cloth objects.
     */
    @Nonnull
    private final ReadWriteLock clothLock;

    /**
     * The copy constructor that is used to create a duplicate of this class in
     * order to get separated instances for each avatar that is needed.
     *
     * @param org the instance of AvatarClothRenderer that shall be copied into
     * a new instance
     */
    AvatarClothRenderer(@Nonnull AvatarClothRenderer org) {
        this(org.direction, org.parentFrames);
    }

    /**
     * Create a cloth renderer for a avatar that looks into a defined direction.
     *
     * @param dir the direction this character is looking at.
     * @param frames the amount of frames the parent avatar animation contains
     */
    AvatarClothRenderer(@Nonnull Direction dir, int frames) {
        clothLock = new ReentrantReadWriteLock();
        scale = 1.f;
        currentClothes = new AvatarCloth[AvatarClothManager.GROUP_COUNT];
        parentFrames = frames;
        direction = dir;
        clothAlpha = -1;
    }

    /**
     * Set the alpha value of all clothes. This is used to perform a proper
     * fading out effect on all clothes.
     *
     * @param newAlpha the new alpha value
     */
    public void setAlpha(int newAlpha) {
        if (newAlpha == clothAlpha) {
            return;
        }

        clothAlpha = newAlpha;
        clothLock.readLock().lock();
        try {
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                if (currentClothes[i] != null) {
                    currentClothes[i].setAlpha(newAlpha);
                    currentClothes[i].setAlphaTarget(newAlpha);
                }
            }
        } finally {
            clothLock.readLock().unlock();
        }
    }

    /**
     * Set the frame that is currently rendered to all clothes.
     *
     * @param frame the index of the frame that shall be rendered
     */
    public void setFrame(int frame) {
        currentFrame = frame;
        clothLock.readLock().lock();
        try {
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                AvatarCloth currentCloth = currentClothes[i];
                if (currentCloth != null) {
                    int currentFrames = currentCloth.getTemplate().getFrames();
                    if (currentFrames == parentFrames) {
                        currentCloth.setFrame(frame);
                    } else if (currentFrames > 1) {
                        currentCloth.setFrame((int) (((float) currentFrames * frame) / parentFrames));
                    }
                }
            }
        } finally {
            clothLock.readLock().unlock();
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
    public void setLight(@Nonnull Color light) {
        currentLight = light;
        clothLock.readLock().lock();
        try {
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                if (currentClothes[i] != null) {
                    currentClothes[i].setLight(light);
                }
            }
        } finally {
            clothLock.readLock().unlock();
        }
    }

    /**
     * Set the scaling value for all clothes so everything is rendered at the
     * proper size.
     *
     * @param newScale the new scaling value to ensure that everything is
     * rendered at the proper size
     */
    public void setScale(float newScale) {
        scale = newScale;
        clothLock.readLock().lock();
        try {
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                if (currentClothes[i] != null) {
                    currentClothes[i].setScale(newScale);
                }
            }
        } finally {
            clothLock.readLock().unlock();
        }
    }

    /**
     * Change the base color of one cloth.
     *
     * @param slot the slot that shall be changed
     * @param color the new color that shall be used as base color
     */
    void changeBaseColor(int slot, Color color) {
        clothLock.readLock().lock();
        try {
            if (currentClothes[slot] != null) {
                currentClothes[slot].changeBaseColor(color);
            }
        } finally {
            clothLock.readLock().unlock();
        }
    }

    /**
     * Render all clothes in the correct order.
     */
    void render(@Nonnull Graphics g) {
        clothLock.readLock().lock();
        try {
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                int currentIndex = RENDER_DIR.get(direction)[i];
                if (currentClothes[currentIndex] != null) {
                    currentClothes[currentIndex].render(g);
                }
            }
        } finally {
            clothLock.readLock().unlock();
        }
    }

    /**
     * Update all clothes
     */
    void update(@Nonnull GameContainer c, int delta) {
        clothLock.readLock().lock();
        try {
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                if (currentClothes[i] != null) {
                    currentClothes[i].update(c, delta);
                }
            }
        } finally {
            clothLock.readLock().unlock();
        }
    }

    /**
     * Set on part of the clothes with a new cloth to wear. This cloth will be
     * rendered at the next run. The current cloth, if any is put back into its
     * factory.
     *
     * @param group the group the item is a part of. So the location its shown
     * at
     * @param item the item that shall be shown itself or {@code null} to
     * remove the item
     */
    void setCloth(int group, @Nullable AvatarCloth item) {
        clothLock.writeLock().lock();
        try {
            if (currentClothes[group] != null) {
                if ((item != null) && (currentClothes[group].getTemplate().getId() == item.getTemplate().getId())) {
                    return;
                }
            }
            currentClothes[group] = item;

            if (item != null) {
                if (currentLight != null) {
                    item.setLight(currentLight);
                }
                if (avatarPos != null) {
                    item.setScreenPos(avatarPos);
                }
                item.setFrame(currentFrame);
                item.setScale(scale);
                if (clothAlpha > -1) {
                    item.setAlpha(clothAlpha);
                    item.setAlphaTarget(clothAlpha);
                }
            }
        } finally {
            clothLock.writeLock().unlock();
        }
    }

    /**
     * Set the screen position of all clothes that are currently defined in this
     * class. Its needed to call this function when ever the location changes or
     * a cloth is added.
     *
     * @param coordinate the display coordinate of the parent avatar
     */
    void setScreenPos(@Nonnull DisplayCoordinate coordinate) {
        avatarPos = coordinate;
        clothLock.readLock().lock();
        try {
            for (int i = 0; i < AvatarClothManager.GROUP_COUNT; ++i) {
                if (currentClothes[i] != null) {
                    currentClothes[i].setScreenPos(coordinate);
                }
            }
        } finally {
            clothLock.readLock().unlock();
        }
    }
}
