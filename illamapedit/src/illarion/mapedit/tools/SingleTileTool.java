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
package illarion.mapedit.tools;

import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.resource.TileImg;
import org.apache.log4j.Logger;

/**
 * @author Tim
 */
public class SingleTileTool extends AbstractTool {
    /**
     * The logger instance for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SingleTileTool.class);

    @Override
    public void clickedAt(final int x, final int y) {
        final Map m = getManager().getMap();
        final TileImg tile = getManager().getSelectedTile();
        if (tile != null) {
            m.setTileAt(x, y, new MapTile(tile.getId(), 0));
            LOGGER.debug("SingleTileTool: " + tile.getDescription());
        }
    }

    @Override
    public void dragged(final int x1, final int y1, final int x2, final int y2) {
        //Do nothing
    }
}
