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

import illarion.common.types.Location;
import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.history.GroupAction;
import illarion.mapedit.history.TileIDChangedAction;
import illarion.mapedit.processing.MapTransitions;
import illarion.mapedit.resource.TileImg;
import illarion.mapedit.tools.panel.TileBrushPanel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * @author Tim
 */
public class TileBrushTool extends AbstractTool {

    @Nonnull
    private final TileBrushPanel panel;


    public TileBrushTool() {
        panel = new TileBrushPanel();
    }

    @Override
    public void clickedAt(final int x, final int y, @Nonnull final Map map) {
        final TileImg tile = getManager().getSelectedTile();
        if (tile == null) {
            return;
        }
        final int radius = panel.getRadius();
        final GroupAction action = new GroupAction();
        for (int i = (x - radius) + 1; i <= ((x + radius) - 1); i++) {
            for (int j = (y - radius) + 1; j <= ((y + radius) - 1); j++) {
                if (!map.contains(i, j)) {
                    continue;
                }
                if (map.getTileAt(i, j).getId() != tile.getId()) {
                    final MapTile newTile = MapTile.MapTileFactory.setId(tile.getId(), map.getTileAt(i, j));
                    action.addAction(new TileIDChangedAction(i, j, map.getTileAt(i, j), newTile, map));
                    map.setTileAt(i, j, newTile);
                    MapTransitions.getInstance().checkTileAndSurround(map, new Location(i, j, 0));
                }
            }
        }
        if (!action.isEmpty()) {
            getHistory().addEntry(action);
        }
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.TileBrushTool");
    }

    @Nullable
    @Override
    public ResizableIcon getToolIcon() {
        return null;
    }

    @Nonnull
    @Override
    public JPanel getSettingsPanel() {
        return panel;
    }
}
