/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.server.events;

/**
 * This event is published in case a book is supposed to be displayed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ShowBookEvent implements ServerEvent {
    /**
     * The ID of the book.
     */
    private final int bookId;

    /**
     * Constructor of the show book event.
     *
     * @param bookId the Id of the book to show.
     */
    public ShowBookEvent(final int bookId) {
        this.bookId = bookId;
    }

    /**
     * Get the ID of the book.
     *
     * @return the book ID
     */
    public int getBookId() {
        return bookId;
    }
}
