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
import illarion.mapedit.data.MapWarpPoint;
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
        final MapWarpPoint point;
        if (panel.isDelete()) {
            point = null;
        } else {
            point = new MapWarpPoint(panel.getTargetX(), panel.getTargetY(), panel.getTargetZ());
        }
        getHistory().addEntry(new WarpPlacedAction(x, y, map.getTileAt(x, y).getMapWarpPoint(), point, map));
        map.getTileAt(x, y).setMapWarpPoint(point);
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
}
