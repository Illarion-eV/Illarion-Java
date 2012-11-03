/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2012 - Illarion e.V.
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
package illarion.easynpc.parser.shared;

import illarion.easynpc.parsed.shared.ParsedItemData;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class can be used to parse item data values.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ItemData {
    /**
     * This pattern should be added at the point of the search regexp to find data patterns at the location.
     */
    public static final String REGEXP = "((\"[^\"]+\"\\s*=\\s*\"[^\"]+\"\\s*,*\\s*)+)";

    /**
     * The compiled pattern used to read the item data values.
     */
    private static final Pattern DATA_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*=\\s*\"([^\"]+)\",*");

    /**
     * Parse the data values from the supplied lines.
     *
     * @param lines the lines to parse
     * @return data parsed data values
     */
    public static ParsedItemData getData(final String... lines) {
        final Map<String, String> dataValues = new HashMap<String, String>();

        for (final String line : lines) {
            final Matcher matcher = DATA_PATTERN.matcher(line);

            while (matcher.find()) {
                dataValues.put(matcher.group(1), matcher.group(2));
            }
        }

        return new ParsedItemData(dataValues);
    }
}
