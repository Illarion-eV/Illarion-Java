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
package illarion.mapedit.tools.panel.components.models;

import illarion.mapedit.resource.Song;

import javax.annotation.Nullable;
import javax.swing.table.AbstractTableModel;

public class SongTableModel extends AbstractTableModel {
    private String[] columnNames = { "ID", "File" };

    private Song[] songs;

    /**
     * Default constructor
     * @param songs Array with songs to show.
     */
    public SongTableModel(final Song[] songs) {
        this.songs = songs;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return songs.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Nullable
    public Object getValueAt(int row, int col) {
        Object retVal = null;
        switch (col) {
            case 0:
                retVal = songs[row].getClipID();
                break;
            case 1:
                retVal = songs[row].getFileName();
                break;
        }
        return retVal;
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Nullable
    public Song getSongAtRow(final int selectedRow) {
        Song retVal = null;
        if (selectedRow >= 0 && selectedRow < songs.length) {
            retVal = songs[selectedRow];
        }
        return retVal;
    }
}
