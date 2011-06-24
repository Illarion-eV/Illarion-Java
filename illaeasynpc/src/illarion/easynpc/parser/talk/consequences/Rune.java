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
package illarion.easynpc.parser.talk.consequences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import illarion.easynpc.Lang;
import illarion.easynpc.data.CharacterMagicType;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.parsed.talk.consequences.ConsequenceRune;
import illarion.easynpc.parser.talk.ConsequenceParser;

/**
 * This is the rune consequence. Its able to parse a rune out of the consequence
 * collection string.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class Rune extends ConsequenceParser {
    /**
     * A empty string used for some replace operations.
     */
    @SuppressWarnings("nls")
    private static final String EMPTY_STRING = "".intern();

    /**
     * This pattern is used to find the strings in the condition and to remove
     * them properly.
     */
    @SuppressWarnings("nls")
    private static final Pattern STRING_FIND = Pattern.compile(
        "\\s*rune\\s*\\([\\s\"]*([a-z]+)\\s*[,]\\s*(\\d+)\\s*\\)\\s*,\\s*",
        Pattern.CASE_INSENSITIVE);

    /**
     * Extract a condition from the working string.
     */
    @Override
    @SuppressWarnings("nls")
    public TalkConsequence extract() {
        if (getNewLine() == null) {
            throw new IllegalStateException("Can't extract if no state set.");
        }

        final Matcher stringMatcher = STRING_FIND.matcher(getNewLine());
        if (stringMatcher.find()) {
            final String magicTypeString =
                stringMatcher.group(1).toLowerCase();
            final int targetValue = Integer.parseInt(stringMatcher.group(2));

            setLine(stringMatcher.replaceFirst(EMPTY_STRING));

            CharacterMagicType magicType = null;
            for (final CharacterMagicType magicTypeTest : CharacterMagicType
                .values()) {
                if (magicTypeTest.canByConsequence()
                    && magicTypeString
                        .equals(magicTypeTest.getMagicTypeName())) {
                    magicType = magicTypeTest;
                    break;
                }
            }

            if (magicType == null) {
                reportError(String.format(
                    Lang.getMsg(getClass(), "magictype"), magicTypeString,
                    stringMatcher.group(0)));
                return extract();
            }

            if ((targetValue < 1) && (targetValue > 31)) {
                reportError(String.format(Lang.getMsg(getClass(), "number"),
                    Integer.valueOf(targetValue), stringMatcher.group(0)));
                return extract();
            }

            final ConsequenceRune runeCons = ConsequenceRune.getInstance();
            runeCons.setData(magicType, targetValue);
            return runeCons;
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
