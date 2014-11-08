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
package illarion.client.docu;

import illarion.client.util.Lang;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Fredrik K
 */
public class DocuNode implements DocuEntry {
    @Nonnull
    private final String docuTitle;
    @Nonnull
    private List<DocuEntry> children;
    @Nonnull
    private final String npcType;

    /**
     * The default constructor that creates the documentation node.
     *
     * @param type The type
     */
    public DocuNode(@Nonnull final String type) {
        this(type, false);
    }

    public DocuNode(@Nonnull final String type, boolean example) {
        children = new ArrayList<>();
        npcType = type;
        docuTitle = String.format("docu.%s.title", npcType);
    }

    @Nullable
    @Override
    public DocuEntry getChild(int index) {
        return children.get(index);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(docuTitle);
    }

    public void addChild(String child) {
        children.add(new DocuLeaf(npcType, child));
    }

    @Override
    public Iterator<DocuEntry> iterator() {
        return new Iterator<DocuEntry>() {
            public int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < children.size();
            }

            @Override
            public DocuEntry next() {
                return children.get(currentIndex++);
            }

            @Override
            public void remove() {

            }
        };
    }
}
