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
        final MusicIDChangedAction newAction = addMusic(x, y, map);
        if (newAction != null) {
            getHistory().addEntry(newAction);
        }
    }

    @Override
    public void paintSelected(final int x, final int y, final Map map, final GroupAction action) {
        final MusicIDChangedAction newAction = addMusic(x, y, map);
        if (newAction != null) {
            action.addAction(newAction);
        }
    }

    @Nullable
    public MusicIDChangedAction addMusic(final int x, final int y, final Map map) {
        final MapTile tile = map.getTileAt(x, y);
        final int musicID = panel.getMusicID();
        if ((tile == null) || (tile.getMusicID() == musicID)) {
            return null;
        }
        MapTile newTile = MapTile.MapTileFactory.setMusicId(musicID, tile);
        newTile.setSelected(tile.isSelected());
        map.setTileAt(x, y, newTile);
        return new MusicIDChangedAction(x, y, tile.getMusicID(), musicID, map);
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
