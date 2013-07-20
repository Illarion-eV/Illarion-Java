/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
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
import illarion.mapedit.tools.panel.components.ItemTree;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;

/**
 * @author Tim
 * @author Fredrik K
 */
public class ItemBrushPanel extends JPanel {
    @Nonnull
    private final JCheckBox fillCheckbox;

    public ItemBrushPanel() {
        super(new BorderLayout());

        add(new ItemTree(), BorderLayout.CENTER);

        final JPanel brushSizePanel = new JPanel(new GridLayout(0, 2));
        final JLabel radiusLabel = new JLabel(Lang.getMsg("tools.Fill"));
        fillCheckbox = new JCheckBox();
        brushSizePanel.add(radiusLabel);
        brushSizePanel.add(fillCheckbox);

        add(brushSizePanel, BorderLayout.SOUTH);
    }

    public boolean isFillSelected() {
        return fillCheckbox.isSelected();
    }
}
