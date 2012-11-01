/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

/**
 * @author Tim
 */
public class TableLoaderOverlay extends TableLoader {

    private static final int TB_TILE_ID = 0;

    public static final int TB_OVERLAY_FILE = 1;

    public static final int TB_LAYER = 2;

    public <T extends TableLoader> TableLoaderOverlay(final TableLoaderSink<T> callback) {
        super("Overlays", callback);
    }

    public int getTileId() {
        return getInt(TB_TILE_ID);
    }

    public String getOverlayFile() {
        return getString(TB_OVERLAY_FILE);
    }

    public String getLayer() {
        return getString(TB_LAYER);
    }
}
