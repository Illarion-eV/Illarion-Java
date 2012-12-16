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

/**
 * This class contains a single person that is listed in the credits.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class CreditsPerson implements Comparable<CreditsPerson> {
    /**
     * The name of the person.
     */
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
    public static void create(final String firstName, final String lastName, final CreditsList... lists) {
        create(firstName, null, lastName, lists);
    }

    /**
     * Create a person that is supposed to be listed in the credits and add this member to the lists.
     *
     * @param nickName the nick name of the person
     * @param lists    the lists this member is supposed to be added to
     */
    public static void create(final String nickName, final CreditsList... lists) {
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
    public static void create(final String firstName, final String nickName, final String lastName,
                              final CreditsList... lists) {
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
    private CreditsPerson(final String firstName, final String nickName, final String lastName) {
        if ((isNullOrWhitespace(firstName) || isNullOrWhitespace(lastName)) && isNullOrWhitespace(nickName)) {
            throw new IllegalArgumentException();
        }

        if (!isNullOrWhitespace(nickName)) {
            if (!isNullOrWhitespace(firstName) && !isNullOrWhitespace(lastName)) {
                name = firstName.trim() + " \"" + nickName.trim() + "\" " + lastName.trim();
                compareName = lastName.trim() + firstName.trim();
            } else {
                name = "\"" + nickName.trim() + "\"";
                compareName = nickName.trim();
            }
        } else {
            name = firstName.trim() + " " + lastName.trim();
            compareName = lastName.trim() + firstName.trim();
        }
    }

    /**
     * Get the name of this person.
     *
     * @return the name of the person
     */
    public String getName() {
        return name;
    }

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
    private static boolean isNullOrWhitespace(final String input) {
        return (input == null) || input.trim().isEmpty();
    }

    @Override
    public int compareTo(final CreditsPerson o) {
        return compareName.compareTo(o.compareName);
    }
}
