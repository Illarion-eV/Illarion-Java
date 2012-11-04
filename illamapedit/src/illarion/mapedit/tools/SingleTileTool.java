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

import illarion.common.util.Location;
import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.history.TileIDChangedAction;
import illarion.mapedit.processing.MapTransitions;
import illarion.mapedit.resource.TileImg;
import illarion.mapedit.tools.panel.SingleTilePanel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.swing.*;

/**
 * @author Tim
 */
public class SingleTileTool extends AbstractTool {

    private final SingleTilePanel panel;

    public SingleTileTool() {
        panel = new SingleTilePanel();
    }

    @Override
    public void clickedAt(final int x, final int y, final Map map) {
        if (!map.contains(x, y)) {
            return;
        }
        final TileImg tile = getManager().getSelectedTile();

        if ((tile == null) || (map.getTileAt(x, y).getId() == tile.getId())) {
            return;
        }
        final int tileId = tile.getId();

        final MapTile newTile = new MapTile(tileId, map.getTileAt(x, y));
        getHistory().addEntry(new TileIDChangedAction(x, y, map.getTileAt(x, y), newTile, map));
        map.setTileAt(x, y, newTile);
        MapTransitions.getInstance().checkTileAndSurround(map, new Location(x, y, 0));
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.SingleTileTool");
    }

    @Override
    public ResizableIcon getToolIcon() {
        return null;
    }

    @Override
    public JPanel getSettingsPanel() {
        return panel;
    }

    @Override
    public void settingsChanged() {

    }
}
