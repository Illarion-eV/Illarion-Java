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

import illarion.mapedit.events.ItemDataAddedEvent;
import illarion.mapedit.events.ItemDataRemovedEvent;
import illarion.mapedit.events.ItemItemDataChangedEvent;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nullable;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Fredrik K
 */
public class ItemDataTableModel extends AbstractTableModel {
    private static final Pattern PATTERN_DATA = Pattern.compile("(?:\\\\.|[^=\\\\]++)*");

    private final String[] columnNames = { "Key", "Value" };
    private final List<String> data;

    /**
     * Default constructor
     * @param data List with key=value data as strings
     */
    public ItemDataTableModel(final List<String> data) {
        this.data = new ArrayList<String>(data);
    }

    /**
     * Adds data to the table
     * @param keyValue a string with key=value
     */
    public void addData(final String keyValue) {
        data.add(keyValue);
        EventBus.publish(new ItemDataAddedEvent(keyValue));
    }

    /**
     * Clears the data in the table
     */
    public void clearData() {
        data.clear();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return  columnNames.length;
    }

    @Override
    public String getColumnName(final int col) {
        return columnNames[col];
    }

    @Nullable
    @Override
    public Object getValueAt(final int row, final int col) {
        final String[] dataKeyVal = split(data.get(row));
        return dataKeyVal[col];
    }

    private static String[] split(final CharSequence line) {
        final Matcher regexMatcher = PATTERN_DATA.matcher(line);
        final List<String> matches = new LinkedList<String>();
        while (regexMatcher.find()) {
            final String match = regexMatcher.group();
            if (!match.isEmpty()) {
                matches.add(match);
            }
        }
        return matches.toArray(new String[2]);
    }

    /**
     * Removes the row from the table
     * @param row the row to remove
     */
    public void removeRow(final int row) {
        if ((data.size() > row) && (row > -1)){
            data.remove(row);
            EventBus.publish(new ItemDataRemovedEvent(row));
        }
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        String newKeyVal = null;
        final String[] dataKeyVal = split(data.get(rowIndex));
        if (columnIndex == 0) {
            newKeyVal = aValue + "=" + dataKeyVal[1];  
        } else if (columnIndex == 1) {
            newKeyVal = dataKeyVal[0] + '=' + aValue; 
        }
        if (newKeyVal != null) {
            data.set(rowIndex,newKeyVal);
            EventBus.publish(new ItemItemDataChangedEvent(rowIndex, newKeyVal));
        }
    }

    /**
     * Clears the list and adds the new data
     * @param dataList List with key=value data as strings
     */
    public void setData(final Collection<String> dataList) {
        data.clear();
        data.addAll(dataList);
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return true;
    }
}
