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
public class TileEraserPanel extends JPanel {
    @Nonnull
    private final JRadioButton fillSelectedCheckbox;
    @Nonnull
    private final JRadioButton fillAreaCheckbox;

    /**
     * Default constructor
     */
    public TileEraserPanel() {
        super(new BorderLayout());

        JPanel brushSizePanel = new JPanel(new GridLayout(0, 2));
        fillSelectedCheckbox = new JRadioButton();
        fillAreaCheckbox = new JRadioButton();
        fillAreaCheckbox.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(fillAreaCheckbox);
        group.add(fillSelectedCheckbox);

        brushSizePanel.add(new JLabel(Lang.getMsg("tools.FillSelected")));
        brushSizePanel.add(fillSelectedCheckbox);
        brushSizePanel.add(new JLabel(Lang.getMsg("tools.FillArea")));
        brushSizePanel.add(fillAreaCheckbox);

        add(brushSizePanel, BorderLayout.NORTH);
    }

    public boolean isFillArea() {
        return fillAreaCheckbox.isSelected();
    }

    public boolean isFillSelected() {
        return fillSelectedCheckbox.isSelected();
    }
}
