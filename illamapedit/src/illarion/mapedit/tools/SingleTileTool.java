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
import illarion.mapedit.resource.TileImg;
import illarion.mapedit.tools.panel.SingleTilePanel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.swing.*;

/**
 * @author Tim
 */
public class SingleTileTool extends AbstractTool {

    private final SingleTilePanel panel;

    public SingleTileTool() {
        panel = new SingleTilePanel();
    }

    @Override
    public void clickedAt(final int x, final int y) {
        final Map m = getManager().getMap();
        if (!m.contains(x, y)) {
            return;
        }
        final TileImg tile = getManager().getSelectedTile();
        if (tile != null) {
            m.setTileAt(x, y, new MapTile(tile.getId(), 0));
        }
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.SingleTileTool");
    }

    @Override
    public ResizableIcon getToolIcon() {
        return null;
    }

    @Override
    public JPanel getSettingsPanel() {
        return panel;
    }
}
