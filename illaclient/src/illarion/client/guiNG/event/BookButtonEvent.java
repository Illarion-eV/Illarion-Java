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
package illarion.client.guiNG.event;

import illarion.client.guiNG.Book;
import illarion.client.guiNG.GUI;
import illarion.client.guiNG.elements.Widget;

/**
 * This event is used to control the buttons that are used with the books.
 * 
 * @author Christopher Baker
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class BookButtonEvent implements WidgetEvent {
    /**
     * The constant used to set this button as button to the next page.
     */
    public static final int TYPE_NEXT = 1;

    /**
     * The constant used to set this button as button to the previous page.
     */
    public static final int TYPE_PREV = 0;

    /**
     * The serialization UID of this event script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The type of the BookButton.
     */
    private int buttonType;

    /**
     * The private constructor to avoid instances created uncontrolled.
     */
    private BookButtonEvent() {
        // private constructor to avoid instances created uncontrolled.
    }

    /**
     * Get a new instance of this event script. This either creates a new
     * instance of this class or returns always the same, depending on what is
     * needed for this script.
     * 
     * @return the instance of this event script that is to be used from now on
     */
    public static BookButtonEvent getInstance() {
        return new BookButtonEvent();
    }

    /**
     * Handles the navigating in the book.
     * 
     * @param source the widget this event was called from
     */
    @Override
    public void handleEvent(final Widget source) {
        final Book book = GUI.getInstance().getBook();
        if (buttonType == TYPE_PREV) {
            book.prevPage();
        }
        if (buttonType == TYPE_NEXT) {
            book.nextPage();
        }
    }

    /**
     * Sets the type of the book button.
     * 
     * @param newButtonType The new type of this button
     * @see #TYPE_NEXT
     * @see #TYPE_PREV
     */
    public void setButtonType(final int newButtonType) {
        buttonType = newButtonType;
    }
}
