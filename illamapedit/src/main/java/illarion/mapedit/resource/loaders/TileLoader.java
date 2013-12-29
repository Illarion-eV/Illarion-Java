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
import illarion.common.graphics.TileInfo;
import illarion.common.util.TableLoaderSink;
import illarion.common.util.TableLoaderTiles;
import illarion.mapedit.resource.Resource;
import illarion.mapedit.resource.TileImg;
import org.apache.log4j.Logger;
import org.illarion.engine.assets.TextureManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Tim
 */
public class TileLoader implements TableLoaderSink<TableLoaderTiles>, Resource {
    /**
     * The logger instance for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(TileLoader.class);
    private static final TileLoader INSTANCE = new TileLoader();
    private static final String DIR_IMG_TILES = "tiles/";

    private final TIntObjectHashMap<TileImg> tiles = new TIntObjectHashMap<>();

    private TileLoader() {
    }

    @Override
    public void load() throws IOException {
        new TableLoaderTiles("Tiles", this);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Tiles";
    }

    /**
     * Handle a single line of the resource table.
     */
    @Override
    public boolean processRecord(final int line, @Nonnull final TableLoaderTiles loader) {
        final int id = loader.getTileId();
        final int mode = loader.getTileMode();
        final String name = loader.getResourceName();
        final TileImg tile;
        final TileInfo info = new TileInfo(loader.getTileColor(), loader.getMovementCost(), loader.isOpaque());
        switch (mode) {
            case TableLoaderTiles.TILE_MODE_ANIMATED:

                if (isLocaleGerman()) {
                    tile = new TileImg(id, name, loader.getFrameCount(), loader.getAnimationSpeed(), info,
                                       getImages(name, loader.getFrameCount()), loader.getNameGerman());
                } else {
                    tile = new TileImg(id, name, loader.getFrameCount(), loader.getAnimationSpeed(), info,
                                       getImages(name, loader.getFrameCount()), loader.getNameEnglish());
                }
                break;

            case TableLoaderTiles.TILE_MODE_VARIANT:
                if (isLocaleGerman()) {
                    tile = new TileImg(id, name, loader.getFrameCount(), 0, info,
                                       getImages(name, loader.getFrameCount()), loader.getNameGerman());
                } else {
                    tile = new TileImg(id, name, loader.getFrameCount(), 0, info,
                                       getImages(name, loader.getFrameCount()), loader.getNameEnglish());
                }
                break;

            default:
                if (isLocaleGerman()) {
                    tile = new TileImg(id, name, 1, 0, info, getImages(name, 1), loader.getNameGerman());
                } else {
                    tile = new TileImg(id, name, 1, 0, info, getImages(name, 1), loader.getNameEnglish());
                }
                break;
        }

        tiles.put(tile.getId(), tile);

        return true;
    }

    @Nonnull
    public Image[] getImages(final String name, final int frames) {

        final Image[] imgs = new Image[frames];
        TextureManager manager = TextureLoaderAwt.getInstance();
        if (frames > 1) {
            for (int i = 0; i < frames; ++i) {
                TextureLoaderAwt.AwtTexture texture = (TextureLoaderAwt.AwtTexture) manager
                        .getTexture(DIR_IMG_TILES, name + '-' + i);
                imgs[i] = texture == null ? null : texture.getImage();
            }
        } else {
            TextureLoaderAwt.AwtTexture texture = (TextureLoaderAwt.AwtTexture) manager.getTexture(DIR_IMG_TILES, name);
            imgs[0] = texture == null ? null : texture.getImage();
        }
        return imgs;
    }

    @Nullable
    public TileImg getTileFromId(final int id) {
        if (tiles.contains(id)) {
            return tiles.get(id);
        }
        return null;
    }

    @Nonnull
    public static TileLoader getInstance() {
        return INSTANCE;
    }

    private static boolean isLocaleGerman() {
        return Locale.getDefault().equals(Locale.GERMAN);
    }

    public TileImg[] getTiles() {
        final TileImg[] t = tiles.values(new TileImg[tiles.size()]);
        return t;
    }
}
