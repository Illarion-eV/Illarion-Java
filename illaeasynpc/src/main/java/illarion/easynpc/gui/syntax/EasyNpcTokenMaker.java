/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.easynpc.gui.syntax;

import illarion.easynpc.Parser;
import illarion.easynpc.data.*;
import illarion.easynpc.grammar.EasyNpcLexer;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.TokenTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the token generator for the easyNPC language. Don't dare questioning the contents of this script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class EasyNpcTokenMaker extends AbstractAntlrTokenMaker<EasyNpcLexer> {
    /**
     * Create a new instance of this token maker.
     */
    public EasyNpcTokenMaker() {
        super(new EasyNpcLexer(null));
    }

    @Nonnull
    @Override
    public TokenMap getWordsToHighlight() {
        TokenMap tokenMap = new TokenMap(true);

        Parser.enlistHighlightedWords(tokenMap);
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
    public boolean getMarkOccurrencesOfTokenType(int type) {
        return (type == Token.IDENTIFIER) || (type == Token.VARIABLE);
    }

    @Override
    protected int convertTokenType(int antlrType) {
        switch (antlrType) {
            case EasyNpcLexer.WS:
                return TokenTypes.WHITESPACE;
            case EasyNpcLexer.BOOLEAN:
                return TokenTypes.LITERAL_BOOLEAN;
            case EasyNpcLexer.INT:
                return TokenTypes.LITERAL_NUMBER_DECIMAL_INT;
            case EasyNpcLexer.FLOAT:
                return TokenTypes.LITERAL_NUMBER_FLOAT;
            case EasyNpcLexer.STRING:
                return TokenTypes.LITERAL_STRING_DOUBLE_QUOTE;
            case EasyNpcLexer.COMMENT:
                return TokenTypes.COMMENT_MULTILINE;
            case EasyNpcLexer.LINE_COMMENT:
                return TokenTypes.COMMENT_EOL;
            case EasyNpcLexer.NAME:
                return TokenTypes.IDENTIFIER;
            case EasyNpcLexer.ADD:
            case EasyNpcLexer.SUB:
            case EasyNpcLexer.MUL:
            case EasyNpcLexer.DIV:
            case EasyNpcLexer.POW:
            case EasyNpcLexer.MOD:
            case EasyNpcLexer.LT:
            case EasyNpcLexer.GT:
            case EasyNpcLexer.EQ:
            case EasyNpcLexer.NEQ:
            case EasyNpcLexer.LET:
            case EasyNpcLexer.GET:
                return TokenTypes.OPERATOR;
            default:
                return TokenTypes.RESERVED_WORD;
        }
    }
}
