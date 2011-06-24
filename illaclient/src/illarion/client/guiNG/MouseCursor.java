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
package illarion.client.guiNG;

import illarion.client.graphics.MarkerFactory;

import illarion.graphics.Sprite;

import illarion.input.InputManager;
import illarion.input.MouseManager;

/**
 * The mouse cursor implementation that is used to show the mouse cursor on the
 * screen.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class MouseCursor {
    /**
     * The image attached to the cursor.
     */
    private Sprite attachedImage = null;

    /**
     * The marker that is rendered at the location of the mouse.
     */
    private Sprite drawCursor = null;

    /**
     * The ID the current displayed cursor.
     */
    private int id = -1;

    /**
     * The instance of the mouse manager that handles the mouse input.
     */
    private final MouseManager mouse;

    /**
     * The default constructor. It loads up the first cursor that needs to be
     * drawn and fetches the needed mouse manager instance.
     */
    public MouseCursor() {
        drawCursor =
            MarkerFactory.getInstance()
                .getPrototype(MarkerFactory.CRSR_NORMAL).getSprite();
        id = MarkerFactory.CRSR_NORMAL;

        mouse = InputManager.getInstance().getMouseManager();
    }

    /**
     * Attach a image to the cursor. This image is rendered at the tip of the
     * mouse cursor.
     * 
     * @param sprite the sprite that shall be rendered at the tip of the mouse
     *            cursor
     */
    public void attachImage(final Sprite sprite) {
        attachedImage = sprite;
    }

    /**
     * Draw the cursor on the screen at the location of the real system mouse
     * cursor.
     * 
     * @param delta the time since the last render event
     */
    public void render(@SuppressWarnings("unused") final int delta) {
        if (drawCursor != null) {
            drawCursor.draw(mouse.getMousePosX(), mouse.getMousePosY());

            if (attachedImage != null) {
                attachedImage.draw(mouse.getMousePosX(), mouse.getMousePosY());
            }
        }
    }

    /**
     * Reset the cursor to its default appearance.
     */
    public void resetCursor() {
        setCursor(MarkerFactory.CRSR_NORMAL);
    }

    /**
     * Update the cursor that is displayed. The ID needs to be a valid ID for a
     * cursor graphics from the MarkerFactory.
     * 
     * @param cursorID the ID of a cursor marker from the MarkerFactory
     */
    public void setCursor(final int cursorID) {
        if (drawCursor != null) {
            if (id == cursorID) {
                return;
            }
            drawCursor = null;
        }
        if (cursorID > 0) {
            drawCursor =
                MarkerFactory.getInstance().getCommand(cursorID).getSprite();
            id = cursorID;
        }
    }
}
