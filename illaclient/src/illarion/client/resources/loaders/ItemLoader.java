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
package illarion.client.resources.loaders;

import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.ItemTemplate;
import illarion.common.graphics.ItemInfo;
import illarion.common.util.TableLoaderItems;
import illarion.common.util.TableLoaderSink;
import org.apache.log4j.Logger;
import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.SpriteFactory;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Sprite;
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;

/**
 * This class is used to load the item definitions from the resource table that was created using the configuration
 * tool. The class will create the required item objects and send them to the item factory that takes care for
 * distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ItemLoader extends AbstractResourceLoader<ItemTemplate> implements TableLoaderSink<TableLoaderItems> {
    /**
     * The logger that is used to report error messages.
     */
    private static final Logger LOGGER = Logger.getLogger(ItemLoader.class);

    /**
     * The resource path to the item graphics. All graphics need to be located at this path within the JAR-resource
     * files.
     */
    @SuppressWarnings("nls")
    private static final String ITEM_PATH = "data/items/";

    /**
     * The resource path to the GUI graphics. All graphics need to be located at this path within the JAR-resource
     * files.
     */
    @SuppressWarnings("nls")
    private static final String GUI_PATH = "data/gui/";

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
    public ItemLoader(@Nonnull final Assets assets) {
        this.assets = assets;
    }

    @Override
    public ResourceFactory<ItemTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<ItemTemplate> factory = getTargetFactory();

        factory.init();
        new TableLoaderItems(this);
        factory.loadingFinished();
        ItemInfo.cleanup();

        loadingDone();

        return factory;
    }

    @Override
    public boolean processRecord(final int line, @Nonnull final TableLoaderItems loader) {
        final String name = loader.getResourceName();

        final int colorRed = loader.getColorModRed();
        final int colorGreen = loader.getColorModGreen();
        final int colorBlue = loader.getColorModBlue();
        final int colorAlpha = loader.getColorModAlpha();

        final Color paperdollingColor;
        if ((colorRed >= 0) && (colorGreen >= 0) && (colorBlue >= 0) && (colorAlpha >= 0)) {
            paperdollingColor = new Color(colorRed, colorGreen, colorBlue, colorAlpha);
        } else {
            paperdollingColor = null;
        }

        final int mode = loader.getItemMode();
        final int itemID = loader.getItemId();
        final int face = loader.getFace();
        final boolean moveable = loader.isMovable();
        final int specialFlag = loader.getSpecialFlag();
        final boolean obstacle = loader.isObstacle();
        final int variance = loader.getSizeVariance();
        final int opacity = loader.getOpacity();
        final int surfaceLevel = loader.getSurfaceLevel();
        final int itemLight = loader.getItemLight();
        final int offsetX = loader.getOffsetX();
        final int offsetY = loader.getOffsetY();
        final int offsetShadow = loader.getShadowOffset();

        final int paperdollingRef = loader.getPaperdollingItemId();

        final ItemInfo info =
                ItemInfo.create(face, moveable, specialFlag, obstacle, variance,
                        opacity, surfaceLevel, itemLight);

        final int frames;
        final int speed;

        if (mode == TableLoaderItems.ITEM_MODE_ANIMATION) {
            frames = loader.getFrameCount();
            speed = loader.getAnimationSpeed();
        } else if (mode == TableLoaderItems.ITEM_MODE_VARIANCES) {
            frames = loader.getFrameCount();
            speed = 0;
        } else {
            frames = 1;
            speed = 0;
        }

        final Sprite itemSprite = assets.getSpriteFactory().createSprite(getTextures(assets.getTextureManager(),
                ITEM_PATH, name, frames), offsetX, offsetY, SpriteFactory.CENTER, SpriteFactory.BOTTOM, false);

        final Texture guiTexture = assets.getTextureManager().getTexture(GUI_PATH, "items/" + name);
        final Texture usedGuiTexture;
        if (guiTexture == null) {
            usedGuiTexture = itemSprite.getFrame(0);
        } else {
            usedGuiTexture = guiTexture;
        }

        final ItemTemplate template = new ItemTemplate(itemID, itemSprite, usedGuiTexture, frames, offsetShadow, speed,
                info, paperdollingRef, paperdollingColor);

        // register item with factory
        try {
            getTargetFactory().storeResource(template);
        } catch (@Nonnull final IllegalStateException e) {
            LOGGER.error("Failed to register item " + name + "in factory due" + " a dublicated ID: " + itemID);
        }

        return true;
    }

}
