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
package illarion.client.resources;

import illarion.client.util.IdWrapper;
import illarion.common.data.Book;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The book factory stores the references to the books.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class BookFactory implements ResourceFactory<IdWrapper<String>> {
    /**
     * The logger instance of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(BookFactory.class);

    /**
     * The map that stores the file names in relation to the book IDs.
     */
    @Nonnull
    private final Map<Integer, String> fileMap;

    /**
     * The map that stores the book data in relation to the book IDs.
     */
    @Nonnull
    private final Map<Integer, Reference<Book>> bookMap;

    /**
     * The singleton instance of this factory.
     */
    private static final BookFactory INSTANCE = new BookFactory();

    /**
     * Get the singleton instance of this factory.
     *
     * @return the singleton instance of this factory
     */
    @Nonnull
    public static BookFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Default constructor.
     */
    private BookFactory() {
        fileMap = new HashMap<Integer, String>();
        bookMap = new HashMap<Integer, Reference<Book>>();
    }

    @Override
    public void init() {
        // nothing to do
    }

    @Override
    public void loadingFinished() {
        // nothing
    }

    /**
     * Store a resource in this factory.
     *
     * @param resource the resource to store
     */
    @Override
    public void storeResource(@Nonnull final IdWrapper<String> resource) {
        if (getBookUrl(resource.getObject()) == null) {
            LOGGER.error("Book ID: " + Integer.toString(resource.getId()) + " not found. File "
                    + resource.getObject() + ".book.xml is missing in the resources.");
        } else {
            fileMap.put(resource.getId(), resource.getObject());
        }
    }

    /**
     * Get the URL of a book with a specific ID.
     *
     * @param id the ID of the book
     * @return the URL to the book resource
     */
    @Nullable
    private URL getBookUrl(final int id) {
        return getBookUrl(fileMap.get(id));
    }

    /**
     * Get the URL of a book with a specific base name.
     *
     * @param baseName the base name of the book
     * @return the URL to the book resource
     */
    @Nullable
    private static URL getBookUrl(@Nullable final String baseName) {
        if (baseName == null) {
            return null;
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("books/");
        builder.append(baseName);
        builder.append(".book.xml");

        return Thread.currentThread().getContextClassLoader().getResource(builder.toString());
    }

    /**
     * Fetch a book from this factory.
     *
     * @param id the ID of the book
     * @return the book with all its data
     */
    @Nullable
    public Book getBook(final int id) {
        final Reference<Book> bookReference = bookMap.get(id);
        Book requestedBook = null;
        if (bookReference != null) {
            requestedBook = bookReference.get();
        }

        if (requestedBook == null) {
            final URL bookUrl = getBookUrl(id);
            if (bookUrl == null) {
                LOGGER.error("Book resource not found: " + Integer.toString(id));
                return null;
            }

            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setIgnoringComments(true);
            docBuilderFactory.setIgnoringElementContentWhitespace(true);
            docBuilderFactory.setNamespaceAware(true);
            docBuilderFactory.setValidating(false);
            try {
                final Document document = docBuilderFactory.newDocumentBuilder().parse(bookUrl.openStream());
                requestedBook = new Book(document);
                bookMap.put(id, new SoftReference<Book>(requestedBook));
            } catch (@Nonnull final ParserConfigurationException e) {
                LOGGER.error("Setting up XML parser failed!", e);
            } catch (@Nonnull final SAXException e) {
                LOGGER.error("Parsing Book XML file failed!", e);
            } catch (@Nonnull final IOException e) {
                LOGGER.error("Reading Book XML file failed!", e);
            }
        }

        return requestedBook;
    }
}
