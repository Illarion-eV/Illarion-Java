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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * This class presents a entire book in the game in one language.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class BookLanguage implements Iterable<BookPage> {
    /**
     * The locale of this book.
     */
    private final Locale locale;

    /**
     * The title page of the book in this language.
     */
    private BookTitlePage titlePage;

    /**
     * The list of pages of this book.
     */
    private final List<BookPage> pages;

    /**
     * Create a new instance of the book language that represents a single locale.
     *
     * @param bookLocale the locale of this book
     */
    public BookLanguage(final Locale bookLocale) {
        locale = bookLocale;
        pages = new ArrayList<BookPage>();
    }

    /**
     * Load the data for this book from a XML node.
     *
     * @param source the XML node that supplies the data
     */
    public void loadData(final Node source) {
        if (!"language".equals(source.getNodeName())) {
            return;
        }

        final NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if ("titlepage".equals(child.getNodeName())) {
                if (titlePage == null) {
                    titlePage = new BookTitlePage(child);
                } else {
                    throw new IllegalStateException("Duplicated title page.");
                }
            } else if ("page".equals(child.getNodeName())) {
                pages.add(new BookPage(child));
            }
        }
    }

    /**
     * Get the locale of this book.
     *
     * @return the book locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Check if this book has a title page.
     *
     * @return {@code true} in case this book has a title page
     */
    public boolean hasTitlePage() {
        return titlePage != null;
    }

    /**
     * Get the title page of this book.
     *
     * @return the title page or {@code null} in case there is none
     */
    public BookTitlePage getTitlePage() {
        return titlePage;
    }

    /**
     * Get the amount of pages this book contains, excluding the title page.
     *
     * @return the amount of normal pages in this book
     */
    public int getPageCount() {
        return pages.size();
    }

    /**
     * Get a book page with a specified index.
     *
     * @param index the index of the book page requested
     * @return the book page assigned to the index
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0 || index &gt;= size()})
     */
    public BookPage getPage(final int index) {
        return pages.get(index);
    }

    @Override
    public Iterator<BookPage> iterator() {
        return pages.iterator();
    }
}
