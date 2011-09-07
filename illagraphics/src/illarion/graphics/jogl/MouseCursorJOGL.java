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
    public void update(final int x, final int y) {
        if (enabled) {
            internalSprite.draw(x, y);
        }
    }
}
