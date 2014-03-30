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
package illarion.mapedit.tools;

import illarion.mapedit.Lang;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.data.MapWarpPoint;
import illarion.mapedit.history.GroupAction;
import illarion.mapedit.history.WarpPlacedAction;
import illarion.mapedit.tools.panel.WarpPanel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * @author Tim
 */
public class WarpTool extends AbstractTool {
    private final WarpPanel panel = new WarpPanel();

    @Override
    public void clickedAt(final int x, final int y, @Nonnull final Map map) {
        final WarpPlacedAction newAction = addWarp(x, y, map);
        if (newAction != null) {
            getHistory().addEntry(newAction);
        }
    }

    @Override
    public void paintSelected(final int x, final int y, @Nonnull final Map map, @Nonnull final GroupAction action) {
        final WarpPlacedAction newAction = addWarp(x, y, map);
        if (newAction != null) {
            action.addAction(newAction);
        }
    }

    @Nullable
    public WarpPlacedAction addWarp(final int x, final int y, @Nonnull final Map map) {
        final MapTile tile = map.getTileAt(x, y);
        if (tile == null) {
            return null;
        }

        MapWarpPoint point = null;
        if (!panel.isDelete()) {
            point = new MapWarpPoint(panel.getTargetX(), panel.getTargetY(), panel.getTargetZ());
        }
        tile.setMapWarpPoint(point);
        return new WarpPlacedAction(x, y, tile.getMapWarpPoint(), point, map);
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.WarpTool");
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
