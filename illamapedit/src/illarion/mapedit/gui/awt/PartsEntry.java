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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import illarion.mapedit.tools.parts.DisableListener;
import illarion.mapedit.tools.parts.MousePartsManager;

/**
 * A parts entry shows the graphical representation of the part and a name next
 * to it.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class PartsEntry extends Panel implements MouseListener,
    DisableListener {
    /**
     * Internal class for the image display of the entry.
     * 
     * @author Martin Karing
     * @since 0.99
     */
    private static final class ImagePanel extends Panel {
        /**
         * The serialization UID of the image panel.
         */
        private static final long serialVersionUID = 1L;

        /**
         * The image to draw.
         */
        private final Image drawImage;

        /**
         * The height of the image that shall be drawn.
         */
        private final int imageHeight;

        /**
         * The width of the image that shall be drawn.
         */
        private final int imageWidth;

        /**
         * The x coordinate where the image shall be drawn.
         */
        private int imageX;

        /**
         * The y coordinate where the image shall be drawn.
         */
        private int imageY;

        /**
         * Create a image panel that displays a specified image.
         * 
         * @param image the image that needs to be displayed
         */
        public ImagePanel(final Image image) {
            super();
            setPreferredSize(new Dimension(MAX_IMAGE_WIDTH + 10,
                MAX_IMAGE_HEIGHT + 10));

            if (image != null) {
                int newWidth = image.getWidth(null);
                int newHeight = image.getHeight(null);
                if (newWidth > MAX_IMAGE_WIDTH) {
                    newHeight *= (float) MAX_IMAGE_WIDTH / (float) newWidth;
                    newWidth = MAX_IMAGE_WIDTH;
                }
                if (newHeight > MAX_IMAGE_HEIGHT) {
                    newWidth *= (float) MAX_IMAGE_HEIGHT / (float) newHeight;
                    newHeight = MAX_IMAGE_HEIGHT;
                }

                imageWidth = newWidth;
                imageHeight = newHeight;

                if (((newWidth == image.getWidth(null)) && (newHeight == image
                    .getHeight(null))) || !PRECALCULATE_SCALEDOWN) {
                    drawImage = image;
                } else {
                    drawImage =
                        image.getScaledInstance(newWidth, newHeight,
                            Image.SCALE_SMOOTH);
                }

                imageX = ((MAX_IMAGE_WIDTH + 10) - newWidth) / 2;
                imageY = ((MAX_IMAGE_HEIGHT + 10) - newHeight) / 2;
            } else {
                imageWidth = 0;
                imageHeight = 0;
                drawImage = null;
            }
        }

        /**
         * Overwritten layout function to ensure that the location of the image
         * is calculated correctly.
         */
        @Override
        public void doLayout() {
            super.doLayout();
            if (drawImage != null) {
                imageX = (getWidth() - imageWidth) / 2;
                imageY = (getHeight() - imageHeight) / 2;
            }
        }

        /**
         * Overwritten paint method that ensures that the image is properly
         * drawn.
         * 
         * @param g the graphics object that is used to draw the image
         */
        @Override
        public void paint(final Graphics g) {
            if (drawImage != null) {
                final Graphics2D myGraphics2D = (Graphics2D) g;
                myGraphics2D.drawImage(drawImage, imageX, imageY, imageWidth,
                    imageHeight, getBackground(), null);
            }
        }

        /**
         * Overwritten update method to ensure that the paint method is called.
         * 
         * @param g the graphic object that is needed to the paint method
         * @see #paint(Graphics)
         */
        @Override
        public void update(final Graphics g) {
            paint(g);
        }
    }

    /**
     * The maximal height of the image
     */
    private static final int MAX_IMAGE_HEIGHT = 32;

    /**
     * The maximal width of the image
     */
    private static final int MAX_IMAGE_WIDTH = 64;

    /**
     * A flag that causes if set to <code>true</code> that the image that is
     * displayed is scaled down before its rendered, to improve the render
     * speed. Else its scaled down in order to reduce the needed memory.
     */
    private static final boolean PRECALCULATE_SCALEDOWN = true;

    /**
     * The serialization UID of this entry.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The ID of this part
     */
    private final int partId;

    /**
     * The type of this part. This is either a tile or a item part.
     */
    private final int partType;

    /**
     * Construct one part entry.
     * 
     * @param img the image displayed by this entry
     * @param name the name displayed by the entry
     * @param type the type of this entry
     * @param id the ID of this entry to be reported to the parts manager
     */
    public PartsEntry(final Image img, final String name, final int type,
        final int id) {
        super(new BorderLayout());

        add(new ImagePanel(img), BorderLayout.WEST);
        final Label text = new Label(name, Label.LEFT);
        add(text, BorderLayout.CENTER);
        partType = type;
        partId = id;
        addMouseListener(this);
    }

    /**
     * Overwritten method that ensures that mouse listeners added to this object
     * are added to all child objects as well.
     * 
     * @param l the mouse listener that is supposed to be added
     */
    @Override
    public synchronized void addMouseListener(final MouseListener l) {
        super.addMouseListener(l);
        for (int i = 0, n = getComponentCount(); i < n; i++) {
            getComponent(i).addMouseListener(l);
        }
    }

    /**
     * Called when the user clicks with the mouse on the entry.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseClicked(final MouseEvent e) {
        MousePartsManager.getInstance().setActivePart(e.getButton(), partType,
            partId, this);
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                setBackground(Color.ORANGE);
                break;
            case MouseEvent.BUTTON2:
                setBackground(Color.CYAN);
                break;
            case MouseEvent.BUTTON3:
                setBackground(Color.LIGHT_GRAY);
                break;
            default:
                break;
        }
    }

    /**
     * Called when a the mouse points at the entry.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseEntered(final MouseEvent e) {
        // nothing to do
    }

    /**
     * Called when a the mouse stops pointint at the entry.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseExited(final MouseEvent e) {
        // nothing to do
    }

    /**
     * Called when a mouse button is pressed.
     * 
     * @param e the mouse event
     */
    @Override
    public void mousePressed(final MouseEvent e) {
        // nothing to do
    }

    /**
     * Called when a mouse button is released.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseReleased(final MouseEvent e) {
        // nothing to do
    }

    /**
     * Overwritten method that ensures that mouse listeners removed from this
     * object are removed from all child objects as well.
     * 
     * @param l the mouse listener that is supposed to be removed
     */
    @Override
    public synchronized void removeMouseListener(final MouseListener l) {
        super.removeMouseListener(l);
        for (int i = 0, n = getComponentCount(); i < n; i++) {
            getComponent(i).removeMouseListener(l);
        }
    }

    /**
     * Called in case this part is not selected any longer.
     */
    @Override
    public void reportDisable() {
        setBackground(SystemColor.window);
    }

    /**
     * Overwritten background method that ensures that a change of the
     * background of this object causes a change of the background of all child
     * objects.
     * 
     * @param color the new color to show
     */
    @Override
    public void setBackground(final Color color) {
        super.setBackground(color);
        for (int i = 0, n = getComponentCount(); i < n; i++) {
            getComponent(i).setBackground(color);
        }
    }
}
