/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.data;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This is a list as part of the credits.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CreditsList implements Iterable<CreditsPerson> {
    /**
     * The german name of this list.
     */
    private final String nameGerman;

    /**
     * The english name of this list.
     */
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
    public CreditsList(final String name) {
        this(name, name);
    }

    /**
     * Create a new credits list.
     *
     * @param nameGerman the german name of the list
     * @param nameEnglish the english name of the list
     */
    public CreditsList(final String nameGerman, final String nameEnglish) {
        this.nameGerman = nameGerman;
        this.nameEnglish = nameEnglish;

        members = new ArrayList<>();
    }

    /**
     * Add a new member to the list.
     *
     * @param person the person to add to the list
     */
    public void addMember(final CreditsPerson person) {
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
    public String getNameGerman() {
        return nameGerman;
    }

    /**
     * Get the english name of the list.
     *
     * @return the english name
     */
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
    public CreditsPerson getFirst() {
        sortList();
        return members.get(0);
    }
}
