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
import javax.annotation.Nullable;

/**
 * This class contains a single person that is listed in the credits.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CreditsPerson implements Comparable<CreditsPerson> {
    /**
     * The name of the person.
     */
    @Nonnull
    private final String name;

    /**
     * The string used to compare the name with others.
     */
    private final String compareName;

    /**
     * Create a person that is supposed to be listed in the credits and add this member to the lists.
     *
     * @param firstName the first name of the person
     * @param lastName  the last name of the person
     * @param lists     the lists this member is supposed to be added to
     */
    public static void create(@Nonnull final String firstName, @Nonnull final String lastName,
                              @Nonnull final CreditsList... lists) {
        create(firstName, null, lastName, lists);
    }

    /**
     * Create a person that is supposed to be listed in the credits and add this member to the lists.
     *
     * @param nickName the nick name of the person
     * @param lists    the lists this member is supposed to be added to
     */
    public static void create(@Nonnull final String nickName, @Nonnull final CreditsList... lists) {
        create(null, nickName, null, lists);
    }

    /**
     * Create a person that is supposed to be listed in the credits and add this member to the lists.
     *
     * @param firstName the first name of the person
     * @param nickName  the nick name of the person
     * @param lastName  the last name of the person
     * @param lists     the lists this member is supposed to be added to
     */
    public static void create(@Nullable final String firstName, @Nullable final String nickName, @Nullable final String lastName,
                              @Nonnull final CreditsList... lists) {
        final CreditsPerson person = new CreditsPerson(firstName, nickName, lastName);
        for (final CreditsList list : lists) {
            list.addMember(person);
        }
    }

    /**
     * Create a person that is supposed to be listed in the credits.
     *
     * @param firstName the first name of the person
     * @param nickName  the nick name of the person
     * @param lastName  the last name of the person
     */
    private CreditsPerson(@Nullable final String firstName, @Nullable final String nickName, @Nullable final String lastName) {
        if ((isNullOrWhitespace(firstName) || isNullOrWhitespace(lastName)) && isNullOrWhitespace(nickName)) {
            throw new IllegalArgumentException("No valid name supplied.");
        }

        if (!isNullOrWhitespace(nickName)) {
            assert nickName != null;
            if (!isNullOrWhitespace(firstName) && !isNullOrWhitespace(lastName)) {
                assert firstName != null;
                assert lastName != null;
                name = firstName.trim() + " \"" + nickName.trim() + "\" " + lastName.trim();
                compareName = lastName.trim() + firstName.trim();
            } else {
                name = '"' + nickName.trim() + '"';
                compareName = nickName.trim();
            }
        } else if (!isNullOrWhitespace(firstName) && !isNullOrWhitespace(lastName)) {
            assert firstName != null;
            assert lastName != null;
            name = firstName.trim() + ' ' + lastName.trim();
            compareName = lastName.trim() + firstName.trim();
        } else {
            throw new IllegalArgumentException("No valid name supplied.");
        }
    }

    /**
     * Get the name of this person.
     *
     * @return the name of the person
     */
    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String toString() {
        return name;
    }

    /**
     * Check if a string is {@code null} or contains only whitespaces.
     *
     * @param input the string to test
     * @return {@code true} in case the input is {@code null} or contains only whitespaces
     */
    private static boolean isNullOrWhitespace(@Nullable final String input) {
        return (input == null) || input.trim().isEmpty();
    }

    @Override
    public int compareTo(@Nonnull final CreditsPerson o) {
        return compareName.compareTo(o.compareName);
    }

    @Override
    public boolean equals(@Nullable final Object other) {
        if (super.equals(other)) {
            return true;
        }
        if (other instanceof CreditsPerson) {
            return name.equals(((CreditsPerson) other).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
