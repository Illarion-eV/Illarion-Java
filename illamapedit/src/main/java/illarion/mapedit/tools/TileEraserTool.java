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

import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.data.MapTile.MapTileFactory;
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
    public void clickedAt(int x, int y, @Nonnull Map map) {
        TileIDChangedAction newAction = eraseTile(x, y, map);
        if (newAction != null) {
            getHistory().addEntry(newAction);
        }
    }

    @Override
    public void paintSelected(int x, int y, @Nonnull Map map, @Nonnull GroupAction action) {
        TileIDChangedAction newAction = eraseTile(x, y, map);
        if (newAction != null) {
            action.addAction(newAction);
        }
    }

    @Nullable
    private static TileIDChangedAction eraseTile(int x, int y, @Nonnull Map map) {
        MapTile oldTile = map.getTileAt(x, y);
        if (oldTile == null) {
            return null;
        }
        MapTile newTile = MapTileFactory.createNew(0, 0, 0, 0);
        map.setTileAt(x, y, newTile);

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
