/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
    public void clickedAt(int x, int y, @Nonnull Map map) {
        int radius = panel.getRadius();
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
    public boolean isFillAreaAction() {
        return true;
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
    public void paintSelected(int x, int y, @Nonnull Map map, GroupAction action) {
        if (map.contains(x, y)) {
            map.setSelected(x, y, !panel.isDeselectChecked());
        }
    }
}
