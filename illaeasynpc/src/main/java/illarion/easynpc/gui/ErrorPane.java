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
package illarion.easynpc.gui;

import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

/**
 * This error pane is showing all needed components to display the errors
 * occurred in the script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class ErrorPane extends JPanel {
    public static final class ErrorPaneTableModel extends AbstractTableModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private final List<Editor> errorEditors = new ArrayList<>();

        ErrorPaneTableModel() {
            // nothing
        }

        public void addEditor(Editor editor) {
            if (errorEditors.contains(editor)) {
                return;
            }
            errorEditors.add(editor);
        }

        public void focusError(int rowIndex) {
            int editorCount = errorEditors.size();
            int errorCount = 0;
            for (int i = 0; i < editorCount; i++) {
                ParsedNpc problemNpc = errorEditors.get(i).getErrorNpc();
                if (problemNpc == null) {
                    errorEditors.remove(i);
                    editorCount--;
                    i--;
                } else {
                    int localErrors = problemNpc.getErrorCount();
                    if ((errorCount + localErrors) >= (rowIndex + 1)) {
                        ParsedNpc.Error error = problemNpc.getError(rowIndex - errorCount);
                        errorEditors.get(i).getLineToFocus(error.getLine());
                    }
                    errorCount += localErrors;
                }
            }
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        /**
         * Get the name of the columns.
         */
        @Nullable
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return Lang.getMsg(getClass(), "description");
                case 1:
                    return Lang.getMsg(getClass(), "file");
                case 2:
                    return Lang.getMsg(getClass(), "location");
            }
            return null;
        }

        @Override
        public int getRowCount() {
            int editorCount = errorEditors.size();
            int errorCount = 0;
            for (int i = 0; i < editorCount; i++) {
                ParsedNpc problemNpc = errorEditors.get(i).getErrorNpc();
                if (problemNpc == null) {
                    errorEditors.remove(i);
                    editorCount--;
                    i--;
                } else {
                    errorCount += problemNpc.getErrorCount();
                }
            }
            return errorCount;
        }

        @Nullable
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            int errorCount = 0;

            ListIterator<Editor> editorItr = errorEditors.listIterator();

            while (editorItr.hasNext()) {
                Editor editor = editorItr.next();
                ParsedNpc problemNpc = editor.getErrorNpc();
                if (problemNpc == null) {
                    editorItr.remove();
                } else {
                    int localErrors = problemNpc.getErrorCount();
                    if ((errorCount + localErrors) >= (rowIndex + 1)) {
                        if (columnIndex == 1) {
                            return editor.getFileName();
                        }
                        ParsedNpc.Error error = problemNpc.getError(rowIndex - errorCount);
                        if (columnIndex == 0) {
                            return error.getMessage();
                        }
                        if (columnIndex == 2) {
                            return "line " + Integer.toString(error.getLine());
                        }
                    }
                    errorCount += localErrors;
                }
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int vColIndex) {
            return false;
        }

        public void removeEditor(Editor editor) {
            errorEditors.remove(editor);
        }
    }

    /**
     * The serialization UID of this script.
     */
    private static final long serialVersionUID = 1L;

    @Nonnull
    private final JTable errorList;
    private final String errorMessage = Lang.getMsg(ErrorPane.class, "errors");
    @Nonnull
    private final JLabel summery;

    @Nonnull
    private final ErrorPaneTableModel tableModel;

    public ErrorPane() {
        super(new BorderLayout(5, 0));

        summery = new JLabel(String.format(errorMessage, Integer.toString(20)));
        tableModel = new ErrorPaneTableModel();
        errorList = new JTable(tableModel);

        errorList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tableModel.focusError(errorList.getSelectedRow());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }
        });

        JScrollPane errorListScroll = new JScrollPane(errorList);

        Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
        ComponentColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                                                                 new int[]{8, 8, 8, 8}, true, false,
                                                                 Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        WritableRaster raster = Raster
                .createInterleavedRaster(DataBuffer.TYPE_BYTE, errorIcon.getIconWidth(), errorIcon.getIconHeight(), 4,
                                         null);

        BufferedImage image = new BufferedImage(colorModel, raster, false, new Hashtable<>());
        errorIcon.paintIcon(null, image.getGraphics(), 0, 0);
        new ImageIcon(image.getScaledInstance(14, 14, Image.SCALE_SMOOTH));

        add(summery, BorderLayout.NORTH);
        add(errorListScroll, BorderLayout.CENTER);

        getPreferredSize();
        setMinimumSize(new Dimension(100, 150));
        setPreferredSize(new Dimension(300, 300));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        updateErrors();
    }

    public void addErrorEditor(Editor editor) {
        tableModel.addEditor(editor);
        updateErrors();
    }

    public void removeErrorEditor(Editor editor) {
        tableModel.removeEditor(editor);
        updateErrors();
    }

    void updateErrors() {
        tableModel.fireTableDataChanged();
        summery.setText(String.format(errorMessage, errorList.getRowCount()));
    }
}
