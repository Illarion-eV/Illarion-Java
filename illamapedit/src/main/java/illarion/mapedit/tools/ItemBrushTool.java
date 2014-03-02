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
import illarion.mapedit.data.MapItem;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.history.GroupAction;
import illarion.mapedit.history.ItemPlacedAction;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.tools.panel.ItemBrushPanel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * @author Tim
 * @author Fredrik K
 */
public class ItemBrushTool extends AbstractTool {
    @Nonnull
    private final ItemBrushPanel panel;

    public ItemBrushTool() {
        panel = new ItemBrushPanel();
    }

    @Override
    public void clickedAt(final int x, final int y, @Nonnull final Map map) {
        final ItemPlacedAction newAction = addItem(x, y, map);
        if (newAction != null) {
            getHistory().addEntry(newAction);
        }
    }

    @Override
    public void paintSelected(final int x, final int y, @Nonnull final Map map, @Nonnull final GroupAction action) {
        final ItemPlacedAction newAction = addItem(x, y, map);
        if (newAction != null) {
            action.addAction(newAction);
        }
    }

    @Nullable
    private ItemPlacedAction addItem(final int x, final int y, @Nonnull final Map map) {
        final ItemImg item = getManager().getSelectedItem();
        if (item == null) {
            return null;
        }
        final MapTile tile = map.getTileAt(x, y);
        if (tile == null) {
            return null;
        }
        final MapItem mapItem = new MapItem(item.getItemId());
        tile.addMapItem(mapItem);
        return new ItemPlacedAction(x, y, mapItem, map);
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.ItemBrushTool");
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
