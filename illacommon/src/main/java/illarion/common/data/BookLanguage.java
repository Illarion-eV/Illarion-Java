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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class presents a entire book in the game in one language.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class BookLanguage implements Iterable<BookPage> {
    /**
     * The title page of the book in this language.
     */
    @Nullable
    private BookTitlePage titlePage;

    /**
     * The list of pages of this book.
     */
    @Nonnull
    private final List<BookPage> pages;

    /**
     * Create a new instance of the book language that represents a single locale.
     */
    public BookLanguage() {
        pages = new ArrayList<>();
    }

    /**
     * Load the data for this book from a XML node.
     *
     * @param source the XML node that supplies the data
     */
    public void loadData(@Nonnull Node source) {
        if (!"language".equals(source.getNodeName())) {
            return;
        }

        NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child != null) {
                //noinspection SpellCheckingInspection
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
    }

    /**
     * Check if this book has a title page.
     *
     * @return {@code true} in case this book has a title page
     */
    @Contract(pure = true)
    public boolean hasTitlePage() {
        return titlePage != null;
    }

    /**
     * Get the title page of this book.
     *
     * @return the title page or {@code null} in case there is none
     */
    @Nullable
    @Contract(pure = true)
    public BookTitlePage getTitlePage() {
        return titlePage;
    }

    /**
     * Get the amount of pages this book contains, excluding the title page.
     *
     * @return the amount of normal pages in this book
     */
    @Contract(pure = true)
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
    @Nonnull
    @Contract(pure = true)
    public BookPage getPage(int index) {
        BookPage page = pages.get(index);
        if (page == null) {
            throw new IllegalStateException("There is a illegal page inside a book");
        }
        return page;
    }

    @Nonnull
    @Override
    public Iterator<BookPage> iterator() {
        return pages.iterator();
    }
}
