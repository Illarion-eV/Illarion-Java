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
    private final JCheckBox fillCheckbox;

    /**
     * Default constructor
     */
    public TileEraserPanel() {
        super(new BorderLayout());

        final JPanel brushSizePanel = new JPanel(new GridLayout(0, 2));
        final JLabel fillLabel = new JLabel(Lang.getMsg("tools.Fill"));
        fillCheckbox = new JCheckBox();
        brushSizePanel.add(fillLabel);
        brushSizePanel.add(fillCheckbox);

        add(brushSizePanel, BorderLayout.NORTH);
    }

    public boolean isFillSelected() {
        return fillCheckbox.isSelected();
    }
}
