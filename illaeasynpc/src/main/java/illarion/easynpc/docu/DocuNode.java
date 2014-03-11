/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyNPC Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyNPC Editor.  If not, see <http://www.gnu.org/licenses/>.
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
public class DocuNode implements DocuEntry{
    @Nonnull
    private final String docuTitle;
    @Nonnull
    private final String docuDesc;
    private String docuEx;
    private String docuSyntax;
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
        docuTitle = String.format("illarion.easynpc.parser.%s.Docu.title", npcType);
        docuDesc = String.format("illarion.easynpc.parser.%s.Docu.description", npcType);
        if (example) {
            docuEx = String.format("illarion.easynpc.parser.%s.Docu.example", npcType);
            docuSyntax = String.format("illarion.easynpc.parser.%s.Docu.syntax", npcType);
        }
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
