/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2013 - Illarion e.V.
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
import illarion.mapedit.tools.panel.DataPanel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * @author Fredrik K
 */
public class DataTool extends AbstractTool {
    @Nonnull
    protected DataPanel panel;

    public DataTool() {
        panel = new DataPanel();
    }

    @Override
    public void clickedAt(final int x, final int y, final Map map) {
        final MapTile tile = map.getTileAt(x,y);

        if (tile != null) {
            panel.setItems(tile.getMapItems());
            map.setActiveTile(x,y);
        }
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.DataTool");
    }

    @Nullable
    @Override
    public ResizableIcon getToolIcon() {
        return null;
    }

    @Override
    public JPanel getSettingsPanel() {
        return panel;
    }
}