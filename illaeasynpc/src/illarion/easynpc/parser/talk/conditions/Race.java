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
import illarion.easynpc.data.CharacterRace;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.parsed.talk.conditions.ConditionRace;
import illarion.easynpc.parser.talk.ConditionParser;

/**
 * This is a race condition. Its able to parse a race value out of the NPC
 * condition line.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public final class Race extends ConditionParser {
    /**
     * A empty string used for some replace operations.
     */
    @SuppressWarnings("nls")
    private static final String EMPTY_STRING = "".intern();

    /**
     * This pattern is used to find the state operation in the condition
     * properly.
     */
    @SuppressWarnings("nls")
    private static final Pattern RACE_FIND = Pattern.compile(
        "\\s*race\\s*=\\s*([a-z]+)\\s*,\\s*", Pattern.CASE_INSENSITIVE);

    /**
     * Extract a condition from the working string.
     */
    @Override
    @SuppressWarnings("nls")
    public TalkCondition extract() {
        if (getNewLine() == null) {
            throw new IllegalStateException("Can't extract if no state set.");
        }

        final Matcher stringMatcher = RACE_FIND.matcher(getNewLine());
        if (stringMatcher.find()) {
            final String raceStr = stringMatcher.group(1).toLowerCase();

            setLine(stringMatcher.replaceFirst(EMPTY_STRING));

            CharacterRace race = null;
            for (final CharacterRace chrRace : CharacterRace.values()) {
                if (chrRace.name().equals(raceStr)) {
                    race = chrRace;
                    break;
                }
            }

            if (race == null) {
                reportError(String.format(Lang.getMsg(getClass(), "race"),
                    raceStr, stringMatcher.group(0)));
                return extract();
            }

            final ConditionRace raceCon = ConditionRace.getInstance();
            raceCon.setData(race);
            return raceCon;
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
