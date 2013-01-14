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
package illarion.easynpc.parser.talk.consequences;

import illarion.common.data.Skills;
import illarion.easynpc.Lang;
import illarion.easynpc.data.CalculationOperators;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.parsed.talk.consequences.ConsequenceSkill;
import illarion.easynpc.parser.talk.AdvNumber;
import illarion.easynpc.parser.talk.ConsequenceParser;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the skill consequence. Its able to parse a skill out of the consequence collection string.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Skill extends ConsequenceParser {
    /**
     * This pattern is used to find the strings in the condition and to remove them properly.
     */
    @SuppressWarnings("nls")
    private static final Pattern STRING_FIND = Pattern.compile("\\s*skill\\s*\\(\\s*([a-z," +
            "\\s]+)\\s*\\)\\s*([+\\-=]+)\\s*" + AdvNumber.ADV_NUMBER_REGEXP + "\\s*,\\s*", Pattern.CASE_INSENSITIVE);

    /**
     * Extract a condition from the working string.
     */
    @Nullable
    @Override
    @SuppressWarnings("nls")
    public TalkConsequence extract() {
        if (getNewLine() == null) {
            throw new IllegalStateException("Can't extract if no state set.");
        }

        final Matcher stringMatcher = STRING_FIND.matcher(getNewLine());
        if (stringMatcher.find()) {
            final String skillName = stringMatcher.group(1).toLowerCase();
            final String operation = stringMatcher.group(2);
            final AdvancedNumber targetValue = AdvNumber.getNumber(stringMatcher.group(3));

            setLine(stringMatcher.replaceFirst(""));

            illarion.common.data.Skill skill = Skills.getInstance().getSkill(skillName);

            if (skill == null) {
                reportError(String.format(Lang.getMsg(getClass(), "name"), skillName, stringMatcher.group(0)));
                return extract();
            }

            if (targetValue == null) {
                reportError(String.format(Lang.getMsg(getClass(), "number"), stringMatcher.group(3),
                        stringMatcher.group(0)));
                return extract();
            }

            CalculationOperators operator = null;
            for (final CalculationOperators op : CalculationOperators.values()) {
                if (op.getRegexpPattern().matcher(operation).matches()) {
                    operator = op;
                    break;
                }
            }

            if (operator == null) {
                reportError(String.format(Lang.getMsg(getClass(), "operator"), operation, stringMatcher.group(0)));
                return extract();
            }

            return new ConsequenceSkill(skill, operator, targetValue);
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

    @Override
    public void enlistHighlightedWords(@Nonnull final TokenMap map) {
        map.put("skill", Token.RESERVED_WORD);
    }
}
