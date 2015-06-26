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
 * @author Tim
 */
public class WarpPanel extends JPanel {

    @Nonnull
    public final JSpinner xSpinner;
    @Nonnull
    public final JSpinner ySpinner;
    @Nonnull
    public final JSpinner zSpinner;
    @Nonnull
    public final JCheckBox delCheckBox;
    @Nonnull
    private final JRadioButton fillSelectedCheckbox;
    @Nonnull
    private final JRadioButton fillAreaCheckbox;

    public WarpPanel() {
        super(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(0, 2));
        xSpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));
        ySpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));
        zSpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));

        delCheckBox = new JCheckBox();
        fillSelectedCheckbox = new JRadioButton();
        fillAreaCheckbox = new JRadioButton();
        fillAreaCheckbox.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(fillAreaCheckbox);
        group.add(fillSelectedCheckbox);

        delCheckBox.addActionListener(e -> {
            xSpinner.setEnabled(!delCheckBox.isSelected());
            ySpinner.setEnabled(!delCheckBox.isSelected());
            zSpinner.setEnabled(!delCheckBox.isSelected());
        });

        panel.add(new JLabel(Lang.getMsg("tools.WarpTool.TargetX")));
        panel.add(xSpinner);
        panel.add(new JLabel(Lang.getMsg("tools.WarpTool.TargetY")));
        panel.add(ySpinner);
        panel.add(new JLabel(Lang.getMsg("tools.WarpTool.TargetZ")));
        panel.add(zSpinner);
        panel.add(new JLabel(Lang.getMsg("tools.WarpTool.Delete")));
        panel.add(delCheckBox);
        panel.add(new JLabel(Lang.getMsg("tools.FillSelected")));
        panel.add(fillSelectedCheckbox);
        panel.add(new JLabel(Lang.getMsg("tools.FillArea")));
        panel.add(fillAreaCheckbox);
        add(panel, BorderLayout.NORTH);
    }

    public int getTargetX() {
        return (Integer) xSpinner.getValue();
    }

    public int getTargetY() {
        return (Integer) ySpinner.getValue();
    }

    public int getTargetZ() {
        return (Integer) zSpinner.getValue();
    }

    public boolean isDelete() {
        return delCheckBox.isSelected();
    }

    public boolean isFillSelected() {
        return fillSelectedCheckbox.isSelected();
    }

    public boolean isFillArea() {
        return fillAreaCheckbox.isSelected();
    }
}
