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
import illarion.easynpc.data.CharacterAttribute;
import illarion.easynpc.data.CompareOperators;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.parsed.talk.conditions.ConditionAttrib;
import illarion.easynpc.parser.talk.AdvNumber;
import illarion.easynpc.parser.talk.ConditionParser;

/**
 * This is a attribute condition. Its able to parse a attribute value out of the
 * NPC condition line.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class Attribute extends ConditionParser {
    /**
     * This pattern is used to find the attribute operation in the condition
     * properly.
     */
    @SuppressWarnings("nls")
    private static final Pattern ATTRIB_FIND = Pattern.compile(
        "\\s*attrib\\(\\s*([a-z]+)\\s*\\)\\s*([=~!<>]{1,2})\\s*"
            + AdvNumber.ADV_NUMBER_REGEXP + "\\s*,\\s*",
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

        final Matcher stringMatcher = ATTRIB_FIND.matcher(getNewLine());
        if (stringMatcher.find()) {
            final String attribName =
                stringMatcher.group(1).trim().toLowerCase();
            final String comperator = stringMatcher.group(2);
            final AdvancedNumber targetValue =
                AdvNumber.getNumber(stringMatcher.group(3));

            setLine(stringMatcher.replaceFirst(EMPTY_STRING));

            if (targetValue == null) {
                reportError(String.format(Lang.getMsg(getClass(), "number"),
                    stringMatcher.group(3), stringMatcher.group(0)));
                return extract();
            }

            CharacterAttribute attrib = null;
            for (final CharacterAttribute attribute : CharacterAttribute
                .values()) {
                if (attribute.name().equalsIgnoreCase(attribName)) {
                    attrib = attribute;
                    break;
                }
            }

            if (attrib == null) {
                reportError(String.format(
                    Lang.getMsg(getClass(), "attribute"), attribName,
                    stringMatcher.group(0)));
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

            final ConditionAttrib attribCon = ConditionAttrib.getInstance();
            attribCon.setData(attrib, operator, targetValue);
            return attribCon;
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
