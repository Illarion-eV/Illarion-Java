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
package illarion.common.data;

import org.jetbrains.annotations.Contract;
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
        germanBook = new BookLanguage();
        englishBook = new BookLanguage();
    }

    /**
     * Create a new book and load its data from a XML node.
     *
     * @param source the XML node that is the source of the book data
     */
    public Book(@Nonnull Node source) {
        this();
        loadData(source);
    }

    /**
     * Load the data from a node.
     *
     * @param source the source node
     */
    public void loadData(@Nonnull Node source) {
        if (!"book".equals(source.getNodeName())) {
            if (source.hasChildNodes()) {
                NodeList children = source.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child != null) {
                        loadData(child);
                    }
                }
            }
            return;
        }
        NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child != null) {
                if ("language".equals(child.getNodeName())) {
                    NamedNodeMap attributes = child.getAttributes();
                    Node idAttribute = (attributes != null) ? attributes.getNamedItem("id") : null;
                    if (idAttribute != null) {
                        if ("de".equals(idAttribute.getNodeValue())) {
                            germanBook.loadData(child);
                        } else if ("en".equals(idAttribute.getNodeValue())) {
                            englishBook.loadData(child);
                        }
                    }
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
    @Contract(pure = true)
    public BookLanguage getGermanBook() {
        return germanBook;
    }

    /**
     * Get the english version of the book.
     *
     * @return the english version of the book
     */
    @Nonnull
    @Contract(pure = true)
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
    @Contract(pure = true)
    public BookLanguage getLocalisedBook(@Nonnull Locale locale) {
        return Locale.GERMAN.equals(locale) ? getGermanBook() : getEnglishBook();
    }
}
