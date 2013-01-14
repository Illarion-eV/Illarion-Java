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
import illarion.easynpc.data.CharacterSex;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.parsed.talk.conditions.ConditionSex;
import illarion.easynpc.parser.talk.ConditionParser;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a sex condition. Its able to parse a sex value out of the NPC condition line.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Sex extends ConditionParser {
    /**
     * This pattern is used to find the state operation in the condition
     * properly.
     */
    @SuppressWarnings("nls")
    private static final Pattern SEX_FIND = Pattern.compile("\\s*(sex\\s*=\\s*)*((male)|(female))\\s*,\\s*",
            Pattern.CASE_INSENSITIVE);

    /**
     * Extract a condition from the working string.
     */
    @Nullable
    @Override
    @SuppressWarnings("nls")
    public TalkCondition extract() {
        if (getNewLine() == null) {
            throw new IllegalStateException("Can't extract if no state set.");
        }

        final Matcher stringMatcher = SEX_FIND.matcher(getNewLine());
        if (stringMatcher.find()) {
            final String genderStr = stringMatcher.group(2).toLowerCase();

            setLine(stringMatcher.replaceFirst(""));

            CharacterSex sex = null;
            for (final CharacterSex gender : CharacterSex.values()) {
                if (gender.name().equals(genderStr)) {
                    sex = gender;
                    break;
                }
            }

            if (sex == null) {
                reportError(String.format(Lang.getMsg(getClass(), "sex"),
                        genderStr, stringMatcher.group(0)));
                return extract();
            }

            return new ConditionSex(sex);
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
        map.put("sex", Token.RESERVED_WORD);
    }
}
