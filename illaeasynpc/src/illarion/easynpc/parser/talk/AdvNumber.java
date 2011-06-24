/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.parser.talk;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import illarion.easynpc.parsed.talk.AdvancedNumber;

/**
 * This class contains the required functions to parse a advanced number out of
 * a string.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public final class AdvNumber {
    /**
     * This regular expression should be used to extract a advanced number area
     * from a longer string.
     */
    @SuppressWarnings("nls")
    public static final String ADV_NUMBER_REGEXP =
        "([expr%NUMBER0-9\\.\\+\\-\\*/\\(\\)\\s]+)";

    /**
     * This pattern is used to parse a expression string out of the line.
     */
    @SuppressWarnings("nls")
    private static final Pattern EXPRESSION_PATTERN = Pattern
        .compile("^expr\\(([^\\\\)]+)\\)$");

    /**
     * This pattern is used to parse a number value out of a string.
     */
    @SuppressWarnings("nls")
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^(\\d+)$");

    /**
     * This pattern is used to parse the said number constant out of a pattern.
     */
    @SuppressWarnings("nls")
    private static final Pattern SAIDNUMBER_PATTERN = Pattern
        .compile("^%NUMBER$");

    /**
     * Parse the value into a advanced number constant.
     * 
     * @param line the line that is to be parsed
     * @return the advanced number that got parsed out or <code>null</code> in
     *         case it does not match any number format
     */
    public static AdvancedNumber getNumber(final String line) {
        final String workingLine = line.trim();

        Matcher matcher = NUMBER_PATTERN.matcher(workingLine);
        if (matcher.matches()) {
            final AdvancedNumber number = AdvancedNumber.getInstance();
            final int value = Integer.parseInt(matcher.group(1));
            number.setNormal(value);
            return number;
        }

        matcher = SAIDNUMBER_PATTERN.matcher(workingLine);
        if (matcher.matches()) {
            final AdvancedNumber number = AdvancedNumber.getInstance();
            number.setSaidNumber();
            return number;
        }

        matcher = EXPRESSION_PATTERN.matcher(workingLine);
        if (matcher.matches()) {
            final AdvancedNumber number = AdvancedNumber.getInstance();
            final String expression = matcher.group(1).trim();
            number.setExpression(expression);
            return number;
        }

        return null;
    }
}
