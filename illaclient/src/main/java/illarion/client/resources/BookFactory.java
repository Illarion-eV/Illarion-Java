/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.resources;

import illarion.client.util.IdWrapper;
import illarion.common.data.Book;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(BookFactory.class);

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
    @Nonnull
    private static final BookFactory INSTANCE = new BookFactory();

    /**
     * Get the singleton instance of this factory.
     *
     * @return the singleton instance of this factory
     */
    @Nonnull
    @Contract(pure = true)
    public static BookFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Default constructor.
     */
    private BookFactory() {
        fileMap = new HashMap<>();
        bookMap = new HashMap<>();
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
    public void storeResource(@Nonnull IdWrapper<String> resource) {
        if (getBookUrl(resource.getObject()) == null) {
            log.error("Book ID: {} not found. File {}.book.xml is missing in the resources.",
                    Integer.toString(resource.getId()), resource.getObject());
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
    @Contract(pure = true)
    private URL getBookUrl(int id) {
        String bookRef = fileMap.get(id);
        if (bookRef == null) {
            return null;
        }
        return getBookUrl(bookRef);
    }

    /**
     * Get the URL of a book with a specific base name.
     *
     * @param baseName the base name of the book
     * @return the URL to the book resource
     */
    @Nullable
    @Contract(pure = true)
    private static URL getBookUrl(@Nonnull String baseName) {
        return Thread.currentThread().getContextClassLoader().getResource("books/" + baseName + ".book.xml");
    }

    /**
     * Fetch a book from this factory.
     *
     * @param id the ID of the book
     * @return the book with all its data
     */
    @Nullable
    @Contract(pure = true)
    public Book getBook(int id) {
        Reference<Book> bookReference = bookMap.get(id);
        Book requestedBook = null;
        if (bookReference != null) {
            requestedBook = bookReference.get();
        }

        if (requestedBook == null) {
            URL bookUrl = getBookUrl(id);
            if (bookUrl == null) {
                log.error("Book resource not found: {}", Integer.toString(id));
                return null;
            }

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setIgnoringComments(true);
            docBuilderFactory.setIgnoringElementContentWhitespace(true);
            docBuilderFactory.setNamespaceAware(true);
            docBuilderFactory.setValidating(false);
            try {
                Document document = docBuilderFactory.newDocumentBuilder().parse(bookUrl.openStream());
                requestedBook = new Book(document);
                bookMap.put(id, new SoftReference<>(requestedBook));
            } catch (@Nonnull ParserConfigurationException e) {
                log.error("Setting up XML parser failed!", e);
            } catch (@Nonnull SAXException e) {
                log.error("Parsing Book XML file failed!", e);
            } catch (@Nonnull IOException e) {
                log.error("Reading Book XML file failed!", e);
            }
        }

        return requestedBook;
    }
}
