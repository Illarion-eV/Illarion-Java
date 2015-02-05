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

import illarion.common.data.BookPageEntry.Align;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents one page of a book.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class BookPage implements Iterable<BookPageEntry> {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(BookPage.class);
    /**
     * The list of entries on this page.
     */
    @Nonnull
    private final List<BookPageEntry> entries;

    /**
     * Create a new blank page.
     */
    private BookPage() {
        entries = new ArrayList<>();
    }

    /**
     * Create a book page that receives its data from a XML node.
     *
     * @param source the XML node that supplies the data
     */
    public BookPage(@Nonnull Node source) {
        this();
        NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            switch (child.getNodeName()) {
                case "headline":
                    entries.add(new BookPageEntry(true, getNodeValue(child.getFirstChild()), false,
                            Align.Center));
                    break;
                case "poem":
                    entries.add(new BookPageEntry(true, getNodeValue(child.getFirstChild()), true,
                            Align.Center));
                    break;
                case "paragraph":
                    NamedNodeMap attributes = child.getAttributes();
                    boolean showLineBreaks = getNodeValueBool(attributes.getNamedItem("showLineBreaks"));
                    Align align;
                    switch (getNodeValue(attributes.getNamedItem("align"))) {
                        case "left":
                            align = Align.Left;
                            break;
                        case "right":
                            align = Align.Right;
                            break;
                        case "center":
                            align = Align.Center;
                            break;
                        default:
                            align = Align.Left;
                    }
                    entries.add(new BookPageEntry(false, getNodeValue(child.getFirstChild()), showLineBreaks, align));
                    break;
                default:
                    log.error("Unknown page entry type: {}, expected paragraph, poem or headline", child.getNodeName());
            }
        }
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

    private static boolean getNodeValueBool(@Nullable Node node) {
        return Boolean.parseBoolean(getNodeValue(node));
    }

    @Nonnull
    @Override
    public Iterator<BookPageEntry> iterator() {
        return entries.iterator();
    }

}
