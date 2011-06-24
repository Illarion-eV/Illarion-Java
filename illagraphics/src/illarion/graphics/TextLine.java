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
 * This is the interface for lines of graphical rendered text. The lines
 * rendered with with the implementations of this interface are simple lines
 * without align. They are just rendered to the base line and take as much space
 * as they need.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface TextLine {
    /**
     * Update the layout in case its dirty. That updates the needed glyphes and
     * their coordinates.
     */
    void layout();

    /**
     * Render the text to its location on the screen.
     */
    void render();

    /**
     * Set the color that is used to render the line. Note that this does not
     * copy the instance of the color. It just stores the reference. So if the
     * color of the instance is changed later this color will change as well.
     * 
     * @param newColor the color that shall be used for the render actions from
     *            now on
     */
    void setColor(SpriteColor newColor);

    /**
     * Set the cursor to a specified location in the text. The cursor will be
     * displayed right before the glyph at this position.
     * 
     * @param newCursorPos 0 is the first position, length value of the string
     *            the last
     */
    void setCursorPosition(int newCursorPos);

    /**
     * Set the visible state of the cursor. This is used to hide or show the
     * cursor directly.
     * 
     * @param newVisible <code>true</code> in case the cursor how shall be
     *            visible
     */
    void setCursorVisible(boolean newVisible);

    /**
     * Set the font that is used to render this text line. And exception will be
     * thrown in case the implementation of the font does not fit.
     * 
     * @param newFont the new font that is used
     */
    void setFont(RenderableFont newFont);

    /**
     * Set the location of the lower left position of this text line.
     * 
     * @param newX the x coordinate of the lower left dot of the text line
     * @param newY the y coordinate of the lower left dot of the text line
     */
    void setLocation(int newX, int newY);

    /**
     * Set the start and the end of the marked text.
     * 
     * @param start the index of the first glyph that is marked
     * @param end the index of the first glyph that is not marked anymore
     */
    void setMarkedRange(int start, int end);

    /**
     * Set the visible state of the marked text.
     * 
     * @param newVisible <code>true</code> in case marked text shall be shown at
     *            this line
     */
    void setMarkedVisible(boolean newVisible);

    /**
     * Set the text that is displayed with this line. This also causes that the
     * arrays for the glyphes and the coordinates are increased in case its
     * needed.
     * 
     * @param newText the text that shall be displayed with this line
     */
    void setText(String newText);

    /**
     * Toggle the display state of the cursor. So if its was shown its hidden
     * after.
     */
    void toogleCursor();
}
