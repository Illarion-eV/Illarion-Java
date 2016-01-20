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
package illarion.client.graphics;

import illarion.client.graphics.AvatarClothManager.AvatarClothGroup;
import illarion.common.types.Direction;
import illarion.common.types.DisplayCoordinate;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is able to trigger the rendering of the clothes of a avatar. The render action is invoked in the order
 * that is defined for the direction the parent avatar is looking at.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class AvatarClothRenderer {
    /**
     * The definition of the orders that are used to render the clothes a character wears. Each direction has a
     * separated order that is stored in this list.
     */
    @Nonnull
    private static final EnumMap<Direction, List<AvatarClothGroup>> RENDER_DIR;

    static {
        RENDER_DIR = new EnumMap<>(Direction.class);

        //noinspection ConstantConditions
        int groups = AvatarClothGroup.values().length;

        {
            AvatarClothGroup[] groupArray = new AvatarClothGroup[groups];
            int cnt = 0;
            RENDER_DIR.put(Direction.North, Arrays.asList(groupArray));
            groupArray[cnt++] = AvatarClothGroup.FirstHand;
            groupArray[cnt++] = AvatarClothGroup.Trousers;
            groupArray[cnt++] = AvatarClothGroup.Shoes;
            groupArray[cnt++] = AvatarClothGroup.Chest;
            groupArray[cnt++] = AvatarClothGroup.Coat;
            groupArray[cnt++] = AvatarClothGroup.Hair;
            groupArray[cnt++] = AvatarClothGroup.Beard;
            groupArray[cnt++] = AvatarClothGroup.Hat;
            groupArray[cnt] = AvatarClothGroup.SecondHand;
        }

        {
            AvatarClothGroup[] groupArray = new AvatarClothGroup[groups];
            int cnt = 0;
            RENDER_DIR.put(Direction.NorthEast, Arrays.asList(groupArray));
            groupArray[cnt++] = AvatarClothGroup.FirstHand;
            groupArray[cnt++] = AvatarClothGroup.SecondHand;
            groupArray[cnt++] = AvatarClothGroup.Trousers;
            groupArray[cnt++] = AvatarClothGroup.Shoes;
            groupArray[cnt++] = AvatarClothGroup.Chest;
            groupArray[cnt++] = AvatarClothGroup.Coat;
            groupArray[cnt++] = AvatarClothGroup.Hair;
            groupArray[cnt++] = AvatarClothGroup.Beard;
            groupArray[cnt] = AvatarClothGroup.Hat;
        }

        {
            AvatarClothGroup[] groupArray = new AvatarClothGroup[groups];
            int cnt = 0;
            RENDER_DIR.put(Direction.East, Arrays.asList(groupArray));
            groupArray[cnt++] = AvatarClothGroup.FirstHand;
            groupArray[cnt++] = AvatarClothGroup.Trousers;
            groupArray[cnt++] = AvatarClothGroup.Shoes;
            groupArray[cnt++] = AvatarClothGroup.Chest;
            groupArray[cnt++] = AvatarClothGroup.Coat;
            groupArray[cnt++] = AvatarClothGroup.Hair;
            groupArray[cnt++] = AvatarClothGroup.Beard;
            groupArray[cnt++] = AvatarClothGroup.Hat;
            groupArray[cnt] = AvatarClothGroup.SecondHand;
        }

        {
            AvatarClothGroup[] groupArray = new AvatarClothGroup[groups];
            int cnt = 0;
            RENDER_DIR.put(Direction.SouthEast, Arrays.asList(groupArray));
            groupArray[cnt++] = AvatarClothGroup.FirstHand;
            groupArray[cnt++] = AvatarClothGroup.Trousers;
            groupArray[cnt++] = AvatarClothGroup.Shoes;
            groupArray[cnt++] = AvatarClothGroup.Chest;
            groupArray[cnt++] = AvatarClothGroup.Coat;
            groupArray[cnt++] = AvatarClothGroup.Hair;
            groupArray[cnt++] = AvatarClothGroup.Beard;
            groupArray[cnt++] = AvatarClothGroup.Hat;
            groupArray[cnt] = AvatarClothGroup.SecondHand;
        }

        {
            AvatarClothGroup[] groupArray = new AvatarClothGroup[groups];
            int cnt = 0;
            RENDER_DIR.put(Direction.South, Arrays.asList(groupArray));
            groupArray[cnt++] = AvatarClothGroup.FirstHand;
            groupArray[cnt++] = AvatarClothGroup.Coat;
            groupArray[cnt++] = AvatarClothGroup.Trousers;
            groupArray[cnt++] = AvatarClothGroup.Shoes;
            groupArray[cnt++] = AvatarClothGroup.Chest;
            groupArray[cnt++] = AvatarClothGroup.Hair;
            groupArray[cnt++] = AvatarClothGroup.Beard;
            groupArray[cnt++] = AvatarClothGroup.Hat;
            groupArray[cnt] = AvatarClothGroup.SecondHand;
        }

        {
            AvatarClothGroup[] groupArray = new AvatarClothGroup[groups];
            int cnt = 0;
            RENDER_DIR.put(Direction.SouthWest, Arrays.asList(groupArray));
            groupArray[cnt++] = AvatarClothGroup.Coat;
            groupArray[cnt++] = AvatarClothGroup.Trousers;
            groupArray[cnt++] = AvatarClothGroup.Shoes;
            groupArray[cnt++] = AvatarClothGroup.Chest;
            groupArray[cnt++] = AvatarClothGroup.FirstHand;
            groupArray[cnt++] = AvatarClothGroup.SecondHand;
            groupArray[cnt++] = AvatarClothGroup.Hair;
            groupArray[cnt++] = AvatarClothGroup.Beard;
            groupArray[cnt] = AvatarClothGroup.Hat;
        }

        {
            AvatarClothGroup[] groupArray = new AvatarClothGroup[groups];
            int cnt = 0;
            RENDER_DIR.put(Direction.West, Arrays.asList(groupArray));
            groupArray[cnt++] = AvatarClothGroup.FirstHand;
            groupArray[cnt++] = AvatarClothGroup.Coat;
            groupArray[cnt++] = AvatarClothGroup.Trousers;
            groupArray[cnt++] = AvatarClothGroup.Shoes;
            groupArray[cnt++] = AvatarClothGroup.Chest;
            groupArray[cnt++] = AvatarClothGroup.Hair;
            groupArray[cnt++] = AvatarClothGroup.Beard;
            groupArray[cnt++] = AvatarClothGroup.Hat;
            groupArray[cnt] = AvatarClothGroup.SecondHand;
        }

        AvatarClothGroup[] groupArray = new AvatarClothGroup[groups];
        int cnt = 0;
        RENDER_DIR.put(Direction.NorthWest, Arrays.asList(groupArray));
        groupArray[cnt++] = AvatarClothGroup.FirstHand;
        groupArray[cnt++] = AvatarClothGroup.Trousers;
        groupArray[cnt++] = AvatarClothGroup.Shoes;
        groupArray[cnt++] = AvatarClothGroup.Chest;
        groupArray[cnt++] = AvatarClothGroup.Coat;
        groupArray[cnt++] = AvatarClothGroup.Hair;
        groupArray[cnt++] = AvatarClothGroup.Beard;
        groupArray[cnt++] = AvatarClothGroup.Hat;
        groupArray[cnt] = AvatarClothGroup.SecondHand;
    }

    /**
     * The list of clothes the avatar currently wears. This clothes are rendered one by one when its requested.
     */
    @Nonnull
    private final Map<AvatarClothGroup, AvatarCloth> currentClothes;
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
     * This is the lock used to ensure the proper access on the cloth objects.
     */
    @Nonnull
    private final ReadWriteLock clothLock;
    /**
     * The current x coordinate of the avatar on the screen.
     */
    @Nullable
    private DisplayCoordinate avatarPos;
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
     * The scaling value that applies to all cloth graphics.
     */
    private float scale;
    /**
     * The alpha value applied to the clothes.
     */
    private int clothAlpha;

    /**
     * Create a cloth renderer for a avatar that looks into a defined direction.
     *
     * @param dir the direction this character is looking at.
     * @param frames the amount of frames the parent avatar animation contains
     */
    AvatarClothRenderer(@Nonnull Direction dir, int frames) {
        clothLock = new ReentrantReadWriteLock();
        scale = 1.f;
        currentClothes = new EnumMap<>(AvatarClothGroup.class);
        parentFrames = frames;
        direction = dir;
        clothAlpha = -1;
    }

    /**
     * Set the alpha value of all clothes. This is used to perform a proper fading out effect on all clothes.
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
            currentClothes.forEach((g, cloth) -> {
                cloth.setAlpha(newAlpha);
                cloth.setAlphaTarget(newAlpha);
            });
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
            currentClothes.forEach((g, cloth) -> {
                int currentFrames = cloth.getTemplate().getFrames();
                if (currentFrames == parentFrames) {
                    cloth.setFrame(frame);
                } else if (currentFrames > 1) {
                    cloth.setFrame((int) (((float) currentFrames * frame) / parentFrames));
                }
            });
        } finally {
            clothLock.readLock().unlock();
        }
    }

    /**
     * Set the light that effects the clothes. This sets the instance of the light directly, so any change to the
     * instance will be send to the clothes as well. How ever in case the used instance changes, its needed to report
     * this to the clothes.
     *
     * @param light the light object that is send to all currently set clothes
     */
    public void setLight(@Nonnull Color light) {
        currentLight = light;
        clothLock.readLock().lock();
        try {
            currentClothes.forEach((g, cloth) -> cloth.setLight(light));
        } finally {
            clothLock.readLock().unlock();
        }
    }

    /**
     * Set the scaling value for all clothes so everything is rendered at the proper size.
     *
     * @param newScale the new scaling value to ensure that everything is rendered at the proper size
     */
    public void setScale(float newScale) {
        scale = newScale;
        clothLock.readLock().lock();
        try {
            currentClothes.forEach((g, cloth) -> cloth.setScale(newScale));
        } finally {
            clothLock.readLock().unlock();
        }
    }

    /**
     * Change the base color of one cloth.
     *
     * @param group the group that shall be changed
     * @param color the new color that shall be used as base color, {@code null} to get the default color
     */
    void changeBaseColor(@Nonnull AvatarClothGroup group, @Nullable Color color) {
        clothLock.readLock().lock();
        try {
            AvatarCloth cloth = currentClothes.get(group);
            if (cloth != null) {
                cloth.changeBaseColor(color);
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
            List<AvatarClothGroup> renderOrder = RENDER_DIR.get(direction);
            assert renderOrder != null;

            renderOrder.stream()
                       .map(currentClothes::get)
                       .filter(Objects::nonNull)
                       .forEachOrdered(cloth -> cloth.render(g)) ;
        } finally {
            clothLock.readLock().unlock();
        }
    }

    /**
     * Update all clothes
     */
    void update(@Nonnull GameContainer container, int delta) {
        clothLock.readLock().lock();
        try {
            currentClothes.forEach((g, cloth) -> cloth.update(container, delta));
        } finally {
            clothLock.readLock().unlock();
        }
    }

    /**
     * Set on part of the clothes with a new cloth to wear. This cloth will be rendered at the next run. The current
     * cloth, if any is put back into its factory.
     *
     * @param group the group the item is a part of. So the location its shown at
     * @param item the item that shall be shown itself or {@code null} to remove the item
     */
    void setCloth(@Nonnull AvatarClothGroup group, @Nullable AvatarCloth item) {
        clothLock.writeLock().lock();
        try {
            AvatarCloth oldItem = currentClothes.get(group);
            if (oldItem != null) {
                if ((item != null) && (oldItem.getTemplate().getId() == item.getTemplate().getId())) {
                    return;
                }
            }
            if (item == null) {
                currentClothes.remove(group);
            } else {
                currentClothes.put(group, item);
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
     * Set the screen position of all clothes that are currently defined in this class. Its needed to call this function
     * when ever the location changes or a cloth is added.
     *
     * @param coordinate the display coordinate of the parent avatar
     */
    void setScreenPos(@Nonnull DisplayCoordinate coordinate) {
        avatarPos = coordinate;
        clothLock.readLock().lock();
        try {
            currentClothes.forEach((g, cloth) -> cloth.setScreenPos(coordinate));
        } finally {
            clothLock.readLock().unlock();
        }
    }
}
