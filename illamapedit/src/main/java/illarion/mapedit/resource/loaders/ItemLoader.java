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
package illarion.mapedit.resource.loaders;

import gnu.trove.map.hash.TIntObjectHashMap;
import illarion.common.graphics.ItemInfo;
import illarion.common.util.TableLoaderItems;
import illarion.common.util.TableLoaderSink;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.Resource;
import illarion.mapedit.resource.loaders.TextureLoaderAwt.AwtTexture;
import org.illarion.engine.assets.TextureManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;

/**
 * @author Tim
 */
public final class ItemLoader implements TableLoaderSink<TableLoaderItems>, Resource {
    /**
     * The logger instance for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemLoader.class);
    private static final int DB_INDEX_NAME = 2;
    private static final ItemLoader INSTANCE = new ItemLoader();
    private static final String DIR_IMG_ITEMS = "items/";
    private final TIntObjectHashMap<ItemImg> items = new TIntObjectHashMap<>();

    private ItemLoader() {

    }

    @Override
    public void load() throws IOException {
        new TableLoaderItems(this);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Items";
    }

    @Override
    public boolean processRecord(int line, @Nonnull TableLoaderItems loader) {
        int mode = loader.getItemMode();
        int itemID = loader.getItemId();
        int face = loader.getFace();
        boolean moveable = loader.isMovable();
        int specialFlag = loader.getSpecialFlag();
        boolean obstacle = loader.isObstacle();
        int variance = loader.getSizeVariance();
        int opacity = loader.getOpacity();
        int surfaceLevel = loader.getSurfaceLevel();
        int itemLight = loader.getItemLight();
        int offsetX = loader.getOffsetX();
        int offsetY = loader.getOffsetY();
        String resourceName = loader.getResourceName();
        int frameCount = loader.getFrameCount();
        int animationSpeed = loader.getAnimationSpeed();
        int editorGroup = loader.getMapEditorGroup();

        ItemInfo info = ItemInfo
                .create(face, moveable, specialFlag, obstacle, variance, opacity, surfaceLevel, itemLight);

        ItemImg img = new ItemImg(itemID, resourceName, ItemNameLoader.getInstance().getItemName(itemID),
                                        editorGroup, offsetX, offsetY, frameCount, animationSpeed, mode,
                                        getTextures(resourceName, frameCount), info);

        items.put(img.getItemId(), img);
        return true;
    }

    @Nonnull
    private static Image[] getTextures(@Nonnull String resourceName, int frameCount) {
        Image[] imgs = new Image[frameCount];
        TextureManager manager = TextureLoaderAwt.getInstance();
        if (frameCount == 1) {
            AwtTexture texture = (AwtTexture) manager
                    .getTexture(DIR_IMG_ITEMS, resourceName);
            imgs[0] = texture == null ? null : texture.getImage();
        } else {
            for (int i = 0; i < frameCount; i++) {
                AwtTexture texture = (AwtTexture) manager
                        .getTexture(DIR_IMG_ITEMS, resourceName + '-' + i);
                imgs[i] = texture == null ? null : texture.getImage();
            }
        }
        return imgs;
    }

    @Nonnull
    public static ItemLoader getInstance() {
        return INSTANCE;
    }

    @Nullable
    public ItemImg getTileFromId(int id) {
        if (items.contains(id)) {
            return items.get(id);
        }
        return null;
    }

    public ItemImg[] getItems() {
        ItemImg[] t = items.values(new ItemImg[items.size()]);
        return t;
    }
}
