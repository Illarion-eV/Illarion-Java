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
package illarion.easynpc.gui.syntax;

import illarion.easynpc.Parser;
import illarion.easynpc.data.*;
import org.fife.ui.rsyntaxtextarea.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.text.Segment;

/**
 * This is the token generator for the easyNPC language. Don't dare questioning the contents of this script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class EasyNpcTokenMaker extends AbstractTokenMaker {
    @Nonnull
    @Override
    public TokenMap getWordsToHighlight() {
        final TokenMap tokenMap = new TokenMap(true);

        Parser.getInstance().enlistHighlightedWords(tokenMap);
        BooleanFlagValues.enlistHighlightedWords(tokenMap);
        CharacterAttribute.enlistHighlightedWords(tokenMap);
        CharacterDirection.enlistHighlightedWords(tokenMap);
        CharacterLanguage.enlistHighlightedWords(tokenMap);
        CharacterMagicType.enlistHighlightedWords(tokenMap);
        CharacterRace.enlistHighlightedWords(tokenMap);
        CharacterSex.enlistHighlightedWords(tokenMap);
        EquipmentSlots.enlistHighlightedWords(tokenMap);
        ItemPositions.enlistHighlightedWords(tokenMap);
        TalkingMode.enlistHighlightedWords(tokenMap);
        Towns.enlistHighlightedWords(tokenMap);

        return tokenMap;
    }

    protected static final String operators = "!~=-+/*<>";
    private int currentTokenStart;
    private int currentTokenType;

    @Override
    public Token getTokenList(@Nonnull final Segment text, final int initialTokenType, final int startOffset) {
        resetTokenList();

        final char[] array = text.array;
        final int offset = text.offset;
        final int count = text.count;
        final int end = offset + count;

        final int newStartOffset = startOffset - offset;

        currentTokenStart = offset;
        currentTokenType = initialTokenType;

        for (int i = offset; i < end; i++) {
            final char c = array[i];
            switch (currentTokenType) {
                case Token.NULL:
                    currentTokenStart = i;
                    switch (c) {
                        case ' ':
                        case '\t':
                            currentTokenType = Token.WHITESPACE;
                            break;

                        case '"':
                            currentTokenType = Token.ERROR_STRING_DOUBLE;
                            break;

                        // The "separators".
                        case '(':
                        case ')':
                        case '[':
                        case ']':
                            addToken(text, currentTokenStart, i, Token.SEPARATOR, newStartOffset + currentTokenStart);
                            currentTokenType = Token.NULL;
                            break;

                        // The "separators2".
                        case ',':
                            addToken(text, currentTokenStart, i, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                            currentTokenType = Token.NULL;
                            break;

                        case '-':
                            currentTokenType = Token.OPERATOR;
                            break;

                        default:
                            // Just to speed things up a tad, as this will usually be the case (if spaces above failed).
                            if (RSyntaxUtilities.isLetterOrDigit(c)) {
                                currentTokenType = Token.IDENTIFIER;
                                break;
                            }

                            final int indexOf = operators.indexOf(c, 0);
                            if (indexOf > -1) {
                                addToken(text, currentTokenStart, i, Token.OPERATOR, newStartOffset + currentTokenStart);
                                currentTokenType = Token.NULL;
                                break;
                            }
                            currentTokenType = Token.IDENTIFIER;
                            break;

                    }
                    break;

                case Token.OPERATOR:
                    switch (c) {
                        case ' ':
                        case '\t':
                            addToken(text, currentTokenStart, i - 1, Token.OPERATOR, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.WHITESPACE;
                            break;

                        case '"':
                            addToken(text, currentTokenStart, i - 1, Token.OPERATOR, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.ERROR_STRING_DOUBLE;
                            break;

                        // The "separators".
                        case '(':
                        case ')':
                        case '[':
                        case ']':
                            addToken(text, currentTokenStart, i - 1, Token.OPERATOR, newStartOffset + currentTokenStart);
                            addToken(text, i - 1, i, Token.SEPARATOR, newStartOffset + i);
                            currentTokenType = Token.NULL;
                            break;

                        // The "separators2".
                        case ',':
                            addToken(text, currentTokenStart, i - 1, Token.OPERATOR, newStartOffset + currentTokenStart);
                            addToken(text, i - 1, i, Token.IDENTIFIER, newStartOffset + i);
                            currentTokenType = Token.NULL;
                            break;

                        case '-':
                            // Check for REM comments.
                            if (((i - currentTokenStart) == 1) && (array[i - 1] == '-')) {
                                currentTokenType = Token.COMMENT_EOL;
                                break;
                            }
                            break;

                        default:
                            addToken(text, currentTokenStart, i - 1, Token.OPERATOR, newStartOffset + currentTokenStart);
                            currentTokenStart = i;

                            // Just to speed things up a tad, as this will usually be the case (if spaces above failed).
                            if (RSyntaxUtilities.isLetterOrDigit(c)) {
                                currentTokenType = Token.IDENTIFIER;
                                break;
                            }

                            final int indexOf = operators.indexOf(c, 0);
                            if (indexOf > -1) {
                                addToken(text, currentTokenStart, i, Token.OPERATOR, newStartOffset + currentTokenStart);
                                currentTokenType = Token.NULL;
                                break;
                            }
                            currentTokenType = Token.IDENTIFIER;
                            break;

                    }
                    break;

                case Token.WHITESPACE:
                    switch (c) {
                        case ' ':
                        case '\t':
                            break;

                        case '"':
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.ERROR_STRING_DOUBLE;
                            break;

                        // The "separators".
                        case '(':
                        case ')':
                        case '[':
                        case ']':
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            addToken(text, i, i, Token.SEPARATOR, newStartOffset + i);
                            currentTokenType = Token.NULL;
                            break;

                        // The "separators2".
                        case ',':
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            addToken(text, i, i, Token.SEPARATOR, newStartOffset + i);
                            currentTokenType = Token.NULL;
                            break;

                        default:    // Add the whitespace token and start anew.
                            addToken(text, currentTokenStart, i - 1, Token.WHITESPACE, newStartOffset + currentTokenStart);
                            currentTokenStart = i;

                            // Just to speed things up a tad, as this will usually be the case (if spaces above failed).
                            if (RSyntaxUtilities.isLetterOrDigit(c)) {
                                currentTokenType = Token.IDENTIFIER;
                                break;
                            }

                            final int indexOf = operators.indexOf(c, 0);
                            if (indexOf > -1) {
                                addToken(text, currentTokenStart, i, Token.OPERATOR, newStartOffset + currentTokenStart);
                                currentTokenType = Token.NULL;
                                break;
                            }
                            currentTokenType = Token.IDENTIFIER;

                    } // End of switch (c).

                    break;

                default: // Should never happen
                case Token.IDENTIFIER:
                    switch (c) {
                        case '-':
                            // Check for REM comments.
                            if (((i - currentTokenStart) == 1) && (array[i - 1] == '-')) {
                                currentTokenType = Token.COMMENT_EOL;
                                break;
                            }
                        case ' ':
                        case '\t':
                            // Check for REM comments.
                            if (((i - currentTokenStart) == 2) && (array[i - 2] == '-') && (array[i - 1] == '-')) {
                                currentTokenType = Token.COMMENT_EOL;
                                break;
                            }
                            addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.WHITESPACE;
                            break;

                        case '"':
                            addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.ERROR_STRING_DOUBLE;
                            break;

                        case '%':
                            addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                            currentTokenStart = i;
                            currentTokenType = Token.VARIABLE;
                            break;

                        // The "separators".
                        case '(':
                        case ')':
                        case ']':
                        case '[':
                            addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                            addToken(text, i, i, Token.SEPARATOR, newStartOffset + i);
                            currentTokenType = Token.NULL;
                            break;

                        // The "separators2".
                        case ',':
                            addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                            addToken(text, i, i, Token.IDENTIFIER, newStartOffset + i);
                            currentTokenType = Token.NULL;
                            break;

                        default:

                            // Just to speed things up a tad, as this will usually be the case.
                            if (RSyntaxUtilities.isLetterOrDigit(c)) {
                                break;
                            }

                            final int indexOf = operators.indexOf(c);
                            if (indexOf > -1) {
                                addToken(text, currentTokenStart, i - 1, Token.IDENTIFIER, newStartOffset + currentTokenStart);
                                addToken(text, i, i, Token.OPERATOR, newStartOffset + i);
                                currentTokenType = Token.NULL;
                                break;
                            }

                            // Otherwise, fall through and assume we're still okay as an IDENTIFIER...

                    } // End of switch (c).

                    break;

                case Token.COMMENT_EOL:
                    if (c == '[') {
                        if (((i - currentTokenStart) == 3) &&
                                (array[i - 1] == '[') &&
                                (array[i - 2] == '-') &&
                                (array[i - 3] == '-')) {
                            currentTokenType = Token.COMMENT_MULTILINE;
                        }
                    } else {
                        i = end - 1;
                        addToken(text, currentTokenStart, i, Token.COMMENT_EOL, newStartOffset + currentTokenStart);
                        // We need to set token type to null so at the bottom we don't add one more token.
                        currentTokenType = Token.NULL;
                    }
                    break;
                case Token.COMMENT_MULTILINE:
                    if (c == ']') {
                        if (((i - currentTokenStart) > 0) && (array[i - 1] == ']')) {
                            addToken(text, currentTokenStart, i, Token.COMMENT_MULTILINE,
                                     newStartOffset + currentTokenStart);
                            currentTokenType = Token.NULL;
                        }
                    }
                    break;
                case Token.ERROR_STRING_DOUBLE:

                    if ((c == '"') && (array[i - 1] != '\\')) {
                        addToken(text, currentTokenStart, i, Token.LITERAL_STRING_DOUBLE_QUOTE, newStartOffset + currentTokenStart);
                        currentTokenStart = i + 1;
                        currentTokenType = Token.NULL;
                    }
                    // Otherwise, we're still an unclosed string...

                    break;
            } // End of switch (currentTokenType).

        } // End of for (int i=offset; i<end; i++).

        // Deal with the (possibly there) last token.

        if (currentTokenType != Token.NULL) {

            // Check for REM comments.
            if (((end - currentTokenStart) == 2) && (array[end - 2] == '-') && (array[end - 1] == '-')) {
                currentTokenType = Token.COMMENT_EOL;
            }

            addToken(text, currentTokenStart, end - 1, currentTokenType, newStartOffset + currentTokenStart);
        }
        if (currentTokenType != Token.COMMENT_MULTILINE) {
            addNullToken();
        }


        // Return the first token in our linked list.
        return firstToken;
    }

    public void addToken(final Segment segment, final int start, final int end, final int tokenType,
                         final int startOffset) {
        int newTokenType = tokenType;
        if (tokenType == Token.IDENTIFIER) {
            final int value = wordsToHighlight.get(segment, start, end);
            if (value != -1) {
                newTokenType = value;
            }

        }

        super.addToken(segment, start, end, newTokenType, startOffset);
    }

    /**
     * Returns the text to place at the beginning and end of a line to "comment" it in a this programming language.
     *
     * @return The start and end strings to add to a line to "comment" it out.
     */
    @Nullable
    @Override
    public String[] getLineCommentStartAndEnd() {
        return new String[]{"--", null};
    }

    /**
     * Returns whether tokens of the specified type should have "mark occurrences" enabled for the current
     * programming language.
     *
     * @param type The token type.
     * @return Whether tokens of this type should have "mark occurrences" enabled.
     */
    @Override
    public boolean getMarkOccurrencesOfTokenType(final int type) {
        return (type == Token.IDENTIFIER) || (type == Token.VARIABLE);
    }
}
