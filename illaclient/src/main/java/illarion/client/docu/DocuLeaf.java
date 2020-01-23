/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Fredrik K
 */
public final class DocuLeaf implements DocuEntry {
    @Nonnull
    private final String docuDesc;
    @Nonnull
    private final String docuTitle;

    public DocuLeaf(@Nonnull String type, @Nonnull String name) {
        docuTitle = String.format("docu.%s.%s.title", type, name);
        docuDesc = String.format("docu.%s.%s.description", type, name);
    }

    @Nonnull
    @Override
    @Contract(value = "_->fail", pure = true)
    public DocuEntry getChild(int index) {
        throw new IllegalArgumentException("There are no childs to request.");
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public int getChildCount() {
        return 0;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String getDescription() {
        return Lang.getMsg(docuDesc);
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String getTitle() {
        return Lang.getMsg(docuTitle);
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public Iterator<DocuEntry> iterator() {
        return Collections.emptyIterator();
    }
}
