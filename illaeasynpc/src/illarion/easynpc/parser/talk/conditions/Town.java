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
package illarion.easynpc.parser.talk.conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import illarion.easynpc.Lang;
import illarion.easynpc.data.Towns;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.parsed.talk.conditions.ConditionTown;
import illarion.easynpc.parser.talk.ConditionParser;

/**
 * This is a town condition. Its able to parse a town value out of the NPC
 * condition line.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class Town extends ConditionParser {
    /**
     * A empty string used for some replace operations.
     */
    @SuppressWarnings("nls")
    private static final String EMPTY_STRING = "".intern();

    /**
     * This pattern is used to find the town operation in the condition
     * properly.
     */
    @SuppressWarnings("nls")
    private static final Pattern TOWN_FIND = Pattern.compile(
        "\\s*town\\s*=\\s*([A-Za-z]+)\\s*,\\s*", Pattern.CASE_INSENSITIVE);

    /**
     * Extract a condition from the working string.
     */
    @Override
    @SuppressWarnings("nls")
    public TalkCondition extract() {
        if (getNewLine() == null) {
            throw new IllegalStateException("Can't extract if no state set.");
        }

        final Matcher stringMatcher = TOWN_FIND.matcher(getNewLine());
        if (stringMatcher.find()) {
            final String townString = stringMatcher.group(1);

            setLine(stringMatcher.replaceFirst(EMPTY_STRING));

            Towns town = null;
            for (final Towns testTown : Towns.values()) {
                if (testTown.validForRankpoints()
                    && townString.equalsIgnoreCase(testTown.name())) {
                    town = testTown;
                    break;
                }
            }

            if (town == null) {
                reportError(String.format(Lang.getMsg(getClass(), "town"),
                    townString, stringMatcher.group(0)));
                return extract();
            }

            final ConditionTown townCon = ConditionTown.getInstance();
            townCon.setData(town);
            return townCon;
        }

        return null;
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(getClass(), "Docu.description"); //$NON-NLS-1$
    }

    @Override
    public String getExample() {
        return Lang.getMsg(getClass(), "Docu.example"); //$NON-NLS-1$
    }

    @Override
    public String getSyntax() {
        return Lang.getMsg(getClass(), "Docu.syntax"); //$NON-NLS-1$
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(getClass(), "Docu.title"); //$NON-NLS-1$
    }
}
