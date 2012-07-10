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
import illarion.mapedit.gui.MapPanel;
import illarion.mapedit.resource.loaders.TileLoader;
import org.apache.log4j.Logger;

/**
 * @author Tim
 */
public final class ToolManager {
    private static final Logger LOGGER = Logger.getLogger(ToolManager.class);


    private AbstractTool actualTool;
    private MapPanel mapPanel;
    private Map map;

    public ToolManager(final MapPanel mapPanel, final Map map) {
        this.mapPanel = mapPanel;

        this.map = map;
    }

    public void clickedAt(final int x, final int y) {
        if (actualTool != null) {
            actualTool.clickedAt(x, y);
        }
        System.out.printf("Clicked at X: %d Y: %d Tile:%s%n", x, y, TileLoader.getInstance().getTileFromId(map.getTileData()
                .getTileAt(x, y).getId()).getDescription());
    }

    public void setTool(final AbstractTool tool) {
        if (tool != null)
            tool.registerManager(this);
        actualTool = tool;
    }

    public Map getMap() {
        return map;
    }
}
