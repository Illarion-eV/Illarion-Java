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

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This is a list as part of the credits.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CreditsList implements Iterable<CreditsPerson> {
    /**
     * The german name of this list.
     */
    @Nonnull
    private final String nameGerman;

    /**
     * The english name of this list.
     */
    @Nonnull
    private final String nameEnglish;

    /**
     * The persons who belong to this list.
     */
    @Nonnull
    private final List<CreditsPerson> members;

    /**
     * This flag stores if the list needs to be sorted.
     */
    private boolean listDirty;

    /**
     * Create a new credits list.
     *
     * @param name the name of the list
     */
    public CreditsList(@Nonnull String name) {
        this(name, name);
    }

    /**
     * Create a new credits list.
     *
     * @param nameGerman the german name of the list
     * @param nameEnglish the english name of the list
     */
    public CreditsList(@Nonnull String nameGerman, @Nonnull String nameEnglish) {
        this.nameGerman = nameGerman;
        this.nameEnglish = nameEnglish;

        members = new ArrayList<>();
    }

    /**
     * Add a new member to the list.
     *
     * @param person the person to add to the list
     */
    public void addMember(@Nonnull CreditsPerson person) {
        members.add(person);
        listDirty = true;
    }

    /**
     * Sort the credits list.
     */
    private void sortList() {
        if (listDirty) {
            listDirty = false;
            if (members.size() > 1) {
                Collections.sort(members);
            }
        }
    }

    /**
     * Get the german name of the list.
     *
     * @return the german name
     */
    @Nonnull
    public String getNameGerman() {
        return nameGerman;
    }

    /**
     * Get the english name of the list.
     *
     * @return the english name
     */
    @Nonnull
    public String getNameEnglish() {
        return nameEnglish;
    }

    @Nonnull
    @Override
    public Iterator<CreditsPerson> iterator() {
        sortList();
        return members.iterator();
    }

    /**
     * Get the first member of this list.
     *
     * @return the first list member
     */
    @Nonnull
    public CreditsPerson getFirst() {
        sortList();
        return Objects.requireNonNull(members.get(0));
    }
}
