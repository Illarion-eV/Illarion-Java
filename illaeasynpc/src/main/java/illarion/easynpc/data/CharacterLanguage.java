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
package illarion.easynpc.data;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nonnull;

/**
 * This enumerator contains the possible values for the language the character is speaking.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum CharacterLanguage {
    ancient(10),
    common(0),
    dwarf(2),
    elf(3),
    halfling(6),
    human(1),
    lizard(4),
    orc(5);

    /**
     * The ID of the language.
     */
    private final int id;

    /**
     * Constructor for the constants. It takes the Id of the language to store
     * it in each constant.
     *
     * @param langId the language ID fitting this constant
     */
    CharacterLanguage(int langId) {
        id = langId;
    }

    /**
     * Get the language ID fitting of this constant.
     *
     * @return the language ID
     */
    public int getLangId() {
        return id;
    }

    /**
     * Add this values to the highlighted tokens.
     *
     * @param map the map that stores the tokens
     */
    public static void enlistHighlightedWords(@Nonnull TokenMap map) {
        for (CharacterLanguage language : CharacterLanguage.values()) {
            map.put(language.name(), Token.VARIABLE);
        }
    }
}
