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
package illarion.mapedit.tools.panel.cellrenderer;

import illarion.mapedit.resource.TileImg;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class TileImgCellRenderer extends JPanel implements ListCellRenderer<TileImg> {
    private static final Color COLOR_SELECTED = new Color(-6100481);
    private static final Color COLOR_UNSELECTED = new Color(-1246977);

    @Nonnull
    private final JLabel img;
    @Nonnull
    private final JLabel name;

    public TileImgCellRenderer() {
        img = new JLabel();
        name = new JLabel();

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(img);
        add(name);
    }

    @Nonnull
    @Override
    public Component getListCellRendererComponent(
            JList<? extends TileImg> list,
            TileImg value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        if (isSelected) {
            adjustColors(COLOR_SELECTED, this, img, name);
        } else {
            adjustColors(COLOR_UNSELECTED, this, img, name);
        }
        img.setIcon(new ImageIcon(value.getImg()[0]));

        if ((value.getDescription() == null) || value.getDescription().isEmpty()) {
            name.setText(value.getName());
        } else {
            name.setText(value.getDescription());
        }
        return this;
    }

    private static void adjustColors(@Nullable Color bg, @Nonnull Component... components) {
        for (Component c : components) {
            if (bg != null) {
                c.setBackground(bg);
            }
        }
    }
}
