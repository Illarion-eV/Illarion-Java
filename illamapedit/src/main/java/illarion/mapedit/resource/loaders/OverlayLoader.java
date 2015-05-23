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
import illarion.common.util.TableLoaderOverlay;
import illarion.common.util.TableLoaderSink;
import illarion.mapedit.resource.Overlay;
import illarion.mapedit.resource.Resource;
import illarion.mapedit.resource.loaders.TextureLoaderAwt.AwtTexture;
import org.illarion.engine.assets.TextureManager;

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
    public boolean processRecord(int line, @Nonnull TableLoaderOverlay loader) {
        Image[] imgs = new Image[SHAPE_COUNT];

        TextureManager manager = TextureLoaderAwt.getInstance();
        String resourceName = loader.getOverlayFile();
        for (int i = 0; i < SHAPE_COUNT; i++) {
            AwtTexture texture = (AwtTexture) manager
                    .getTexture(DIR_IMG_TILES, resourceName + '-' + i);
            imgs[i] = texture == null ? null : texture.getImage();
        }
        overlays.put(loader.getTileId(),
                     new Overlay(loader.getTileId(), loader.getOverlayFile(), loader.getLayer(), imgs));
        return true;
    }

    @Nullable
    public Overlay getOverlayFromId(int id) {
        if (overlays.contains(id)) {
            return overlays.get(id);
        }
        return null;
    }
}
