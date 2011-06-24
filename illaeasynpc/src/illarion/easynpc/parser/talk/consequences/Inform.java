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
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.parsed.talk.consequences.ConsequenceInform;
import illarion.easynpc.parser.talk.ConsequenceParser;

/**
 * This is the inform consequence. Its able to parse a inform out of the
 * consequence collection string.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class Inform extends ConsequenceParser {
    /**
     * A empty string used for some replace operations.
     */
    @SuppressWarnings("nls")
    private static final String EMPTY_STRING = "".intern();

    /**
     * This pattern is used to find the strings in the condition and to remove
     * them properly. (new syntax)
     */
    @SuppressWarnings("nls")
    private static final Pattern STRING_FIND_NEW = Pattern.compile(
        "\\s*inform\\s*\\(\\s*\"([^\"]+)\"\\s*\\)\\s*,\\s*",
        Pattern.CASE_INSENSITIVE);

    /**
     * This pattern is used to find the strings in the condition and to remove
     * them properly. (old syntax)
     */
    @SuppressWarnings("nls")
    private static final Pattern STRING_FIND_OLD = Pattern.compile(
        "\\s*inform\\s*\\(\\s*([^\\)]+)\\s*\\)\\s*[,]*\\s*",
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

        Matcher stringMatcher =
            STRING_FIND_NEW.matcher(getNewLine().replace("\\\"", "''"));
        if (stringMatcher.find()) {
            final ConsequenceInform informCon =
                ConsequenceInform.getInstance();
            final String trigger =
                stringMatcher.group(1).replace("''", "\\\"");
            informCon.setData(trigger);
            setLine(stringMatcher.replaceFirst(EMPTY_STRING).replace("''",
                "\\\""));
            return informCon;
        }

        stringMatcher = STRING_FIND_OLD.matcher(getNewLine());
        if (stringMatcher.find()) {
            final ConsequenceInform informCon =
                ConsequenceInform.getInstance();
            final String trigger =
                stringMatcher.group(1).replace("''", "\\\"");
            informCon.setData(trigger);
            setLine(stringMatcher.replaceFirst(EMPTY_STRING).replace("''",
                "\\\""));
            return informCon;
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
