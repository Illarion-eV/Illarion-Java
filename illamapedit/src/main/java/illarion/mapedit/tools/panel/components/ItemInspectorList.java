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
import illarion.mapedit.events.ItemInspectorSelectedEvent;
import illarion.mapedit.events.ItemRemoveEvent;
import illarion.mapedit.events.ItemReplaceEvent;
import illarion.mapedit.events.TileAnnotationEvent;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.tools.ToolManager;
import illarion.mapedit.tools.panel.cellrenderer.MapItemCellRenderer;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * @author Fredrik K
 */
public class ItemInspectorList extends JPanel {
    @Nonnull
    private final AnnotationLabel annotation;
    @Nonnull
    private final JScrollPane scroll;
    @Nonnull
    private JList dataList;
    @Nonnull
    private final JButton removeItemButton;
    @Nonnull
    private final JButton itemUpButton;
    @Nonnull
    private final JButton itemDownButton;

    public ItemInspectorList() {
        super(new BorderLayout());
        annotation = new AnnotationLabel();
        add(annotation, BorderLayout.NORTH);

        scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scroll, BorderLayout.CENTER);

        final ResizableIcon iconRemove = ImageLoader.getResizableIcon("edit_remove");
        iconRemove.setDimension(new Dimension(ToolManager.ICON_SIZE, ToolManager.ICON_SIZE));
        final ResizableIcon iconUp = ImageLoader.getResizableIcon("1uparrow");
        iconUp.setDimension(new Dimension(ToolManager.ICON_SIZE, ToolManager.ICON_SIZE));
        final ResizableIcon iconDown = ImageLoader.getResizableIcon("1downarrow");
        iconDown.setDimension(new Dimension(ToolManager.ICON_SIZE, ToolManager.ICON_SIZE));
        final ResizableIcon iconAnnotation = ImageLoader.getResizableIcon("annotation");
        iconAnnotation.setDimension(new Dimension(ToolManager.ICON_SIZE, ToolManager.ICON_SIZE));

        removeItemButton = new JButton();
        removeItemButton.setIcon(iconRemove);
        removeItemButton.setEnabled(false);
        removeItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (dataList.getSelectedIndex() > -1) {
                    EventBus.publish(new ItemRemoveEvent(dataList.getSelectedIndex()));
                }
            }
        });

        itemUpButton = new JButton();
        itemUpButton.setIcon(iconUp);
        itemUpButton.setEnabled(false);
        itemUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int index = dataList.getSelectedIndex();
                if (index > 0) {
                    EventBus.publish(new ItemReplaceEvent(index, index - 1));
                }
            }
        });

        itemDownButton = new JButton();
        itemDownButton.setIcon(iconDown);
        itemDownButton.setEnabled(false);
        itemDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int index = dataList.getSelectedIndex();
                if ((index > -1) && (index < (dataList.getModel().getSize() - 1))) {
                    EventBus.publish(new ItemReplaceEvent(index, index + 1));
                }
            }
        });

        final JButton annotationButton = new JButton();
        annotationButton.setIcon(iconAnnotation);
        annotationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                addAnnotation();
            }
        });

        final JToolBar itemActions = new JToolBar();
        itemActions.setFloatable(false);
        itemActions.add(removeItemButton);
        itemActions.addSeparator();
        itemActions.add(itemUpButton);
        itemActions.add(itemDownButton);
        itemActions.addSeparator();
        itemActions.add(annotationButton);
        add(itemActions, BorderLayout.PAGE_END);
    }

    /**
     * Get the selected item in the list
     *
     * @return the selected MapItem
     */
    @Nonnull
    public MapItem getSelectedItem() {
        return (MapItem) dataList.getSelectedValue();
    }

    private void addAnnotation() {
        final JTextField annotationField = new JTextField(20);
        annotationField.setText(annotation.getAnnotation());

        final JPanel panel = new JPanel();
        panel.add(new JLabel(Lang.getMsg("tools.DataTool.Annotation")));
        panel.add(annotationField);

        final int result = JOptionPane.showConfirmDialog(null, panel, Lang.getMsg("tools.DataTool.Annotation_header"),
                                                         JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            EventBus.publish(new TileAnnotationEvent(annotationField.getText()));
        }
    }

    public void setAnnotation(@Nonnull final String text) {
        annotation.setAnnotation(text);
    }

    /**
     * Set items to show in the list
     *
     * @param itemList A collection of items to show
     */
    public void setDataList(@Nonnull final Collection<MapItem> itemList) {
        dataList = new JList(itemList.toArray(new MapItem[itemList.size()]));
        dataList.setCellRenderer(new MapItemCellRenderer());
        dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                EventBus.publish(new ItemInspectorSelectedEvent((MapItem) dataList.getSelectedValue()));
            }
        });

        removeItemButton.setEnabled(itemList.size() > 0);
        itemUpButton.setEnabled(itemList.size() > 1);
        itemDownButton.setEnabled(itemList.size() > 1);
        scroll.setViewportView(dataList);
    }
}
