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
package illarion.mapedit.tools.panel.components;

import illarion.mapedit.Lang;
import illarion.mapedit.data.MapItem;
import illarion.mapedit.events.ItemDataAnnotationEvent;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.tools.ToolManager;
import illarion.mapedit.tools.panel.components.models.ItemDataTableModel;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author Fredrik K
 */
public class ItemDataTable extends JPanel {
    private static final int PREFERRED_KEY_WIDTH = 15;
    private final ItemDataTableModel dataTableModel;
    private final JTable dataTable;
    private final AnnotationLabel annotation;
    private final JButton addDataButton;
    private final JButton removeDataButton;
    private final JButton annotationButton;

    public ItemDataTable() {
        super(new BorderLayout());
        annotation = new AnnotationLabel();
        add(annotation, BorderLayout.NORTH);

        final JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        dataTableModel = new ItemDataTableModel(new ArrayList<String>());

        dataTable = new JTable(dataTableModel);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.getColumnModel().getColumn(0).setPreferredWidth(PREFERRED_KEY_WIDTH);
        scroll.setViewportView(dataTable);
        add(scroll, BorderLayout.CENTER);

        final ResizableIcon iconAdd =  ImageLoader.getResizableIcon("edit_add") ;
        iconAdd.setDimension(new Dimension(ToolManager.ICON_SIZE, ToolManager.ICON_SIZE));

        final ResizableIcon iconRemove =  ImageLoader.getResizableIcon("edit_remove") ;
        iconRemove.setDimension(new Dimension(ToolManager.ICON_SIZE, ToolManager.ICON_SIZE));

        final ResizableIcon iconAnnotation =  ImageLoader.getResizableIcon("annotation") ;
        iconAnnotation.setDimension(new Dimension(ToolManager.ICON_SIZE, ToolManager.ICON_SIZE));

        addDataButton = new JButton();
        addDataButton.setIcon(iconAdd);
        addDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                 addData();
            }
        });

        removeDataButton = new JButton();
        removeDataButton.setIcon(iconRemove);
        removeDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dataTableModel.removeRow(dataTable.getSelectedRow());
                removeDataButton.setEnabled(dataTableModel.getRowCount() > 0);
            }
        });

        annotationButton = new JButton();
        annotationButton.setIcon(iconAnnotation);
        annotationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                addAnnotation();
            }
        });

        final JToolBar dataActions = new JToolBar();
        dataActions.setFloatable(false);
        dataActions.add(addDataButton);
        dataActions.add(removeDataButton);
        dataActions.addSeparator();
        dataActions.add(annotationButton);

        add(dataActions, BorderLayout.PAGE_END);
    }

    private void addAnnotation() {
        final JTextField annotationField = new JTextField(20);
        annotationField.setText(annotation.getAnnotation());
        final JPanel panel = new JPanel();
        panel.add(new JLabel(Lang.getMsg("tools.DataTool.Annotation")));
        panel.add(annotationField);

        final int result = JOptionPane.showConfirmDialog(null, panel,
                Lang.getMsg("tools.DataTool.Annotation_header"), JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            EventBus.publish(new ItemDataAnnotationEvent(annotationField.getText()));
        }
    }

    private void addData() {
        final JTextField keyField = new JTextField(5);
        final JTextField valueField = new JTextField(5);

        final JPanel keyValuePanel = new JPanel();
        keyValuePanel.add(new JLabel(Lang.getMsg("tools.DataTool.Key")));
        keyValuePanel.add(keyField);
        keyValuePanel.add(Box.createHorizontalStrut(15));
        keyValuePanel.add(new JLabel(Lang.getMsg("tools.DataTool.Value")));
        keyValuePanel.add(valueField);

        final int result = JOptionPane.showConfirmDialog(null, keyValuePanel,
                Lang.getMsg("tools.DataTool.Dialog_header"), JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            dataTableModel.addData(keyField.getText() + '=' + valueField.getText());
            dataTableModel.fireTableDataChanged();
            removeDataButton.setEnabled(dataTableModel.getRowCount() > 0);
        }
    }

    public void clearDataList() {
        dataTableModel.clearData();
        dataTableModel.fireTableDataChanged();
        setAnnotation("");
        addDataButton.setEnabled(false);
        removeDataButton.setEnabled(false);
        annotationButton.setEnabled(false);
    }

    public void setDataList(final MapItem item) {
        if (item.getItemData() != null) {
            dataTableModel.setData(item.getItemData());
            dataTableModel.fireTableDataChanged();
        }
        setAnnotation(item.getAnnotation());
        addDataButton.setEnabled(true);
        removeDataButton.setEnabled(dataTableModel.getRowCount() > 0);
        annotationButton.setEnabled(true);
    }

    public void setAnnotation(final String text) {
        annotation.setAnnotation(text);
    }
}