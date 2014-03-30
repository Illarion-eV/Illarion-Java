/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.common.data;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import java.util.Locale;

/**
 * The content of this class represent a single book in the game. Along with all its data.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Book {
    /**
     * The german version of this book.
     */
    @Nonnull
    private final BookLanguage germanBook;

    /**
     * The english version of this book.
     */
    @Nonnull
    private final BookLanguage englishBook;

    /**
     * Create a new book.
     */
    public Book() {
        germanBook = new BookLanguage(Locale.GERMAN);
        englishBook = new BookLanguage(Locale.ENGLISH);
    }

    /**
     * Create a new book and load its data from a XML node.
     *
     * @param source the XML node that is the source of the book data
     */
    public Book(@Nonnull final Node source) {
        this();
        loadData(source);
    }

    /**
     * Load the data from a node.
     *
     * @param source the source node
     */
    public void loadData(@Nonnull final Node source) {
        if (!"book".equals(source.getNodeName())) {
            if (source.hasChildNodes()) {
                final NodeList children = source.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    loadData(children.item(i));
                }
            }
            return;
        }
        final NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if ("language".equals(child.getNodeName())) {
                final NamedNodeMap attributes = child.getAttributes();
                if ("de".equals(attributes.getNamedItem("id").getNodeValue())) {
                    germanBook.loadData(child);
                } else if ("en".equals(attributes.getNamedItem("id").getNodeValue())) {
                    englishBook.loadData(child);
                }
            }
        }
    }

    /**
     * Get the german version of the book.
     *
     * @return the german version of the book
     */
    @Nonnull
    public BookLanguage getGermanBook() {
        return germanBook;
    }

    /**
     * Get the english version of the book.
     *
     * @return the english version of the book
     */
    @Nonnull
    public BookLanguage getEnglishBook() {
        return englishBook;
    }

    /**
     * Get the localised version of this book.
     *
     * @param locale the locale that is requested
     * @return the localised version of the book
     */
    @Nonnull
    public BookLanguage getLocalisedBook(final Locale locale) {
        if (Locale.GERMAN.equals(locale)) {
            return getGermanBook();
        } else {
            return getEnglishBook();
        }
    }
}
