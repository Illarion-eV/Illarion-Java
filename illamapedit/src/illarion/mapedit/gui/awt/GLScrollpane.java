/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import illarion.mapedit.MapEditor;

import illarion.common.util.Rectangle;

/**
 * This class defines a scroll pane that virtually scrolls a canvas around that
 * is used to display graphical renderings. The scrolling is done by changing
 * the offset values of all render graphics.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class GLScrollpane extends Panel {
    static final class GLScrollpaneLayout implements LayoutManager2 {
        /**
         * The constraint constant to add a component as content element.
         */
        @SuppressWarnings("nls")
        public static final String CONTENT = "content";

        /**
         * The constraint constant to add a component as horizontal scrolling
         * element.
         */
        @SuppressWarnings("nls")
        public static final String SCROLL_HORIZONTAL = "hScroll";

        /**
         * The constraint constant to add a component as vertical scrolling
         * element.
         */
        @SuppressWarnings("nls")
        public static final String SCROLL_VERTICAL = "vScroll";

        /**
         * Storage for the content component.
         */
        private Component content;

        /**
         * Storage for the horizontal scrolling component.
         */
        private Component hScroll;

        /**
         * Storage for the vertical scrolling component.
         */
        private Component vScroll;

        /**
         * Add a component to this layout manager.
         * 
         * @param comp the component that is added
         * @param constraints the constraint that defines the location where it
         *            is added
         */
        @Override
        @SuppressWarnings("nls")
        public void addLayoutComponent(final Component comp,
            final Object constraints) {
            synchronized (comp.getTreeLock()) {
                if (SCROLL_VERTICAL.equals(constraints)) {
                    vScroll = comp;
                } else if (SCROLL_HORIZONTAL.equals(constraints)) {
                    hScroll = comp;
                } else if (CONTENT.equals(constraints)) {
                    content = comp;
                } else {
                    throw new IllegalArgumentException("Illegal constraint: "
                        + constraints.toString());
                }
            }
        }

        /**
         * Add a component to this layout manager.
         * 
         * @param name the constraint that defines the location where it is
         *            added
         * @param comp the component that is added
         * @deprecated replaced by
         *             {@link #addLayoutComponent(Component, Object)}
         */
        @Override
        @Deprecated
        public void addLayoutComponent(final String name, final Component comp) {
            addLayoutComponent(comp, name);
        }

        /**
         * Get how this layout wants to be aligned relative to other components.
         * 
         * @param the target container of this layout manager
         * @return the relative alignment
         */
        @Override
        public float getLayoutAlignmentX(final Container target) {
            return 0.5f;
        }

        /**
         * Get how this layout wants to be aligned relative to other components.
         * 
         * @param the target container of this layout manager
         * @return the relative alignment
         */
        @Override
        public float getLayoutAlignmentY(final Container target) {
            return 0.5f;
        }

        /**
         * Remove cached values in case there are any.
         * 
         * @param the target container of this layout manager
         */
        @Override
        public void invalidateLayout(final Container target) {
            // there are no cached versions so nothing needs to be removed
        }

        /**
         * Place the components correctly according to the specifications of
         * this layout manager.
         * 
         * @param parent the parent container
         */
        @Override
        public void layoutContainer(final Container parent) {
            synchronized (parent.getTreeLock()) {
                final Insets insets = parent.getInsets();
                final int top = insets.top;
                int bottom = parent.getHeight() - insets.bottom;
                final int left = insets.left;
                int right = parent.getWidth() - insets.right;

                vScroll.setSize(vScroll.getWidth(),
                    bottom - top - hScroll.getHeight());
                final Dimension vScrollDim = vScroll.getPreferredSize();
                vScroll.setBounds(right - vScrollDim.width, top,
                    vScrollDim.width, bottom - top - hScroll.getHeight());
                right -= vScrollDim.width;

                hScroll.setSize(right - left, hScroll.getHeight());
                final Dimension hScrollDim = hScroll.getPreferredSize();
                hScroll.setBounds(left, bottom - hScrollDim.height, right
                    - left, hScrollDim.height);
                bottom -= hScrollDim.height;

                content.setBounds(left, top, right - left, bottom - top);
            }
        }

        /**
         * Get the maximum size of this layout.
         * 
         * @param the target container of this layout manager
         * @return the maximum size this layout is able to cover
         */
        @Override
        public Dimension maximumLayoutSize(final Container target) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        /**
         * Get the minimal valid size of this container.
         * 
         * @param parent the container that is the parent of this operation
         * @return the minimal dimension of this container
         */
        @Override
        public Dimension minimumLayoutSize(final Container parent) {
            final Dimension hScrollDim = hScroll.getMinimumSize();
            final Dimension vScrollDim = vScroll.getMinimumSize();
            final Dimension contentDim = content.getMinimumSize();

            return new Dimension(contentDim.width + vScrollDim.width,
                contentDim.height + hScrollDim.height);
        }

        /**
         * Get the preferred size of this container.
         * 
         * @param parent the container that is the parent of this operation
         * @return the preferred dimension of this container
         */
        @Override
        public Dimension preferredLayoutSize(final Container parent) {
            final Dimension hScrollDim = hScroll.getPreferredSize();
            final Dimension vScrollDim = vScroll.getPreferredSize();
            final Dimension contentDim = content.getPreferredSize();

            return new Dimension(contentDim.width + vScrollDim.width,
                contentDim.height + hScrollDim.height);
        }

        /**
         * Remove a component from this layout.
         * 
         * @param comp the component that is to be removed
         */
        @Override
        public void removeLayoutComponent(final Component comp) {
            if (comp == hScroll) {
                hScroll = null;
            } else if (comp == vScroll) {
                vScroll = null;
            } else if (comp == content) {
                content = null;
            }
        }

    }

    /**
     * The serialization UID of this scroll pane.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The used content component.
     */
    private final Component content;

    /**
     * The used horizontal scrollbar.
     */
    private final Scrollbar hScrollbar;

    /**
     * The virtual height of the content element.
     */
    private int virtualHeight;

    /**
     * The virtual width of the content element.
     */
    private int virtualWidth;

    /**
     * The x coordinate of the virtual origin.
     */
    private int virtualX;

    /**
     * The y coordinate of the virtual origin.
     */
    private int virtualY;

    /**
     * The used vertical scrollbar.
     */
    private final Scrollbar vScrollbar;

    /**
     * Create a new instance of this scrollpane.
     * 
     * @param displayContent the component that is displayed in the content area
     */
    public GLScrollpane(final Component displayContent) {
        super(new GLScrollpaneLayout());

        hScrollbar = new Scrollbar(Scrollbar.HORIZONTAL);
        hScrollbar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(final AdjustmentEvent e) {
                updateXOffset(e.getValue());
            }
        });

        vScrollbar = new Scrollbar(Scrollbar.VERTICAL);
        vScrollbar.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(final AdjustmentEvent e) {
                updateYOffset(e.getValue());
            }
        });

        content = displayContent;

        add(hScrollbar, GLScrollpaneLayout.SCROLL_HORIZONTAL);
        add(vScrollbar, GLScrollpaneLayout.SCROLL_VERTICAL);
        add(content, GLScrollpaneLayout.CONTENT);

        virtualWidth = 500;
        virtualHeight = 500;
    }

    /**
     * Overwritten doLayout method to recalculate the values of the scrollbars
     * correctly.
     */
    @Override
    public void doLayout() {
        super.doLayout();
        calculateScrollbars();
    }

    /**
     * Report that the zoom value changed. In case that happens the scrollbars
     * need to be recalculated.
     */
    public void reportZoomChanged() {
        calculateScrollbars();
    }

    /**
     * Set the virtual size of this scroll pane.
     * 
     * @param rect the rectangle of the required area
     */
    public void setVirtualSize(final Rectangle rect) {
        virtualX = rect.getX();
        virtualY = rect.getY();
        virtualHeight = rect.getHeight();
        virtualWidth = rect.getWidth();
        calculateScrollbars();
    }

    /**
     * Update the X offset value that is displayed on the screen. This method
     * uses the raw value from the scrollbar. Recalculations regarding the zoom
     * are done here.
     * 
     * @param newValue the new x offset value
     */
    void updateXOffset(final int newValue) {
        final int scrollMin = hScrollbar.getMinimum();
        final float zoom = MapEditor.getDisplay().getZoom();
        final int adjValue = (int) ((newValue - scrollMin) / zoom);
        MapEditor.getDisplay().setOffsetX(scrollMin + adjValue);
    }

    /**
     * Update the Y offset value that is displayed on the screen. This method
     * uses the raw value from the scrollbar. Recalculations regarding the zoom
     * and the required invertation of the scrollbar are done here.
     * 
     * @param newValue the new y offset value
     */
    void updateYOffset(final int newValue) {
        final int scrollMin = vScrollbar.getMinimum();
        final float zoom = MapEditor.getDisplay().getZoom();
        final int invValue =
            vScrollbar.getMaximum() - (newValue - scrollMin)
                - content.getHeight();
        final int adjValue = (int) ((invValue - scrollMin) / zoom);
        MapEditor.getDisplay().setOffsetY(scrollMin + adjValue);
    }

    /**
     * Calculate the values of the scrollbars.
     */
    private void calculateScrollbars() {
        final float zoom = MapEditor.getDisplay().getZoom();

        final int usedX = virtualX;
        final int usedY = virtualY;
        final int usedW = (int) (virtualWidth * zoom);
        final int usedH = (int) (virtualHeight * zoom);
        int xOffset;
        int yOffset;
        vScrollbar.setValues(vScrollbar.getValue(), content.getHeight(),
            usedY, usedY + usedH);
        hScrollbar.setValues(hScrollbar.getValue(), content.getWidth(), usedX,
            usedX + usedW);
        if (content.getHeight() >= usedH) {
            vScrollbar.setEnabled(false);
            yOffset = usedY - ((content.getHeight() - usedH) / 2);
        } else {
            vScrollbar.setEnabled(true);
            yOffset = vScrollbar.getValue();
        }
        if (content.getWidth() >= usedW) {
            hScrollbar.setEnabled(false);
            xOffset = usedX - ((content.getWidth() - usedW) / 2);
        } else {
            hScrollbar.setEnabled(true);
            xOffset = hScrollbar.getValue();
        }

        updateXOffset(xOffset);
        updateYOffset(yOffset);
    }
}
