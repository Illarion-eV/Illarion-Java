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

/**
 * This class represents one page of a book.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class BookPage implements Iterable<BookPageEntry> {
    /**
     * The list of entries on this page.
     */
    private final List<BookPageEntry> entries;

    /**
     * Create a new blank page.
     */
    private BookPage() {
        entries = new ArrayList<BookPageEntry>();
    }

    /**
     * Create a book page that receives its data from a XML node.
     *
     * @param source the XML node that supplies the data
     */
    public BookPage(final Node source) {
        this();
        final NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if ("headline".equals(child.getNodeName())) {
                entries.add(new BookPageEntry(true, child.getFirstChild().getNodeValue()));
            } else if ("paragraph".equals(child.getNodeName())) {
                entries.add(new BookPageEntry(false, child.getFirstChild().getNodeValue()));
            }
        }
    }

    @Override
    public Iterator<BookPageEntry> iterator() {
        return entries.iterator();
    }

    /**
     * Get the amount of book page entries on this book page.
     *
     * @return the amount of book page entries
     */
    public int getEntryCount() {
        return entries.size();
    }

    /**
     * Get a book page entry with a specified index.
     *
     * @param index the index of the book page entry requested
     * @return the book page entry assigned to the index
     * @throws IndexOutOfBoundsException if the index is out of range ({@code index &lt; 0 || index &gt;= size()})
     */
    public BookPageEntry getEntry(final int index) {
        return entries.get(index);
    }
}
