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
import illarion.mapedit.data.MapPosition;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.history.GroupAction;
import illarion.mapedit.history.HistoryManager;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * @author Tim
 */
public abstract class AbstractTool {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTool.class);

    private HistoryManager history;

    private ToolManager manager;

    /**
     * X and Y are tile coordinates.
     *
     * @param x coordinate
     * @param y coordinate
     * @param map current map
     */
    public abstract void clickedAt(int x, int y, Map map);

    public abstract String getLocalizedName();

    @Nullable
    public abstract ResizableIcon getToolIcon();

    @Nullable
    public abstract JPanel getSettingsPanel();

    public abstract boolean isFillAreaAction();

    public abstract boolean isFillSelected();

    public abstract boolean isWarnAnnotated();

    public abstract void paintSelected(int x, int y, Map map, GroupAction action);

    public final void registerManager(@Nonnull final ToolManager toolManager) {
        manager = toolManager;
        history = toolManager.getHistory();
    }

    public void fillSelected(@Nonnull final Map map) {
        final GroupAction action = new GroupAction();
        for (final MapPosition pos : map.getSelectedTiles()) {
            final MapTile tile = map.getTileAt(pos.getX(), pos.getY());
            if (tile != null) {
                paintSelected(pos.getX(), pos.getY(), map, action);
            }
        }
        if (!action.isEmpty()) {
            getHistory().addEntry(action);
        }
    }

    public void fillArea(final int startX, final int startY, final int endX, final int endY, final Map map) {
        final int fromX = Math.min(startX, endX);
        final int toX = Math.max(startX, endX);
        final int fromY = Math.min(startY, endY);
        final int toY = Math.max(startY, endY);
        final GroupAction action = new GroupAction();
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                paintSelected(x, y, map, action);
            }
        }
        if (!action.isEmpty()) {
            getHistory().addEntry(action);
        }
    }

    protected final ToolManager getManager() {
        return manager;
    }

    protected final HistoryManager getHistory() {
        return history;
    }
}
