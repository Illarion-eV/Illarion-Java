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
import java.util.Iterator;

/**
 * @author Fredrik K
 */
public class DocuLeaf implements DocuEntry {
    @Nonnull
    private final String docuDesc;
    @Nonnull
    private final String docuTitle;

    public DocuLeaf(String type, String name) {
        docuTitle = String.format("docu.%s.%s.title", type, name);
        docuDesc = String.format("docu.%s.%s.description", type, name);
    }

    @Nullable
    @Override
    public DocuEntry getChild(int index) {
        throw new IllegalArgumentException("There are no childs to request.");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(docuDesc);
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(docuTitle);
    }

    @Override
    public Iterator<DocuEntry> iterator() {
        return new Iterator<DocuEntry>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public DocuEntry next() {
                return null;
            }

            @Override
            public void remove() {

            }
        };
    }
}
