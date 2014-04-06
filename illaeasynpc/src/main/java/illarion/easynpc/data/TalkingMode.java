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
 * This enumerator contains the possible values for the talking modes.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum TalkingMode {
    /**
     * The constant for normal spoken text.
     */
    Talk("say"),

    /**
     * The constant for whispered text.
     */
    Whisper("whisper"),

    /**
     * The constant for shouted text.
     */
    Shout("yell");

    /**
     * The string identifier of this talking mode value used to identify it in the lua script.
     */
    private final String talkMode;

    /**
     * The constructor for the NPC constant that stores the string.
     *
     * @param mode the text that identifies this mode
     */
    TalkingMode(String mode) {
        talkMode = mode;
    }

    /**
     * Get the ID of this talking mode representation.
     *
     * @return the ID of this talking mode representation
     */
    public String getMode() {
        return talkMode;
    }

    /**
     * Add this values to the highlighted tokens.
     *
     * @param map the map that stores the tokens
     */
    public static void enlistHighlightedWords(@Nonnull TokenMap map) {
        map.put("shout", Token.VARIABLE);
        map.put("yell", Token.VARIABLE);
        map.put("whisper", Token.VARIABLE);
        map.put("say", Token.VARIABLE);
        map.put("talk", Token.VARIABLE);
    }
}
