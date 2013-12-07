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

    public boolean isAnnotatedFill(@Nonnull final Map map) {
        final List<String[]> annotatedTiles = new ArrayList<String[]>();
        for (final MapPosition pos : map.getSelectedTiles()) {
            final MapTile tile = map.getTileAt(pos.getX(),pos.getY());
            if (tile != null) {
                annotatedTiles.addAll(getAnnotatedObject(pos.getX(),pos.getY(), tile));
            }
        }
        if (annotatedTiles.isEmpty()) {
            return false;
        }

        return shouldEditAnyway(annotatedTiles);
    }

    public boolean isAnnotated(final int x, final int y, @Nonnull final Map map, @Nonnull final MapSelection mapSelection) {
        final List<String[]> annotatedTiles = new ArrayList<String[]>();
        for (final MapPosition position : mapSelection.getTiles().keySet()) {
            final int newX = x + (position.getX() - mapSelection.getOffsetX());
            final int newY = y + (position.getY() - mapSelection.getOffsetY());
            if (map.contains(newX, newY)) {
                final MapTile tile = map.getTileAt(newX,newY);
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

    public boolean isAnnotated(final int mapX, final int mapY, @Nullable final Map map) {
        if (map == null) {
            return false;
        }
        final List<String[]> annotatedTiles = new ArrayList<String[]>();
        final MapTile tile = map.getTileAt(mapX, mapY);
        if (tile != null) {
            annotatedTiles.addAll(getAnnotatedObject(mapX, mapY, tile));
        }
        if (annotatedTiles.isEmpty()) {
            return false;
        }

        return shouldEditAnyway(annotatedTiles);
    }

    @Nonnull
    private static List<String[]> getAnnotatedObject(final int x, final int y, @Nonnull final MapTile tile) {
        final List<String[]> annotatedObject = new ArrayList<String[]>();
        if(tile.hasAnnotation()) {
            final String[] annoArray = {x+"", y+"", "", tile.getAnnotation()};
            annotatedObject.add(annoArray);
        }
        if (tile.getMapItems() != null) {
            for (final MapItem item : tile.getMapItems()) {
                if (item.hasAnnotation()) {
                    final String[] annoItemArray = {x + "", y + "", MapItem.join(item.getItemData(), ", "),
                            item.getAnnotation()};
                    annotatedObject.add(annoItemArray);
                }
            }
        }

        return annotatedObject;
    }

    private static boolean shouldEditAnyway(@Nonnull final List<String[]> annotatedTiles) {
        final String[] columnNames = { "X", "Y", "Item data", "Annotation" };

        final String[][] dataValues = annotatedTiles.toArray(new String[annotatedTiles.size()][columnNames.length]);
        final JTable annotationFields = new JTable(dataValues, columnNames);
        annotationFields.getColumnModel().getColumn(0).setPreferredWidth(15);
        annotationFields.getColumnModel().getColumn(1).setPreferredWidth(15);
        annotationFields.getColumn(columnNames[2]).setCellRenderer(new HoverCellRenderer());
        annotationFields.getColumn(columnNames[3]).setCellRenderer(new HoverCellRenderer());
        final JPanel panel = new JPanel();
        panel.add(new JScrollPane(annotationFields));

        final int result = JOptionPane.showConfirmDialog(null, panel,
                Lang.getMsg("data.AnnotationChecker.Annotation_header"), JOptionPane.YES_NO_OPTION);
        return result != JOptionPane.YES_OPTION;
    }

    private static class HoverCellRenderer extends DefaultTableCellRenderer {
        @Nonnull
        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value,
                                                       final boolean isSelected, final boolean hasFocus,
                                                       final int row, final int column) {
            final JLabel cellLabel = (JLabel)super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row, column);
            cellLabel.setToolTipText(cellLabel.getText());
            return cellLabel;
        }
    }
}
