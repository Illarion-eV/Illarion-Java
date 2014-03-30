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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class represents the title page of a book.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class BookTitlePage {
    /**
     * The title that is displayed on this title page.
     */
    @Nonnull
    private final String title;

    /**
     * The author of the book displayed on this page.
     */
    @Nullable
    private final String author;

    /**
     * Create the title page by providing the title and the author of the book.
     *
     * @param title the title of the book
     * @param author the author of the book
     */
    public BookTitlePage(@Nonnull final String title, @Nullable final String author) {
        this.title = title;
        this.author = author;
    }

    /**
     * Create a book title page that receives its data from a XML node.
     *
     * @param source the node that supplies the data
     */
    public BookTitlePage(@Nonnull final Node source) {
        String title = null;
        String author = null;

        final NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if ("title".equals(child.getNodeName())) {
                title = getNodeValue(child.getFirstChild()).trim().replaceAll("\\s+", " ");
            } else if ("author".equals(child.getNodeName())) {
                author = getNodeValue(child.getFirstChild()).trim().replaceAll("\\s+", " ");
            }
        }

        if (title == null) {
            throw new IllegalStateException("No title set.");
        }

        this.title = title;
        this.author = author;
    }

    /**
     * Get the value of the node.
     *
     * @param node the node
     * @return the value of the node or a empty string
     */
    private static String getNodeValue(@Nullable final Node node) {
        if (node == null) {
            return "";
        }
        final String nodeValue = node.getNodeValue();
        if (nodeValue == null) {
            return "";
        }
        return nodeValue;
    }

    /**
     * Get the title of the book.
     *
     * @return the book title
     */
    @Nonnull
    public String getTitle() {
        return title;
    }

    /**
     * Get the author of the book.
     *
     * @return the author
     */
    @Nullable
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
