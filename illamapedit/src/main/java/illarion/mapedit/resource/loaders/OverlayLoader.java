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
import illarion.common.util.TableLoaderOverlay;
import illarion.common.util.TableLoaderSink;
import illarion.mapedit.resource.Overlay;
import illarion.mapedit.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;

/**
 * @author Tim
 */
public class OverlayLoader implements TableLoaderSink<TableLoaderOverlay>, Resource {

    private static final OverlayLoader INSTANCE = new OverlayLoader();
    private static final int SHAPE_COUNT = 28;
    private static final String DIR_IMG_TILES = "tiles/";

    private final TIntObjectHashMap<Overlay> overlays = new TIntObjectHashMap<>();

    @Nonnull
    public static OverlayLoader getInstance() {
        return INSTANCE;
    }

    @Override
    public void load() throws IOException {
        new TableLoaderOverlay(this);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Overlays";
    }

    @Override
    public boolean processRecord(final int line, @Nonnull final TableLoaderOverlay loader) {
        Image[] imgs = new Image[SHAPE_COUNT];
        for (int i = 0; i < SHAPE_COUNT; i++) {
            imgs[i] = TextureLoaderAwt.getInstance().
                    getTexture(String.format("%s%s-%d.png", DIR_IMG_TILES, loader.getOverlayFile(), i));
        }
        overlays.put(loader.getTileId(),
                     new Overlay(loader.getTileId(), loader.getOverlayFile(), loader.getLayer(), imgs));
        return true;
    }

    @Nullable
    public Overlay getOverlayFromId(final int id) {
        if (overlays.contains(id)) {
            return overlays.get(id);
        }
        return null;
    }
}
