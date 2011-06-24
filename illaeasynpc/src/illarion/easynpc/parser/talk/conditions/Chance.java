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
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.parsed.talk.conditions.ConditionChance;
import illarion.easynpc.parser.talk.ConditionParser;

/**
 * This is a chance condition. Its able to parse a chance value out of the NPC
 * condition line.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class Chance extends ConditionParser {
    /**
     * This pattern is used to find the chance operation in the condition
     * properly.
     */
    @SuppressWarnings("nls")
    private static final Pattern CHANCE_FIND = Pattern.compile(
        "\\s*chance\\s*\\(\\s*([0-9]{1,3})\\)\\s*,\\s*",
        Pattern.CASE_INSENSITIVE);
    /**
     * A empty string used for some replace operations.
     */
    @SuppressWarnings("nls")
    private static final String EMPTY_STRING = "".intern();

    /**
     * Extract a condition from the working string.
     */
    @Override
    @SuppressWarnings("nls")
    public TalkCondition extract() {
        if (getNewLine() == null) {
            throw new IllegalStateException("Can't extract if no string set.");
        }

        final Matcher stringMatcher = CHANCE_FIND.matcher(getNewLine());
        if (stringMatcher.find()) {
            int targetValue;
            try {
                targetValue = Integer.parseInt(stringMatcher.group(1));
            } catch (final NumberFormatException ex) {
                reportError(String.format(Lang.getMsg(getClass(), "number"),
                    stringMatcher.group(1), stringMatcher.group(0)));
                return extract();
            }

            setLine(stringMatcher.replaceFirst(EMPTY_STRING));

            final ConditionChance chanceCon = ConditionChance.getInstance();
            chanceCon.setData(targetValue);
            return chanceCon;
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
