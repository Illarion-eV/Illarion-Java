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

import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

/**
 * This is the token maker factory that provides the access to the easyNPC tokens.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class EasyNpcTokenMakerFactory extends TokenMakerFactory {
    /**
     * The name of the syntax style for the easyNPC syntax highlighting.
     */
    @Nonnull
    public static final String SYNTAX_STYLE_EASY_NPC = "EasyNPC";

    @Nullable
    @Override
    protected TokenMaker getTokenMakerImpl(String key) {
        if (SYNTAX_STYLE_EASY_NPC.equals(key)) {
            return new EasyNpcTokenMaker();
        }
        return null;
    }

    @Nonnull
    @Override
    public Set<String> keySet() {
        return Collections.singleton(SYNTAX_STYLE_EASY_NPC);
    }
}
