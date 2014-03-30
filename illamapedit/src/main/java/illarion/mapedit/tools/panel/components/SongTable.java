/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.mapedit.tools.panel.components;

import illarion.mapedit.resource.Song;
import illarion.mapedit.resource.loaders.SongLoader;
import illarion.mapedit.tools.panel.components.models.SongTableModel;

import javax.annotation.Nonnull;
import javax.swing.*;

/**
 * @author Fredrik K
 */
public class SongTable extends JScrollPane {

    @Nonnull
    private final JTable songTable;
    @Nonnull
    private final SongTableModel songTableModel;

    /**
     * Default constructor
     */
    public SongTable() {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        songTableModel = new SongTableModel(SongLoader.getInstance().getSongs());
        songTable = new JTable(songTableModel);
        songTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songTable.getColumnModel().getColumn(0).setPreferredWidth(15);
        setViewportView(songTable);
    }

    /**
     * Get the selected songs clipID
     *
     * @return clipID
     */
    public int getSelectedMusicID() {
        return (Integer) songTable.getValueAt(songTable.getSelectedRow(), 0);
    }

    /**
     * Play the selected song
     */
    public void playSelectedSong() {
        Song s = songTableModel.getSongAtRow(songTable.getSelectedRow());
        if (s != null) {
            s.play();
        }
    }
}
