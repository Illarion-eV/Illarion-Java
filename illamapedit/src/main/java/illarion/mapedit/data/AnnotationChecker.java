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
package illarion.mapedit.data;

import illarion.mapedit.Lang;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fredrik K
 */
public class AnnotationChecker {

    public boolean isAnnotatedFill(@Nonnull Map map) {
        List<String[]> annotatedTiles = new ArrayList<>();
        for (MapPosition pos : map.getSelectedTiles()) {
            MapTile tile = map.getTileAt(pos.getX(), pos.getY());
            if (tile != null) {
                annotatedTiles.addAll(getAnnotatedObject(pos.getX(), pos.getY(), tile));
            }
        }
        if (annotatedTiles.isEmpty()) {
            return false;
        }

        return shouldEditAnyway(annotatedTiles);
    }

    public boolean isAnnotated(
            int x, int y, @Nonnull Map map, @Nonnull MapSelection mapSelection) {
        List<String[]> annotatedTiles = new ArrayList<>();
        for (MapPosition position : mapSelection.getSelectedPositions()) {
            int newX = x + (position.getX() - mapSelection.getOffsetX());
            int newY = y + (position.getY() - mapSelection.getOffsetY());
            if (map.contains(newX, newY)) {
                MapTile tile = map.getTileAt(newX, newY);
                if (tile != null) {
                    annotatedTiles.addAll(getAnnotatedObject(newX, newY, tile));
                }
            }
        }
        if (annotatedTiles.isEmpty()) {
            return false;
        }

        return shouldEditAnyway(annotatedTiles);
    }

    public boolean isAnnotated(int mapX, int mapY, @Nullable Map map) {
        if (map == null) {
            return false;
        }
        List<String[]> annotatedTiles = new ArrayList<>();
        MapTile tile = map.getTileAt(mapX, mapY);
        if (tile != null) {
            annotatedTiles.addAll(getAnnotatedObject(mapX, mapY, tile));
        }
        if (annotatedTiles.isEmpty()) {
            return false;
        }

        return shouldEditAnyway(annotatedTiles);
    }

    @Nonnull
    private static List<String[]> getAnnotatedObject(int x, int y, @Nonnull MapTile tile) {
        List<String[]> annotatedObject = new ArrayList<>();
        if (tile.hasAnnotation()) {
            String[] annoArray = {x + "", y + "", "", tile.getAnnotation()};
            annotatedObject.add(annoArray);
        }
        if (tile.getMapItems() != null) {
            tile.getMapItems().stream().filter(MapItem::hasAnnotation).forEach(item -> {
                String[] annoItemArray = {x + "", y + "", MapItem.join(item.getItemData(), ", "),
                        item.getAnnotation()};
                annotatedObject.add(annoItemArray);
            });
        }

        return annotatedObject;
    }

    private static boolean shouldEditAnyway(@Nonnull List<String[]> annotatedTiles) {
        String[] columnNames = {"X", "Y", "Item data", "Annotation"};

        String[][] dataValues = annotatedTiles.toArray(new String[annotatedTiles.size()][columnNames.length]);
        JTable annotationFields = new JTable(dataValues, columnNames);
        annotationFields.getColumnModel().getColumn(0).setPreferredWidth(15);
        annotationFields.getColumnModel().getColumn(1).setPreferredWidth(15);
        annotationFields.getColumn(columnNames[2]).setCellRenderer(new HoverCellRenderer());
        annotationFields.getColumn(columnNames[3]).setCellRenderer(new HoverCellRenderer());
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(annotationFields));

        int result = JOptionPane
                .showConfirmDialog(null, panel, Lang.getMsg("data.AnnotationChecker.Annotation_header"),
                                   JOptionPane.YES_NO_OPTION);
        return result != JOptionPane.YES_OPTION;
    }

    private static class HoverCellRenderer extends DefaultTableCellRenderer {
        @Nonnull
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            JLabel cellLabel = (JLabel) super
                    .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cellLabel.setToolTipText(cellLabel.getText());
            return cellLabel;
        }
    }
}
