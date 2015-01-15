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
package illarion.mapedit.tools.panel.components;

import illarion.mapedit.Lang;
import illarion.mapedit.data.MapItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

/**
 * @author Fredrik K
 */
public class ItemDataFields extends JPanel {
    @Nonnull
    private final JFormattedTextField durabilityField;
    @Nonnull
    private final JFormattedTextField qualityField;
    @Nullable
    private MapItem item;

    public ItemDataFields() {
        super(new GridLayout(2, 2));
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter qualityFormatter = new NumberFormatter(format);
        qualityFormatter.setValueClass(Integer.class);
        qualityFormatter.setMinimum(1);
        qualityFormatter.setMaximum(9);
        qualityFormatter.setCommitsOnValidEdit(true);
        qualityField = new JFormattedTextField(qualityFormatter);
        qualityField.setEnabled(false);
        qualityField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(@Nonnull PropertyChangeEvent e) {
                if (item == null) {
                    return;
                }
                if (!e.getPropertyName().equals("value")) {
                    return;
                }
                if (e.getSource() != qualityField) {
                    return;
                }
                int val = (int) e.getNewValue();
                item.setQuality(val);
            }
        });

        NumberFormatter durabilityFormatter = new NumberFormatter(format);
        durabilityFormatter.setValueClass(Integer.class);
        durabilityFormatter.setMinimum(0);
        durabilityFormatter.setMaximum(99);
        durabilityFormatter.setCommitsOnValidEdit(true);
        durabilityField = new JFormattedTextField(durabilityFormatter);
        durabilityField.setEnabled(false);
        durabilityField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(@Nonnull PropertyChangeEvent e) {
                if (item == null) {
                    return;
                }
                if (!e.getPropertyName().equals("value")) {
                    return;
                }
                if (e.getSource() != durabilityField) {
                    return;
                }
                int val = (int) e.getNewValue();
                item.setDurability(val);
            }
        });

        add(new JLabel(Lang.getMsg("tools.DataTool.Quality")));
        add(qualityField);

        add(new JLabel(Lang.getMsg("tools.DataTool.Durability")));
        add(durabilityField);
    }

    public void setData(@Nonnull MapItem item) {
        this.item = item;
        durabilityField.setValue(item.getDurability());
        qualityField.setValue(item.getQuality());
        durabilityField.setEnabled(true);
        qualityField.setEnabled(true);
    }

    public void clearFields() {
        item = null;
        durabilityField.setValue(null);
        qualityField.setValue(null);
        durabilityField.setEnabled(false);
        qualityField.setEnabled(false);
    }
}
