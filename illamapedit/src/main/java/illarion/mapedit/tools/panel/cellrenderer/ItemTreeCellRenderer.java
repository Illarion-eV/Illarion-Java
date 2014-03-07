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

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * @author Tim
 */
public class ItemTreeCellRenderer extends JPanel implements TreeCellRenderer {

    private static final int MAX_WIDTH = 75;
    private static final int MAX_HEIGHT = 50;
    public static final Color COLOR_SELECTED = new Color(-6100481);
    public static final Font FONT = new Font("Arial", Font.BOLD, 12);

    private final JLabel label = new JLabel();
    private Image image;
    private String name;
    private String id;
    private boolean selected;

    private Dimension size;
    private final Color unselected;

    public ItemTreeCellRenderer(final Color unselected) {
        this.unselected = unselected;
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Nonnull
    @Override
    public Component getTreeCellRendererComponent(final JTree tree, @Nonnull final Object val, final boolean isSelected,
                                                  final boolean expanded, final boolean leaf, final int row,
                                                  final boolean hasFocus) {
        if (!(val instanceof ItemImg)) {
            if (isSelected) {
                label.setBackground(COLOR_SELECTED);
            } else {
                label.setBackground(unselected);
            }
            label.setText(val.toString());
            return label;
        }
        final ItemImg value = (ItemImg) val;
        selected = isSelected;

        image = value.getImgs()[0];
        name = value.getName();
        id = String.format("%d", value.getItemId());
        size = new Dimension((MAX_WIDTH * 2) - 15, MAX_HEIGHT);
        return this;
    }

    @Override
    protected void paintComponent(@Nonnull final Graphics g) {
        final int w = image.getWidth(null);
        final int h = image.getHeight(null);
        final Dimension size = getPreferredSize();

        if (selected) {
            g.setColor(COLOR_SELECTED);
        } else {
            g.setColor(unselected);
        }
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(Color.BLACK);

        final int newWidth;
        final int newHeight;

        if (w > (h)) {
            newWidth = Math.min(w, MAX_WIDTH);
            newHeight = (int) (((float) h / (float) w) * (float) newWidth);
        } else {
            newHeight = Math.min(h, MAX_HEIGHT-15);
            newWidth = (int) (((float) w / (float) h) * (float) newHeight);
        }

        g.setFont(FONT);
        g.drawImage(image, 50, 20, newWidth, newHeight, null);
        g.drawString(name, 0, 15);
        g.drawString(id, 0, 30);
    }
}
