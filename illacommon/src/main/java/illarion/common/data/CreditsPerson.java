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

import org.jetbrains.annotations.Contract;

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
    @Nonnull
    private final String compareName;

    /**
     * Create a person that is supposed to be listed in the credits and add this member to the lists.
     *
     * @param firstName the first name of the person
     * @param lastName the last name of the person
     * @param lists the lists this member is supposed to be added to
     */
    public static void create(
            @Nonnull String firstName,
            @Nonnull String lastName,
            @Nonnull CreditsList... lists) {
        create(firstName, null, lastName, lists);
    }

    /**
     * Create a person that is supposed to be listed in the credits and add this member to the lists.
     *
     * @param nickName the nick name of the person
     * @param lists the lists this member is supposed to be added to
     */
    public static void create(@Nonnull String nickName, @Nonnull CreditsList... lists) {
        create(null, nickName, null, lists);
    }

    /**
     * Create a person that is supposed to be listed in the credits and add this member to the lists.
     *
     * @param firstName the first name of the person
     * @param nickName the nick name of the person
     * @param lastName the last name of the person
     * @param lists the lists this member is supposed to be added to
     */
    public static void create(
            @Nullable String firstName,
            @Nullable String nickName,
            @Nullable String lastName,
            @Nonnull CreditsList... lists) {
        CreditsPerson person = new CreditsPerson(firstName, nickName, lastName);
        for (CreditsList list : lists) {
            list.addMember(person);
        }
    }

    /**
     * Create a person that is supposed to be listed in the credits.
     *
     * @param firstName the first name of the person
     * @param nickName the nick name of the person
     * @param lastName the last name of the person
     */
    private CreditsPerson(
            @Nullable String firstName,
            @Nullable String nickName,
            @Nullable String lastName) {
        if ((isNullOrWhitespace(firstName) || isNullOrWhitespace(lastName)) && isNullOrWhitespace(nickName)) {
            throw new IllegalArgumentException("No valid name supplied.");
        }

        if (!isNullOrWhitespace(nickName)) {
            if (!isNullOrWhitespace(firstName) && !isNullOrWhitespace(lastName)) {
                name = firstName.trim() + " \"" + nickName.trim() + "\" " + lastName.trim();
                compareName = lastName.trim() + firstName.trim();
            } else {
                name = '"' + nickName.trim() + '"';
                compareName = nickName.trim();
            }
        } else if (!isNullOrWhitespace(firstName) && !isNullOrWhitespace(lastName)) {
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
    @Contract(pure = true)
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return name;
    }

    /**
     * Check if a string is {@code null} or contains only whitespaces.
     *
     * @param input the string to test
     * @return {@code true} in case the input is {@code null} or contains only whitespaces
     */
    @Contract(value = "null->true", pure = true)
    private static boolean isNullOrWhitespace(@Nullable String input) {
        return (input == null) || input.trim().isEmpty();
    }

    @Override
    @Contract(pure = true)
    public int compareTo(@Nonnull CreditsPerson o) {
        return compareName.compareTo(o.compareName);
    }

    @Override
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj) || ((obj instanceof CreditsPerson) && equals((CreditsPerson) obj));
    }

    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable CreditsPerson other) {
        return (other != null) && name.equals(other.name);
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        return name.hashCode();
    }
}
