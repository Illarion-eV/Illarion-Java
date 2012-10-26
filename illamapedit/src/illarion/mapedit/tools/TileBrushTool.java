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
import illarion.mapedit.tools.panel.TileBrushPanel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.swing.*;

/**
 * @author Tim
 */
public class TileBrushTool extends AbstractTool {

    private final TileBrushPanel panel;

    private int radius = 1;

    public TileBrushTool() {
        panel = new TileBrushPanel(this);
    }

    @Override
    public void clickedAt(final int x, final int y, final Map map) {
        final TileImg tile = getManager().getSelectedTile();
        System.out.println("TileBrushTool.clickedAt(" + x + ", " + y + ", map);");
        for (int i = x - radius; i <= (x + radius); i++) {
            for (int j = y - radius; j <= (y + radius); j++) {
                if (!map.contains(i, j)) {
                    System.out.println(i + "   " + j + "  no");
                    continue;
                }
                System.out.println(i + "   " + j + "  ok");
                map.setTileAt(i, j, new MapTile(tile.getId(), map.getTileAt(i, j)));
            }

        }
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.TileBrushTool");
    }

    @Override
    public ResizableIcon getToolIcon() {
        return null;
    }

    @Override
    public JPanel getSettingsPanel() {
        return panel;
    }

    @Override
    public void settingsChanged() {
        radius = panel.getRadius();
    }
}
