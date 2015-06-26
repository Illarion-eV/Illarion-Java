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

import illarion.mapedit.events.ItemDataAddedEvent;
import illarion.mapedit.events.ItemDataRemovedEvent;
import illarion.mapedit.events.ItemItemDataChangedEvent;
import org.bushe.swing.event.EventBus;

import javax.annotation.Nonnull;
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

    private final String[] columnNames = {"Key", "Value"};
    @Nonnull
    private final List<String> data;

    /**
     * Default constructor
     *
     * @param data List with key=value data as strings
     */
    public ItemDataTableModel(@Nonnull List<String> data) {
        this.data = new ArrayList<>(data);
    }

    /**
     * Adds data to the table
     *
     * @param keyValue a string with key=value
     */
    public void addData(String keyValue) {
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
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Nullable
    @Override
    public Object getValueAt(int row, int col) {
        String[] dataKeyVal = split(data.get(row));
        return dataKeyVal[col];
    }

    @Nonnull
    private static String[] split(@Nonnull CharSequence line) {
        Matcher regexMatcher = PATTERN_DATA.matcher(line);
        List<String> matches = new LinkedList<>();
        while (regexMatcher.find()) {
            String match = regexMatcher.group();
            if (!match.isEmpty()) {
                matches.add(match);
            }
        }
        return matches.toArray(new String[2]);
    }

    /**
     * Removes the row from the table
     *
     * @param row the row to remove
     */
    public void removeRow(int row) {
        if ((data.size() > row) && (row > -1)) {
            data.remove(row);
            EventBus.publish(new ItemDataRemovedEvent(row));
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String newKeyVal = null;
        String[] dataKeyVal = split(data.get(rowIndex));
        if (columnIndex == 0) {
            newKeyVal = aValue + "=" + dataKeyVal[1];
        } else if (columnIndex == 1) {
            newKeyVal = dataKeyVal[0] + '=' + aValue;
        }
        if (newKeyVal != null) {
            data.set(rowIndex, newKeyVal);
            EventBus.publish(new ItemItemDataChangedEvent(rowIndex, newKeyVal));
        }
    }

    /**
     * Clears the list and adds the new data
     *
     * @param dataList List with key=value data as strings
     */
    public void setData(@Nonnull Collection<String> dataList) {
        data.clear();
        data.addAll(dataList);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}
