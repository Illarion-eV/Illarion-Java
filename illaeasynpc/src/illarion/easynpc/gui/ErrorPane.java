/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;

import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;

/**
 * This error pane is showing all needed components to display the errors
 * occurred in the script.
 * 
 * @author Martin Karing
 * @since 1.00
 */
final class ErrorPane extends JPanel {
    public static final class ErrorPaneTableModel extends AbstractTableModel {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private final List<Editor> errorEditors = new ArrayList<Editor>();

        protected ErrorPaneTableModel() {
            // nothing
        }

        public void addEditor(final Editor editor) {
            if (errorEditors.contains(editor)) {
                return;
            }
            errorEditors.add(editor);
        }

        public void focusError(final int rowIndex) {
            int editorCount = errorEditors.size();
            int errorCount = 0;
            ParsedNpc problemNpc;
            int localErrors = 0;
            for (int i = 0; i < editorCount; i++) {
                problemNpc = errorEditors.get(i).getErrorNpc();
                if (problemNpc == null) {
                    errorEditors.remove(i);
                    editorCount--;
                    i--;
                } else {
                    localErrors = problemNpc.getErrorCount();
                    if ((errorCount + localErrors) >= (rowIndex + 1)) {
                        final ParsedNpc.Error error =
                            problemNpc.getError(rowIndex - errorCount);
                        errorEditors.get(i).getLineToFocus(
                            error.getLine().getLineNumber());
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
        @Override
        public String getColumnName(final int column) {
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
            ParsedNpc problemNpc;
            for (int i = 0; i < editorCount; i++) {
                problemNpc = errorEditors.get(i).getErrorNpc();
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

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            int editorCount = errorEditors.size();
            int errorCount = 0;
            ParsedNpc problemNpc;
            int localErrors = 0;
            for (int i = 0; i < editorCount; i++) {
                problemNpc = errorEditors.get(i).getErrorNpc();
                if (problemNpc == null) {
                    errorEditors.remove(i);
                    editorCount--;
                    i--;
                } else {
                    localErrors = problemNpc.getErrorCount();
                    if ((errorCount + localErrors) >= (rowIndex + 1)) {
                        if (columnIndex == 1) {
                            return errorEditors.get(i).getFileName();
                        }
                        final ParsedNpc.Error error =
                            problemNpc.getError(rowIndex - errorCount);
                        if (columnIndex == 0) {
                            return error.getMessage();
                        }
                        if (columnIndex == 2) {
                            return "line "
                                + Integer.toString(error.getLine()
                                    .getLineNumber());
                        }
                    }
                    errorCount += localErrors;
                }
            }
            return null;
        }

        @Override
        public boolean isCellEditable(final int rowIndex, final int vColIndex) {
            return false;
        }

        public void removeEditor(final Editor editor) {
            errorEditors.remove(editor);
        }
    }

    /**
     * The serialization UID of this script.
     */
    private static final long serialVersionUID = 1L;

    private final JTable errorList;
    private final String errorMessage = Lang.getMsg(ErrorPane.class, "errors");
    private final JLabel summery;

    private final ErrorPaneTableModel tableModel;

    public ErrorPane() {
        super(new BorderLayout(5, 0));

        summery =
            new JLabel(String.format(errorMessage, Integer.toString(20)));
        tableModel = new ErrorPaneTableModel();
        errorList = new JTable(tableModel);

        errorList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                tableModel.focusError(errorList.getSelectedRow());
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(final MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(final MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                // TODO Auto-generated method stub

            }
        });

        final JScrollPane errorListScroll = new JScrollPane(errorList);

        final Icon errorIcon = UIManager.getIcon("OptionPane.errorIcon");
        final ComponentColorModel colorModel =
            new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
                    8, 8 }, true, false, Transparency.TRANSLUCENT,
                DataBuffer.TYPE_BYTE);
        final WritableRaster raster =
            Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
                errorIcon.getIconWidth(), errorIcon.getIconHeight(), 4, null);

        final BufferedImage image =
            new BufferedImage(colorModel, raster, false,
                new Hashtable<Object, Object>());
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

    public void addErrorEditor(final Editor editor) {
        tableModel.addEditor(editor);
        updateErrors();
    }

    public void removeErrorEditor(final Editor editor) {
        tableModel.removeEditor(editor);
        updateErrors();
    }

    public void updateErrors() {
        tableModel.fireTableDataChanged();
        summery.setText(String.format(errorMessage,
            Integer.valueOf(errorList.getRowCount())));
    }
}
