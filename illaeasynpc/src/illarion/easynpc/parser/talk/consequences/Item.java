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

import illarion.easynpc.Lang;
import illarion.easynpc.data.Items;
import illarion.easynpc.parsed.shared.ParsedItemData;
import illarion.easynpc.parsed.talk.AdvancedNumber;
import illarion.easynpc.parsed.talk.TalkConsequence;
import illarion.easynpc.parsed.talk.consequences.ConsequenceItem;
import illarion.easynpc.parser.shared.ItemData;
import illarion.easynpc.parser.talk.AdvNumber;
import illarion.easynpc.parser.talk.ConsequenceParser;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the item consequence. Its able to parse a item condition out of the consequence collection string.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Item extends ConsequenceParser {
    /**
     * This pattern is used to find the strings in the condition and to remove them properly.
     */
    @SuppressWarnings("nls")
    private static final Pattern STRING_FIND = Pattern.compile("\\s*item\\s*\\(\\s*(\\d{1,4})\\s*[,]\\s*"
            + AdvNumber.ADV_NUMBER_REGEXP + "\\s*[,]\\s*(\\d+)\\s*([,]\\s*" + ItemData.REGEXP + "\\s*)?\\)\\s*,\\s*",
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
            final int itemId = Integer.parseInt(stringMatcher.group(1));
            final AdvancedNumber count = AdvNumber.getNumber(stringMatcher.group(2));
            final int quality = Integer.parseInt(stringMatcher.group(3));

            final ParsedItemData data;
            if (stringMatcher.group(4) != null) {
                data = ItemData.getData(stringMatcher.group(5));
            } else {
                data = ItemData.getData("");
            }

            setLine(stringMatcher.replaceFirst(""));

            if (count == null) {
                reportError(String.format(Lang.getMsg(getClass(), "number"), stringMatcher.group(2),
                        stringMatcher.group(0)));
                return extract();
            }

            Items item = null;
            for (final Items it : Items.values()) {
                if (it.getItemId() == itemId) {
                    item = it;
                    break;
                }
            }

            if (item == null) {
                reportError(String.format(Lang.getMsg(getClass(), "item"),
                        stringMatcher.group(1), stringMatcher.group(0)));
                return extract();
            }

            return new ConsequenceItem(item, count, quality, data);
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
    public void enlistHighlightedWords(final TokenMap map) {
        map.put("item", Token.RESERVED_WORD);
    }
}
