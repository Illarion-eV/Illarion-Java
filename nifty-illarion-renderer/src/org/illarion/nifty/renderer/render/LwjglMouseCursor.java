package org.illarion.nifty.renderer.render;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.CursorLoader;

import de.lessvoid.nifty.spi.render.MouseCursor;

/**
 * The LWJGL MouseCursor that is used to connect LWJGL and the Nifty
 * MouseCursor.
 * 
 * @author Martin Karing
 * @author void
 */
public class LwjglMouseCursor implements MouseCursor {
    /**
     * The actual LWJGL cursor.
     */
    private Cursor cursor;

    /**
     * The logger that is used to output all logging messages.
     */
    private static final Logger LOG = Logger.getLogger(LwjglMouseCursor.class
        .getName());

    /**
     * Create this wrapper class from a existing LWJGL MouseCursor.
     * 
     * @param cursor the mouse cursor to wrap
     */
    public LwjglMouseCursor(final Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Create a mouse cursor by reading it from the resources using the
     * reference string.
     * 
     * @param ref the reference string
     * @param hotspotX the x coordinate of the hotspot of this cursor
     * @param hotspotY the y coordinate of the hotspot of this cursor
     */
    public LwjglMouseCursor(final String ref, final int hotspotX,
        final int hotspotY) {
        try {
            cursor = CursorLoader.get().getCursor(ref, hotspotX, hotspotY);
        } catch (IOException e) {
            LOG.log(Level.SEVERE,
                "Error reading resource of the mouse cursor.", e);
        } catch (LWJGLException e) {
            LOG.log(Level.SEVERE, "Error creating the native mouse cursor.", e);
        }
    }

    /**
     * Dispose the mouse cursor and free the resources of this cursor by doing so.
     */
    @Override
    public void dispose() {
        if (cursor != null) {
            cursor.destroy();
            cursor = null;
        }
    }
    
    /**
     * Activate the cursor and replace the current native cursor by doing so.
     */
    void enableCursor() {
        try {
            Mouse.setNativeCursor(cursor);
        } catch (LWJGLException e) {
            LOG.log(Level.WARNING, "Error activating the native cursor", e);
        }
    }
    
    /**
     * Disable any set native mouse cursor and switch back to the cursor that
     * is displayed by the native operating system.
     */
    static void disableCursor() {
        try {
            Mouse.setNativeCursor(null);
        } catch (LWJGLException e) {
            LOG.log(Level.WARNING, "Error disabling the native cursor", e);
        }
    }
}
