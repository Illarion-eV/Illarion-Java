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
import illarion.mapedit.history.GroupAction;
import illarion.mapedit.tools.panel.SelectionPanel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * Tool for selecting tiles
 *
 * @author Fredrik K
 */
public class SelectionTool extends AbstractTool {
    @Nonnull
    protected SelectionPanel panel;

    public SelectionTool() {
        panel = new SelectionPanel();
    }

    @Override
    public void clickedAt(final int x, final int y, @Nonnull final Map map) {
        final int radius = panel.getRadius();
        for (int i = (x - radius) + 1; i <= ((x + radius) - 1); i++) {
            for (int j = (y - radius) + 1; j <= ((y + radius) - 1); j++) {
                if (map.contains(i, j)) {
                    map.setSelected(i, j, !panel.isDeselectChecked());
                }
            }
        }
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.SelectionTool");
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
    public boolean isFillSelected() {
        return false;
    }

    @Override
    public boolean isWarnAnnotated() {
        return false;
    }

    @Override
    public void paintSelected(final int x, final int y, final Map map, final GroupAction action) {
    }
}
