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
package illarion.client.resources.loaders;

import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.ItemTemplate;
import illarion.common.graphics.ItemInfo;
import illarion.common.util.TableLoaderItems;
import illarion.common.util.TableLoaderSink;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Sprite;
import org.illarion.engine.graphic.Texture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This class is used to load the item definitions from the resource table that was created using the configuration
 * tool. The class will create the required item objects and send them to the item factory that takes care for
 * distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ItemLoader extends AbstractResourceLoader<ItemTemplate>
        implements TableLoaderSink<TableLoaderItems> {
    /**
     * The logger that is used to report error messages.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemLoader.class);

    /**
     * The resource path to the item graphics. All graphics need to be located at this path within the JAR-resource
     * files.
     */
    @SuppressWarnings("nls")
    private static final String ITEM_PATH = "items/";

    /**
     * The resource path to the GUI graphics. All graphics need to be located at this path within the JAR-resource
     * files.
     */
    @SuppressWarnings("nls")
    private static final String GUI_PATH = "gui/";

    /**
     * The assets of the game engine that are required to load the data needed for the items.
     */
    @Nonnull
    private final Assets assets;

    /**
     * Create a new item loader.
     *
     * @param assets the assets instance of the game engine that is used to load the data
     */
    public ItemLoader(@Nonnull Assets assets) {
        this.assets = assets;
    }

    @Nonnull
    @Override
    public ResourceFactory<ItemTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        ResourceFactory<ItemTemplate> factory = getTargetFactory();

        factory.init();
        new TableLoaderItems(this);
        factory.loadingFinished();

        loadingDone();

        return factory;
    }

    @Override
    public boolean processRecord(int line, @Nonnull TableLoaderItems loader) {
        String name = loader.getResourceName();

        int colorRed = loader.getColorModRed();
        int colorGreen = loader.getColorModGreen();
        int colorBlue = loader.getColorModBlue();
        int colorAlpha = loader.getColorModAlpha();

        Color paperdollingColor;
        if ((colorRed >= 0) && (colorGreen >= 0) && (colorBlue >= 0) && (colorAlpha >= 0)) {
            paperdollingColor = new Color(colorRed, colorGreen, colorBlue, colorAlpha);
        } else {
            paperdollingColor = null;
        }

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
        int offsetShadow = loader.getShadowOffset();

        int paperdollingRef = loader.getPaperdollingItemId();

        ItemInfo info = ItemInfo
                .create(face, moveable, specialFlag, obstacle, variance, opacity, surfaceLevel, itemLight);

        int frames;
        int speed;

        if (mode == TableLoaderItems.ITEM_MODE_ANIMATION) {
            frames = loader.getFrameCount();
            speed = loader.getAnimationSpeed();
        } else if (mode == TableLoaderItems.ITEM_MODE_VARIANCES) {
            frames = loader.getFrameCount();
            speed = 0;
        } else if (mode == TableLoaderItems.ITEM_MODE_SIMPLE) {
            frames = 1;
            speed = 0;
        } else {
            LOGGER.error("Unknown mode for item: {}", mode);
            frames = 1;
            speed = 0;
        }

        Sprite itemSprite;
        try {
            itemSprite = assets.getSpriteFactory()
                    .createSprite(getTextures(assets.getTextureManager(), ITEM_PATH, name, frames), offsetX, offsetY,
                                  SpriteFactory.CENTER, SpriteFactory.BOTTOM, false);
        } catch (@Nonnull IllegalArgumentException e) {
            LOGGER.error("Failed to fetch graphics for item {} (ID: {}) because: {}", name, itemID, e.getMessage());
            return true;
        }

        Texture guiTexture = assets.getTextureManager().getTexture(GUI_PATH, "items/" + name);
        Texture usedGuiTexture;
        if (guiTexture == null) {
            usedGuiTexture = itemSprite.getFrame(0);
        } else {
            usedGuiTexture = guiTexture;
        }

        ItemTemplate template = new ItemTemplate(itemID, itemSprite, usedGuiTexture, frames, offsetShadow, speed,
                                                       info, paperdollingRef, paperdollingColor);

        // register item with factory
        try {
            getTargetFactory().storeResource(template);
        } catch (@Nonnull IllegalStateException e) {
            LOGGER.error("Failed to register item {} in factory due a duplicated ID: {}", name, itemID);
        }

        return true;
    }
}
