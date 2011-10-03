package illarion.graphics.lwjgl;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.CursorLoader;

import illarion.graphics.MouseCursor;

public class MouseCursorLWJGL implements MouseCursor {

    private final Cursor internalCursor;

    public MouseCursorLWJGL(final String resName, final int hotspotX,
        final int hotspotY) {
        Cursor newCursor = null;
        try {
            newCursor =
                CursorLoader.get().getCursor(resName, hotspotX, hotspotY);
        } catch (IOException e) {
        } catch (LWJGLException e) {
        }
        internalCursor = newCursor;
    }

    @Override
    public void update(int x, int y) {
        // updating the cursor location is not needed in this implementation.
    }

    @Override
    public void enableCursor() {
        try {
            Mouse.setNativeCursor(internalCursor);
        } catch (LWJGLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void disableCursor() {
        try {
            Mouse.setNativeCursor(null);
        } catch (LWJGLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void switchCursorTo(MouseCursor newCursor) {
        if (newCursor != null && newCursor instanceof MouseCursorLWJGL) {
            try {
                Mouse
                    .setNativeCursor(((MouseCursorLWJGL) newCursor).internalCursor);
            } catch (LWJGLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
