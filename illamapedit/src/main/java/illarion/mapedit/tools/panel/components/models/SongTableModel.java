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
package illarion.mapedit.tools.panel.components.models;

import illarion.mapedit.resource.Song;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.table.AbstractTableModel;

public class SongTableModel extends AbstractTableModel {
    @Nonnull
    private final String[] columnNames = {"ID", "File"};

    private final Song[] songs;

    /**
     * Default constructor
     *
     * @param songs Array with songs to show.
     */
    public SongTableModel(Song... songs) {
        this.songs = songs;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return songs.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
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

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Nullable
    public Song getSongAtRow(int selectedRow) {
        Song retVal = null;
        if (selectedRow >= 0 && selectedRow < songs.length) {
            retVal = songs[selectedRow];
        }
        return retVal;
    }
}
