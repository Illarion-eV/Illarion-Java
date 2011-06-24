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

import illarion.graphics.common.Font;

/**
 * This interface is used for targets of the text cursor. All input a text
 * cursor fetches is forwarded to this interface.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public interface TextCursorTarget {
    /**
     * This function is called in case the user cancels the input by hitting
     * escape.
     */
    public void cancelInput();

    /**
     * This function is called once the text cursor receives a pressed enter
     * key. This could either cause that a new line is started, or that the data
     * that was written is executed.
     */
    public void executeEnter();

    /**
     * Returns the textFontSource of the chat editor
     * 
     * @return the textFontSource of the chat editor
     */
    public Font getTextFontSource();

    /**
     * Clear all characters from the underlying text.
     */
    void clear();

    /**
     * Disable the cursor of this target. This should be called in case the
     * cursor changes the target away from this one or is disabled.
     */
    void disableCursor();

    /**
     * Get the maximal length of the text this target is accepting.
     * 
     * @return the maximal amount of characters this target accepts
     */
    int getMaxLength();

    /**
     * Get the characters stored behind this target.
     * 
     * @return the characters behind this target
     */
    CharSequence getText();

    /**
     * Get the length of the text the cursor is editing here.
     * 
     * @return the length of the text in characters
     */
    int getTextLength();

    /**
     * Insert a character to the text laying behind this cursor target.
     * 
     * @param character the character that is added
     */
    void insertCharacter(char character);

    /**
     * Insert a sequence of characters to the text laying behind this cursor
     * target.
     * 
     * @param characters the characters to be added
     */
    void insertCharacters(CharSequence characters);

    /**
     * Remove a character from the current cursor position
     */
    void remove();

    /**
     * Remove a character at a given position
     * 
     * @param idx the position of the character which should be removed
     */
    void removeAt(int idx);

    /**
     * Set the location of the cursor.
     * 
     * @param pos the new location of the cursor in the text
     */
    void setCursorPosition(int pos);
}
