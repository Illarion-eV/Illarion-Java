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

import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.history.GroupAction;
import illarion.mapedit.history.TileIDChangedAction;
import illarion.mapedit.tools.panel.TileEraserPanel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * @author Tim
 * @author Fredrik K
 */
public class TileEraserTool extends AbstractTool {
    @Nonnull
    private final TileEraserPanel panel;

    public TileEraserTool() {
        panel = new TileEraserPanel();
    }

    @Override
    public void clickedAt(final int x, final int y, @Nonnull final Map map) {
        final TileIDChangedAction newAction = eraseTile(x, y, map);
        if (newAction != null) {
            getHistory().addEntry(newAction);
        }
    }

    @Override
    public void paintSelected(final int x, final int y, @Nonnull final Map map, @Nonnull final GroupAction action) {
        final TileIDChangedAction newAction = eraseTile(x, y, map);
        if (newAction != null) {
            action.addAction(newAction);
        }
    }

    @Nullable
    private static TileIDChangedAction eraseTile(final int x, final int y, @Nonnull final Map map) {
        final MapTile oldTile = map.getTileAt(x, y);
        if (oldTile == null) {
            return null;
        }
        final MapTile newTile = MapTile.MapTileFactory.createNew(0, 0, 0, 0);
        map.setTileAt(x,y,newTile);

        return new TileIDChangedAction(x, y, oldTile, newTile, map);
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.TileEraser");
    }

    @Nullable
    @Override
    public ResizableIcon getToolIcon() {
        return null;
    }

    @Nullable
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
