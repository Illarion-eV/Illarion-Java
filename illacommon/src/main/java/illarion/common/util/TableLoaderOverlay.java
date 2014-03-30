/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.common.util;

import javax.annotation.Nonnull;

/**
 * @author Tim
 */
public class TableLoaderOverlay extends TableLoader {

    private static final int TB_TILE_ID = 0;

    public static final int TB_OVERLAY_FILE = 1;

    public static final int TB_LAYER = 3;

    public <T extends TableLoader> TableLoaderOverlay(@Nonnull final TableLoaderSink<T> callback) {
        super("Overlays", callback);
    }

    public int getTileId() {
        return getInt(TB_TILE_ID);
    }

    public String getOverlayFile() {
        return getString(TB_OVERLAY_FILE);
    }

    public int getLayer() {
        return getInt(TB_LAYER);
    }
}
