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
package illarion.mapedit.tools.panel.cellrenderer;

import illarion.mapedit.resource.ItemImg;

import javax.swing.*;
import java.awt.*;

/**
 * @author Tim
 */
public class ItemImgCellRenderer extends JPanel implements ListCellRenderer<ItemImg> {

    private static final int MAX_WIDTH = 75;
    private static final int MAX_HEIGHT = 50;
    private static final Color COLOR_SELECTED = new Color(-6100481);

    private Image image;
    private String name;
    private boolean selected;

    private Dimension size;

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    public Component getListCellRendererComponent(final JList<? extends ItemImg> jList, final ItemImg value,
                                                  final int index, final boolean isSelected, final boolean cellHasFocus) {

        selected = isSelected;

        image = value.getImgs()[0];
        name = value.getResourceName();
        size = new Dimension(MAX_WIDTH * 2, MAX_HEIGHT);

        return this;
    }


    @Override
    protected void paintComponent(final Graphics g) {
        final int w = image.getWidth(null);
        final int h = image.getHeight(null);
        final Dimension size = getPreferredSize();

        if (selected) {
            g.setColor(COLOR_SELECTED);
            g.fillRect(0, 0, size.width, size.height);
        }
        g.setColor(Color.BLACK);
        g.drawImage(image, 0, 0, Math.min(w, MAX_WIDTH), Math.min(h, MAX_HEIGHT), null);
        g.drawString(name, MAX_WIDTH, 10);

    }
}
