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
package illarion.mapedit.tools;

import illarion.common.types.ServerCoordinate;
import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.data.MapTile.MapTileFactory;
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
    public void clickedAt(int x, int y, @Nonnull Map map) {
        TileIDChangedAction newAction = addTile(x, y, map);
        if (newAction != null) {
            getHistory().addEntry(newAction);
        }
    }

    @Override
    public void paintSelected(int x, int y, @Nonnull Map map, @Nonnull GroupAction action) {
        TileIDChangedAction newAction = addTile(x, y, map);
        if (newAction != null) {
            action.addAction(newAction);
        }
    }

    @Nullable
    private TileIDChangedAction addTile(int x, int y, @Nonnull Map map) {
        TileImg tile = getManager().getSelectedTile();
        if (tile == null) {
            return null;
        }
        MapTile oldTile = map.getTileAt(x, y);
        TileIDChangedAction action = null;
        if ((oldTile != null) && (oldTile.getId() != tile.getId())) {
            MapTile newTile = MapTileFactory.setId(tile.getId(), oldTile);
            action = new TileIDChangedAction(x, y, oldTile, newTile, map);
            map.setTileAt(x, y, newTile);
            newTile.setAnnotation(null);
            MapTransitions.getInstance().checkTileAndSurround(map, new ServerCoordinate(x, y, 0));
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
