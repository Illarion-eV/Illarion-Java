/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.data;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the title page of a book.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class BookTitlePage {
    /**
     * The title that is displayed on this title page.
     */
    private final String title;

    /**
     * The author of the book displayed on this page.
     */
    private final String author;

    /**
     * Create the title page by providing the title and the author of the book.
     *
     * @param title  the title of the book
     * @param author the author of the book
     */
    public BookTitlePage(final String title, final String author) {
        this.title = title;
        this.author = author;
    }

    /**
     * Create a book title page that receives its data from a XML node.
     *
     * @param source the node that supplies the data
     */
    public BookTitlePage(final Node source) {
        String title = null;
        String author = null;

        final NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if ("title".equals(child.getNodeName())) {
                title = child.getFirstChild().getNodeValue().trim().replaceAll("\\s+", " ");
            } else if ("author".equals(child.getNodeName())) {
                author = child.getFirstChild().getNodeValue().trim().replaceAll("\\s+", " ");
            }
        }

        this.title = title;
        this.author = author;
    }

    /**
     * Get the title of the book.
     *
     * @return the book title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the author of the book.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Check if this title page has a author set.
     *
     * @return {@code true} in case the author is set
     */
    public boolean hasAuthor() {
        return author != null;
    }
}
