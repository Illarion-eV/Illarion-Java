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
package illarion.easynpc.gui.syntax;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.text.Segment;

import jsyntaxpane.Lexer;
import jsyntaxpane.Token;

import illarion.easynpc.Parser;
import illarion.easynpc.gui.Config;

/**
 * The lexer that is used to create the syntax highlighting of the editor.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class EasyNpcLexer implements Lexer, Comparator<Token>,
    Serializable {
    /**
     * The serialization UID of this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The variable that says of the syntax highlighting should be used or not.
     */
    private final boolean syntax = Config.getInstance()
        .getUseSyntaxHighlighting();

    @Override
    public int compare(final Token o1, final Token o2) {
        return o1.start - o2.start;
    }

    @Override
    public void parse(final Segment segment, final int offset,
        final List<Token> tokenList) {
        if (syntax) {
            Parser.getInstance().parseSegment(segment, offset, tokenList);

            Collections.sort(tokenList, this);
        }
    }

}
