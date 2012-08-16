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
package illarion.easynpc.parser.talk.conditions;

import illarion.easynpc.Lang;
import illarion.easynpc.data.CharacterSkill;
import illarion.easynpc.data.CompareOperators;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.parsed.talk.conditions.ConditionSkill;
import illarion.easynpc.parser.talk.AdvNumber;
import illarion.easynpc.parser.talk.ConditionParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a skill condition. Its able to parse a skill value out of the NPC condition line.
 *
 * @author Martin Karing
 */
public final class Skill extends ConditionParser {
    /**
     * This pattern is used to find the skill operation in the condition
     * properly.
     */
    @SuppressWarnings("nls")
    private static final Pattern SKILL_FIND = Pattern.compile("\\s*skill\\(([a-z,\\s]+)\\)\\s*([=~!<>]{1,2})\\s*"
            + AdvNumber.ADV_NUMBER_REGEXP + "\\s*,\\s*", Pattern.CASE_INSENSITIVE);

    /**
     * Extract a condition from the working string.
     */
    @Override
    @SuppressWarnings("nls")
    public TalkCondition extract() {
        if (getNewLine() == null) {
            throw new IllegalStateException("Can't extract if no string set.");
        }

        final Matcher stringMatcher = SKILL_FIND.matcher(getNewLine());
        if (stringMatcher.find()) {
            final String skillName =
                    stringMatcher.group(1).trim().toLowerCase();
            final String comperator = stringMatcher.group(2);
            final AdvancedNumber targetValue =
                    AdvNumber.getNumber(stringMatcher.group(3));

            setLine(stringMatcher.replaceFirst(""));

            if (targetValue == null) {
                reportError(String.format(Lang.getMsg(getClass(), "number"),
                        stringMatcher.group(3), stringMatcher.group(0)));
                return extract();
            }

            CharacterSkill skill = null;
            for (final CharacterSkill sk : CharacterSkill.values()) {
                if (skillName.contains(sk.getSkillName())) {
                    skill = sk;
                    break;
                }
            }

            if (skill == null) {
                reportError(String.format(Lang.getMsg(getClass(), "skill"),
                        skillName, stringMatcher.group(0)));
                return extract();
            }

            CompareOperators operator = null;
            for (final CompareOperators op : CompareOperators.values()) {
                if (op.getRegexpPattern().matcher(comperator).matches()) {
                    operator = op;
                    break;
                }
            }

            if (operator == null) {
                reportError(String.format(Lang.getMsg(getClass(), "operator"),
                        comperator, stringMatcher.group(0)));
                return extract();
            }

            return new ConditionSkill(skill, operator, targetValue);
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
