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
package illarion.mapedit.gui;

import illarion.mapedit.resource.TileImg;

import javax.swing.*;
import java.awt.*;

public class TileImgCellRenderer extends JPanel implements ListCellRenderer<TileImg> {
    private static final Color COLOR_SELECTED = new Color(-6100481);
    private static final Color COLOR_UNSELECTED = new Color(-1246977);

    private final JLabel img;
    private final JLabel name;

    public TileImgCellRenderer() {
        this.img = new JLabel();
        this.name = new JLabel();

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(img);
        add(name);
    }


    @Override
    public Component getListCellRendererComponent(final JList<? extends TileImg> jList, final TileImg value, final int index, final boolean isSelected, final boolean cellHasFocus) {

        if (isSelected) {
            adjustColors(COLOR_SELECTED, this, img, name);
        } else {
            adjustColors(COLOR_UNSELECTED, this, img, name);
        }
//        Dimension d = getPreferredSize();
//        d.width = 100;
//        d.height = 50;
//        setPreferredSize(d);

        img.setIcon(new ImageIcon(value.getImg()[0]));

        if ((value.getDescription() == null) || value.getDescription().isEmpty()) {
            name.setText(value.getName());
        } else {
            name.setText(value.getDescription());
        }
        return this;
    }

    private void adjustColors(final Color bg, final Component... components) {
        for (final Component c : components) {
//            if (fg != null) {
//                c.setForeground(fg);
//            }
            if (bg != null) {
                c.setBackground(bg);
            }
        }
    }
}
