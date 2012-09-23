/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.resource.loaders;

import illarion.common.graphics.ItemInfo;
import illarion.common.util.TableLoaderItems;
import illarion.common.util.TableLoaderSink;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.Resource;
import javolution.util.FastList;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.IOException;

/**
 * @author Tim
 */
public class ItemLoader implements TableLoaderSink<TableLoaderItems>, Resource {
    /**
     * The logger instance for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ItemLoader.class);
    private static final int DB_INDEX_NAME = 2;
    private static final ItemLoader INSTANCE = new ItemLoader();
    private static final String DIR_IMG_ITEMS = "data/items/";
    private final FastList<ItemImg> items = new FastList<ItemImg>();

    private ItemLoader() {

    }

    @Override
    public void load() throws IOException {
        new TableLoaderItems(this);
    }

    @Override
    public String getDescription() {
        return "Items";
    }

    @Override
    public boolean processRecord(final int line, final TableLoaderItems loader) {
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
        final String resourceName = loader.getResourceName();
        final int frameCount = loader.getFrameCount();
        final int animationSpeed = loader.getAnimationSpeed();

        final ItemInfo info =
                ItemInfo.create(face, moveable, specialFlag, obstacle, variance,
                        opacity, surfaceLevel, itemLight);

        final ItemImg img = new ItemImg(itemID, resourceName, offsetX, offsetY,
                frameCount, animationSpeed, mode
                , getTextures(loader.getResourceName(), frameCount), info);

        items.add(img);
        return true;
    }

    private Image[] getTextures(final String resourceName, final int frameCount) {
        final Image[] imgs = new Image[frameCount];
        if (frameCount == 1) {
            imgs[0] = TextureLoaderAwt.getInstance().getTexture(DIR_IMG_ITEMS + resourceName);
        } else {
            for (int i = 0; i < frameCount; i++) {
                imgs[i] = TextureLoaderAwt.getInstance().getTexture(String.format("%s%s-%d", DIR_IMG_ITEMS,
                        resourceName, i));
            }
        }
        return imgs;
    }

    public static ItemLoader getInstance() {
        return INSTANCE;
    }

    public ItemImg getTileFromId(final int id) {
        for (final ItemImg t : items) {
            if (t.getItemId() == id) {
                return t;
            }
        }
        return null;
    }

    public ItemImg[] getTiles() {
        final ItemImg[] t = items.toArray(new ItemImg[items.size()]);
        return t;
    }
}
