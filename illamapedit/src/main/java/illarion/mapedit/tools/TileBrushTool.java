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
 * @author Fredrik K
 */
public class TileBrushTool extends AbstractTool {
    @Nonnull
    private final TileBrushPanel panel;

    public TileBrushTool() {
        panel = new TileBrushPanel();
    }

    @Override
    public void clickedAt(final int x, final int y, @Nonnull final Map map) {
        TileIDChangedAction newAction = addTile(x,y,map);
        if (newAction != null) {
            getHistory().addEntry(newAction);
        }
    }

    @Override
    public void paintSelected(final int x, final int y, @Nonnull final Map map, @Nonnull final GroupAction action) {
        TileIDChangedAction newAction = addTile(x,y,map);
        if (newAction != null) {
            action.addAction(newAction);
        }
    }

    @Nullable
    private TileIDChangedAction addTile(final int x, final int y, @Nonnull final Map map) {
        final TileImg tile = getManager().getSelectedTile();
        if (tile == null) {
            return null;
        }
        final MapTile oldTile = map.getTileAt(x, y);
        TileIDChangedAction action = null;
        if ((oldTile != null) && (oldTile.getId() != tile.getId())) {
            final MapTile newTile = MapTile.MapTileFactory.setId(tile.getId(), oldTile);
            action = new TileIDChangedAction(x, y, oldTile, newTile, map);
            map.setTileAt(x, y, newTile);
            newTile.setAnnotation(null);
            MapTransitions.getInstance().checkTileAndSurround(map, new Location(x, y, 0));
        }
        return action;
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

    @Override
    public boolean isFillAreaAction() {
        return panel.isFillArea();
    }

    @Override
    public boolean isFillSelected() {
        return panel.isFillSelected();
    }

    @Override
    public boolean isWarnAnnotated() {
        return true;
    }
}
