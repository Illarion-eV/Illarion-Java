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
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.tools.ToolManager;
import illarion.mapedit.tools.panel.components.models.ItemDataTableModel;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Fredrik K
 */
public class ItemDataTable extends JPanel {
    private static final int PREFERRED_KEY_WIDTH = 15;
    private final ItemDataTableModel dataTableModel;
    private final JTable dataTable;

    public ItemDataTable() {
        super(new BorderLayout());
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

        final JButton addDataButton = new JButton();
        addDataButton.setIcon(iconAdd);
        addDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                 addData();
            }
        });

        final JButton removeDataButton = new JButton();
        removeDataButton.setIcon(iconRemove);
        removeDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                 dataTableModel.removeRow(dataTable.getSelectedRow());
            }
        });

        final JToolBar dataActions = new JToolBar();
        dataActions.setFloatable(false);
        dataActions.add(addDataButton);
        dataActions.add(removeDataButton);

        add(dataActions, BorderLayout.PAGE_END);
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
        }
    }

    public void clearDataList() {
        dataTableModel.clearData();
        dataTableModel.fireTableDataChanged();
    }

    public void setDataList(final Collection<String> dataList) {
        dataTableModel.setData(dataList);
        dataTableModel.fireTableDataChanged();
    }
}