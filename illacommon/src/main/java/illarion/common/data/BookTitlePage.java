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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;

/**
 * This class represents the title page of a book.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class BookTitlePage {
    @Nonnull
    private static final Pattern WHITE_SPACES_PATTERN = Pattern.compile("\\s+");
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
     * Create a book title page that receives its data from a XML node.
     *
     * @param source the node that supplies the data
     */
    public BookTitlePage(@Nonnull Node source) {
        String title = null;
        String author = null;

        NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child != null) {
                if ("title".equals(child.getNodeName())) {
                    title = WHITE_SPACES_PATTERN.matcher(getNodeValue(child.getFirstChild()).trim()).replaceAll(" ");
                } else if ("author".equals(child.getNodeName())) {
                    author = WHITE_SPACES_PATTERN.matcher(getNodeValue(child.getFirstChild()).trim()).replaceAll(" ");
                }
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
    @Nonnull
    private static String getNodeValue(@Nullable Node node) {
        if (node == null) {
            return "";
        }
        String nodeValue = node.getNodeValue();
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
    @Contract(pure = true)
    public String getTitle() {
        return title;
    }

    /**
     * Get the author of the book.
     *
     * @return the author
     */
    @Nullable
    @Contract(pure = true)
    public String getAuthor() {
        return author;
    }

    /**
     * Check if this title page has a author set.
     *
     * @return {@code true} in case the author is set
     */
    @Contract(pure = true)
    public boolean hasAuthor() {
        return author != null;
    }
}
