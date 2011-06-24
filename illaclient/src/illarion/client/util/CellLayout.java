/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * Default layout of the dialogs that are not shown with real ingame fitting
 * graphics.
 * 
 * @author Nop
 * @since 0.92
 * @version 0.92
 */
public final class CellLayout implements LayoutManager {

    /**
     * Horizontal gap between the elements.
     */
    private static final int HGAP = 5;

    /**
     * Vertical gab between the elements.
     */
    private static final int VGAP = 5;

    /**
     * Width of the largest component on the layout.
     */
    private int labWidth;

    /**
     * Width of the largest text area on the layout.
     */
    private int txtWidth;

    /**
     * Default constructor.
     */
    public CellLayout() {
        // nothing to do
    }

    /**
     * Adds the specified component with the specified name to the layout.
     * 
     * @param name the component name
     * @param comp the component to be added
     * @see java.awt.LayoutManager#addLayoutComponent(String, Component)
     */
    @Override
    public void addLayoutComponent(final String name, final Component comp) {
        // nothing to do
    }

    /**
     * Lays out the container in the specified panel.
     * 
     * @param parent the component which needs to be laid out
     */
    @Override
    public void layoutContainer(final Container parent) {
        final int len = parent.getComponentCount();

        Component lab = null;
        Component txt;

        int height = 0;
        int totHeight = 0;
        // enlarge display if required, but never reduce size below minimum
        final int curWidth =
            Math.max(txtWidth, parent.getWidth() - labWidth - HGAP);
        for (int i = 0; i < len; i++) {
            if ((i % 2) == 0) {
                lab = parent.getComponent(i);
            } else if (lab != null) {
                txt = parent.getComponent(i);
                height =
                    Math.max(lab.getPreferredSize().height,
                        txt.getPreferredSize().height);
                lab.setBounds(0, totHeight, labWidth,
                    lab.getPreferredSize().height);
                txt.setBounds(labWidth + HGAP, totHeight, curWidth, height);
                // txt.getPreferredSize().width, height);
                totHeight += height + VGAP;
            }
        }
    }

    /**
     * Calculates the minimum size dimensions for the specified panel given the
     * components in the specified parent container.
     * 
     * @param parent the component to be laid out
     * @return the minimal dimension of the layout
     * @see #preferredLayoutSize
     * @see java.awt.LayoutManager#minimumLayoutSize(Container)
     */
    @Override
    public Dimension minimumLayoutSize(final Container parent) {
        return preferredLayoutSize(parent);
    }

    /**
     * Calculates the preferred size dimensions for the specified panel given
     * the components in the specified parent container.
     * 
     * @param parent the component to be laid out
     * @return the dimension of the preferred layout
     * @see #minimumLayoutSize
     * @see java.awt.LayoutManager#preferredLayoutSize(Container)
     */
    @Override
    public Dimension preferredLayoutSize(final Container parent) {
        final int len = parent.getComponentCount();

        // Component comp;
        Dimension compSize;

        txtWidth = 0;
        labWidth = 0;
        int lastHeight = 0;
        int totHeight = 0;
        for (int i = 0; i < len; i++) {
            compSize = parent.getComponent(i).getPreferredSize();
            if ((i % 2) == 0) {
                labWidth = Math.max(labWidth, compSize.width);
                lastHeight = compSize.height;
            } else {
                txtWidth = Math.max(txtWidth, compSize.width);
                totHeight += Math.max(lastHeight, compSize.height);
            }
        }
        return new Dimension(labWidth + HGAP + txtWidth, totHeight
            + (((len / 2) - 1) * VGAP));
    }

    /**
     * Removes the specified component from the layout.
     * 
     * @param comp the component to be removed
     * @see java.awt.LayoutManager#removeLayoutComponent(Component)
     */
    @Override
    public void removeLayoutComponent(final Component comp) {
        // nothing to do
    }
}
