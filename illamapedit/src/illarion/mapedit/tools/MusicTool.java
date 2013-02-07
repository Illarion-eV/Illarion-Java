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
import illarion.mapedit.history.GroupAction;
import illarion.mapedit.history.MusicIDChangedAction;
import illarion.mapedit.tools.panel.MusicPanel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;

/**
 * TODO: Bigger brush
 * TODO: Play music
 * TODO: Load ids from table
 *
 * @author Tim
 */
public class MusicTool extends AbstractTool {

    @Nonnull
    private final MusicPanel panel;


    public MusicTool() {
        panel = new MusicPanel();
    }

    @Override
    public void clickedAt(final int x, final int y, @Nonnull final Map map) {
        final int musicID = panel.getMusicID();
        final int radius = panel.getRadius();
        final GroupAction action = new GroupAction();
        for (int i = (x - radius) + 1; i <= ((x + radius) - 1); i++) {
            for (int j = (y - radius) + 1; j <= ((y + radius) - 1); j++) {
                if (map.getTileAt(i, j).getMusicID() != musicID) {
                    action.addAction(new MusicIDChangedAction(i, j, map.getTileAt(i, j).getMusicID(), musicID, map));
                    map.setTileAt(i, j, MapTile.MapTileFactory.setMusicId(musicID, map.getTileAt(i, j)));

                }
            }
        }
        if (!action.isEmpty()) {
            getHistory().addEntry(action);
        }
    }

    @Override
    public String getLocalizedName() {
        return Lang.getMsg("tools.MusicTool");
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
