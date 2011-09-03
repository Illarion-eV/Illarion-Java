/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics;

/**
 * This interface is used to implement a mouse cursor using the different render
 * implementation. The implementations should use the native method to realize
 * the mouse cursor if possible. In case its not rendering it using a sprite
 * works too.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface MouseCursor {
    /**
     * Update the mouse cursor. For non-native cursors this function is needed
     * to render the cursor at the correct location.
     */
    void update();

    /**
     * Enable the cursor. From now on this cursor is the active one and is
     * supposed to be displayed.
     */
    void enableCursor();

    /**
     * Disable the cursor. After calling this function the cursor won't be
     * displayed any longer.
     */
    void disableCursor();

    /**
     * Switch the mouse cursor so the one in the parameter is the active one
     * after the call. This is the preferred version of changing the current
     * cursor as its faster then disabling the old and enabling the new cursor.
     * 
     * @param cursor the new cursor
     * @note After all this function has the same outcome as disabling the
     *       cursor in the current instance and enabling the cursor that is hand
     *       over as parameter.
     */
    void switchCursorTo(MouseCursor newCursor);
}
