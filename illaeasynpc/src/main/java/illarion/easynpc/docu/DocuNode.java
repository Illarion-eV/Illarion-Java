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
package illarion.easynpc.docu;

import illarion.easynpc.Lang;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fredrik K
 */
public class DocuNode implements DocuEntry {
    @Nonnull
    private final String docuTitle;
    @Nonnull
    private final String docuDesc;
    private String docuEx;
    private String docuSyntax;
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
        this(type, false);
    }

    public DocuNode(@Nonnull String type, boolean example) {
        children = new ArrayList<>();
        npcType = type;
        docuTitle = String.format("illarion.easynpc.parser.%s.Docu.title", npcType);
        docuDesc = String.format("illarion.easynpc.parser.%s.Docu.description", npcType);
        if (example) {
            docuEx = String.format("illarion.easynpc.parser.%s.Docu.example", npcType);
            docuSyntax = String.format("illarion.easynpc.parser.%s.Docu.syntax", npcType);
        }
    }

    @Nonnull
    @Override
    public DocuEntry getChild(int index) {
        if ((index >= 0) && (index < children.size())) {
            return children.get(index);
        }
        throw new IndexOutOfBoundsException("Index is out of range.");
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(docuDesc);
    }

    @Nullable
    @Override
    public String getExample() {
        return Lang.getMsg(docuEx);
    }

    @Nullable
    @Override
    public String getSyntax() {
        return Lang.getMsg(docuSyntax);
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(docuTitle);
    }

    public void addChild(String child) {
        children.add(new DocuLeaf(npcType, child));
    }

    public void addChild(DocuEntry childNode) {
        children.add(childNode);
    }
}
