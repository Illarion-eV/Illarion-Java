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
package illarion.mapedit.gui.util;

import illarion.mapedit.data.Map;
import illarion.mapedit.events.map.RepaintRequestEvent;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nullable;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Fredrik K
 */
public class OpenMapTableModel extends AbstractTableModel {
    private final String[] columnNames = { "", "Map", "" };
    private final List<Map> maps;

    public OpenMapTableModel() {
        maps = new ArrayList<Map>();
    }

    @Override
    public int getRowCount() {
        return maps.size();
    }

    @Override
    public int getColumnCount() {
        return  columnNames.length;
    }

    @Override
    public void setValueAt(final Object value, final int row, final int column) {
        if (column == 0) {
            maps.get(row).setVisible((Boolean) value);
            EventBus.publish(new RepaintRequestEvent());
        }
    }

    @Override
    public String getColumnName(final int col) {
        return columnNames[col];
    }

    @Nullable
    @Override
    public Object getValueAt(final int row, final int column) {
        if (column == 0) {
            return maps.get(row).isVisible();
        }
        if (column == 1) {
             return maps.get(row);
        }
        return "";
    }

    @Override
    public Class getColumnClass(final int column) {
        if (column == 0) {
            return  Boolean.class;
        }
        if (column == 1) {
            return Map.class;
        }
        return String.class;
    }

    public void setTableData(final Collection<Map> maps) {
        this.maps.clear();
        this.maps.addAll(maps);
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return columnIndex != 1;
    }
}
