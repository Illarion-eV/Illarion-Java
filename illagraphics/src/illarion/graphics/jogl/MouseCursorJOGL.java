package illarion.graphics.jogl;

import illarion.graphics.Graphics;
import illarion.graphics.MouseCursor;
import illarion.graphics.Sprite;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * This is the mouse cursor implementation for the JOGL graphic engine.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class MouseCursorJOGL implements MouseCursor {
    /**
     * This class is used to monitor the movement of the mouse in order to drawn
     * the mouse at the proper location at the update events.
     * 
     * @author Martin Karing
     * @since 1.22
     */
    private static final class MouseMonitor implements MouseMotionListener,
        com.jogamp.newt.event.MouseListener {
        /**
         * The last recored X coordinate of the mouse location;
         */
        private int lastX;

        /**
         * The last recored Y coordinate of the mouse location;
         */
        private int lastY;

        /**
         * The public constructor, used to expose the class constructor to the
         * surrounding class.
         */
        public MouseMonitor() {
            // just to expose the constructor to the surrounding class
        }

        public int getLastX() {
            return lastX;
        }

        public int getLastY() {
            return lastY;
        }

        @Override
        public void mouseClicked(final com.jogamp.newt.event.MouseEvent arg0) {
            // nothing
        }

        @Override
        public void mouseDragged(final com.jogamp.newt.event.MouseEvent arg0) {
            // nothing
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            // nothing
        }

        @Override
        public void mouseEntered(final com.jogamp.newt.event.MouseEvent arg0) {
            // nothing
        }

        @Override
        public void mouseExited(final com.jogamp.newt.event.MouseEvent arg0) {
            // nothing
        }

        @Override
        public void mouseMoved(final com.jogamp.newt.event.MouseEvent arg0) {
            lastX = arg0.getX();
            lastY = arg0.getY();
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();
        }

        @Override
        public void mousePressed(final com.jogamp.newt.event.MouseEvent arg0) {
            // nothing
        }

        @Override
        public void mouseReleased(final com.jogamp.newt.event.MouseEvent arg0) {
            // nothing
        }

        @Override
        public void mouseWheelMoved(final com.jogamp.newt.event.MouseEvent arg0) {
            // nothing
        }
    }

    private static MouseMonitor monitor;

    /**
     * Stores if the cursor is currently active and supposed to be drawn.
     */
    private boolean enabled;

    /**
     * The sprite that is used to draw the mouse cursor. This sprite will be
     * exclusive used as mouse cursor and altered based on the settings required
     * for the cursor.
     */
    private final SpriteJOGL internalSprite;

    public MouseCursorJOGL(final Sprite sprite, final int hotspotX,
        final int hotspotY) {
        internalSprite = new SpriteJOGL((SpriteJOGL) sprite);
        internalSprite.setAlign(Sprite.HAlign.left, Sprite.VAlign.top);
        internalSprite.setOffset(-hotspotX, hotspotY);

        enabled = false;

        if (monitor == null) {
            synchronized (MouseCursorJOGL.class) {
                if (monitor == null) {
                    monitor = new MouseMonitor();
                    Graphics.getInstance().getRenderDisplay()
                        .addInputListener(monitor);
                }
            }
        }
    }

    @Override
    public void disableCursor() {
        enabled = false;
        Graphics.getInstance().getRenderDisplay().showCursor();
    }

    @Override
    public void enableCursor() {
        enabled = true;
        Graphics.getInstance().getRenderDisplay().hideCursor();
    }

    @Override
    public void switchCursorTo(final MouseCursor newCursor) {
        enabled = false;
        newCursor.enableCursor();
    }

    @Override
    public void update() {
        if (enabled) {
            internalSprite.draw(monitor.getLastX(), monitor.getLastY());
        }
    }
}
