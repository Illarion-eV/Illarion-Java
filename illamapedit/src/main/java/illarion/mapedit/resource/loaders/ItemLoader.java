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

import gnu.trove.map.hash.TIntObjectHashMap;
import illarion.common.graphics.ItemInfo;
import illarion.common.util.TableLoaderItems;
import illarion.common.util.TableLoaderSink;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.illarion.engine.assets.TextureManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;

/**
 * @author Tim
 */
public class ItemLoader implements TableLoaderSink<TableLoaderItems>, Resource {
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
    public boolean processRecord(final int line, @Nonnull final TableLoaderItems loader) {
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
        final int editorGroup = loader.getMapEditorGroup();

        final ItemInfo info = ItemInfo
                .create(face, moveable, specialFlag, obstacle, variance, opacity, surfaceLevel, itemLight);

        final ItemImg img = new ItemImg(itemID, resourceName, ItemNameLoader.getInstance().getItemName(itemID),
                editorGroup, offsetX, offsetY, frameCount, animationSpeed, mode, getTextures(resourceName, frameCount),
                info);

        items.put(img.getItemId(), img);
        return true;
    }

    @Nonnull
    private static Image[] getTextures(final String resourceName, final int frameCount) {
        final Image[] imgs = new Image[frameCount];
        TextureManager manager = TextureLoaderAwt.getInstance();
        if (frameCount == 1) {
            TextureLoaderAwt.AwtTexture texture = (TextureLoaderAwt.AwtTexture) manager
                    .getTexture(DIR_IMG_ITEMS, resourceName);
            imgs[0] = texture == null ? null : texture.getImage();
        } else {
            for (int i = 0; i < frameCount; i++) {
                TextureLoaderAwt.AwtTexture texture = (TextureLoaderAwt.AwtTexture) manager
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
    public ItemImg getTileFromId(final int id) {
        if (items.contains(id)) {
            return items.get(id);
        }
        return null;
    }

    public ItemImg[] getItems() {
        final ItemImg[] t = items.values(new ItemImg[items.size()]);
        return t;
    }
}
