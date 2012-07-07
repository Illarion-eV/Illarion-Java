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

import illarion.client.graphics.Item;
import illarion.client.resources.ResourceFactory;
import illarion.common.graphics.ItemInfo;
import illarion.common.util.TableLoaderItems;
import illarion.common.util.TableLoaderSink;
import org.apache.log4j.Logger;
import org.newdawn.slick.Color;

/**
 * This class is used to load the item definitions from the resource table that
 * was created using the configuration tool. The class will create the required
 * item objects and send them to the item factory that takes care for
 * distributing those objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ItemLoader extends AbstractResourceLoader<Item> implements TableLoaderSink<TableLoaderItems> {
    /**
     * The logger that is used to report error messages.
     */
    private final Logger logger = Logger.getLogger(ItemLoader.class);

    @Override
    public ResourceFactory<Item> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<Item> factory = getTargetFactory();

        factory.init();
        new TableLoaderItems("Items", this);
        factory.loadingFinished();
        ItemInfo.cleanup();

        return factory;
    }

    @Override
    public boolean processRecord(final int line, final TableLoaderItems loader) {
        final String name = loader.getResourceName();

        final int colorRed = loader.getColorModRed();
        final int colorGreen = loader.getColorModGreen();
        final int colorBlue = loader.getColorModBlue();
        final int colorAlpha = loader.getColorModAlpha();

        Color baseColor = null;
        if ((colorRed >= 0) && (colorGreen >= 0) && (colorBlue >= 0)
                && (colorAlpha >= 0)) {
            baseColor = new Color(colorRed, colorGreen, colorBlue, colorAlpha);
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

        int frames;
        int speed;

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

        final Item item =
                new Item(itemID, name, offsetX, offsetY, offsetShadow, frames,
                        speed, info, null, paperdollingRef);
        item.setPaperdollingColor(baseColor);

        // register item with factory
        try {
            getTargetFactory().storeResource(item);
        } catch (final IllegalStateException e) {
            logger.error("Failed to register item " + name + "in factory due"
                    + " a dublicated ID: " + Integer.toString(itemID));
        }

        item.activate(itemID);

        return true;
    }

}
