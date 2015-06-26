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
package illarion.mapedit.tools.panel;

import illarion.mapedit.Lang;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

/**
 * @author Fredrik K
 */
public class ItemEraserPanel extends JPanel {
    @Nonnull
    private final JCheckBox clearCheckBox;
    @Nonnull
    private final JRadioButton fillSelectedCheckbox;
    @Nonnull
    private final JRadioButton fillAreaCheckbox;

    public ItemEraserPanel() {
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel(new GridLayout(0, 2));
        clearCheckBox = new JCheckBox();
        fillSelectedCheckbox = new JRadioButton();
        fillAreaCheckbox = new JRadioButton();
        fillAreaCheckbox.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(fillAreaCheckbox);
        group.add(fillSelectedCheckbox);

        northPanel.add(new JLabel(Lang.getMsg("tools.ItemEraser.Clear")));
        northPanel.add(clearCheckBox);

        northPanel.add(new JLabel(Lang.getMsg("tools.FillSelected")));
        northPanel.add(fillSelectedCheckbox);
        northPanel.add(new JLabel(Lang.getMsg("tools.FillArea")));
        northPanel.add(fillAreaCheckbox);

        add(northPanel, BorderLayout.NORTH);
    }

    public boolean isFillArea() {
        return fillAreaCheckbox.isSelected();
    }

    public boolean isFillSelected() {
        return fillSelectedCheckbox.isSelected();
    }

    public boolean shouldClear() {
        return clearCheckBox.isSelected();
    }
}
