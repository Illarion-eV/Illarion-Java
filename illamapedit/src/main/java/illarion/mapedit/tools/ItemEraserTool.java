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
import illarion.mapedit.history.GroupAction;
import illarion.mapedit.history.ItemPlacedAction;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;

/**
 * @author Tim
 */
public class ItemEraserTool extends AbstractTool {

    @Override
    public void clickedAt(final int x, final int y, @Nonnull final Map map) {
        if (!map.contains(x, y)) {
            return;
        }
        final List<MapItem> items = map.getTileAt(x, y).getMapItems();
        if (!items.isEmpty()) {
            final GroupAction action = new GroupAction();
            for (final MapItem item : items) {
                action.addAction(new ItemPlacedAction(x, y, item, null, map));
            }
            getHistory().addEntry(action);
            items.clear();
        }
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.ItemEraserTool");
    }

    @Nullable
    @Override
    public ResizableIcon getToolIcon() {
        return null;
    }

    @Nullable
    @Override
    public JPanel getSettingsPanel() {
        return null;
    }
}
