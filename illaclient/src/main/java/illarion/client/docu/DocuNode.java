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
package illarion.client.docu;

import illarion.client.util.Lang;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Fredrik K
 */
public final class DocuNode implements DocuEntry {
    @Nonnull
    private final String docuTitle;
    @Nonnull
    private final List<DocuEntry> children;
    @Nonnull
    private final String npcType;

    /**
     * The default constructor that creates the documentation node.
     *
     * @param type The type
     */
    public DocuNode(@Nonnull String type) {
        children = new ArrayList<>();
        npcType = type;
        docuTitle = String.format("docu.%s.title", npcType);
    }

    @Nonnull
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
    @Contract(value = "_->null", pure = true)
    public String getDescription() {
        return null;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String getTitle() {
        return Lang.getMsg(docuTitle);
    }

    public void addChild(@Nonnull String child) {
        children.add(new DocuLeaf(npcType, child));
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public Iterator<DocuEntry> iterator() {
        return children.iterator();
    }
}
