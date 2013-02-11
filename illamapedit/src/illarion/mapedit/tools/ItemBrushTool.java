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
import illarion.mapedit.history.ItemPlacedAction;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.tools.panel.ItemBrushPanel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;

/**
 * @author Tim
 */
public class ItemBrushTool extends AbstractTool {

    @Nonnull
    private final ItemBrushPanel panel;

    public ItemBrushTool() {
        panel = new ItemBrushPanel();
    }

@Override
    public void clickedAt(final int x, final int y, @Nonnull final Map map) {
        final ItemImg item = getManager().getSelectedItem();
        final int radius = panel.getRadius();
        if (item != null) {
            for (int i = (x - radius) + 1; i <= ((x + radius) - 1); i++) {
                for (int j = (y - radius) + 1; j <= ((y + radius) - 1); j++) {
                    final MapTile tile = map.getTileAt(i, j);
                    if (tile != null) {
                        final List<MapItem> items = tile.getMapItems();
                        final MapItem itm = new MapItem(item.getItemId(), "", 0);
                        if (!items.contains(itm)) {
                            getHistory().addEntry(new ItemPlacedAction(i, j, itm, map));
                            items.add(itm);
                        }
                    }
                }
            }
        }
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
}
