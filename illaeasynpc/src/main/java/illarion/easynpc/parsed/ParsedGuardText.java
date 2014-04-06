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
package illarion.easynpc.parsed;

import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;

/**
 * This is the parsed text that is spoken by a NPC guard upon specific events.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ParsedGuardText implements ParsedData {
    /**
     * This enumerator contains the different types of texts that are allowed to be parsed.
     */
    public enum TextType {
        /**
         * This text will be spoken in case the NPC warps away a monster.
         */
        WarpedMonster,

        /**
         * This text will be spoken in case the NPC warps away a player.
         */
        WarpedPlayer,

        /**
         * This text will be spoken in case the NPC attacks a player.
         */
        HitPlayer
    }

    /**
     * The type of this parsed text instance.
     */
    @Nonnull
    private final TextType type;

    /**
     * The german text stored in this text instance.
     */
    @Nonnull
    private final String german;

    /**
     * The english text stored in this text instance.
     */
    @Nonnull
    private final String english;

    /**
     * Initialize a new parsed text instance that contains the text read from the easyNPC script.
     *
     * @param type the type of the parsed text
     * @param german the german text
     * @param english the english text
     */
    public ParsedGuardText(@Nonnull TextType type, @Nonnull String german, @Nonnull String english) {
        this.type = type;
        this.german = german;
        this.english = english;
    }

    @Override
    public void buildSQL(@Nonnull SQLBuilder builder) {
    }

    @Override
    public boolean effectsLuaWritingStage(@Nonnull LuaWriter.WritingStage stage) {
        return stage == LuaWriter.WritingStage.Guarding;
    }

    @Nonnull
    @Override
    public Collection<String> getRequiredModules() {
        return Collections.singleton("npc.base.guard");
    }

    @Override
    public void writeLua(@Nonnull Writer target, @Nonnull LuaWriter.WritingStage stage) throws IOException {
        if (stage != LuaWriter.WritingStage.Guarding) {
            throw new IllegalArgumentException("This function did not request a call for a stage but guarding.");
        }
        target.write("guardNPC:");
        switch (type) {
            case WarpedMonster:
                target.write("addWarpedMonsterText");
                break;
            case WarpedPlayer:
                target.write("addWarpedPlayerText");
                break;
            case HitPlayer:
                target.write("addHitPlayerText");
                break;
            default:
                throw new IllegalStateException("Illegal value for type: " + type);
        }
        target.write("(\"");
        target.write(german);
        target.write("\", \"");
        target.write(english);
        target.write("\");");
        target.write(LuaWriter.NL);
    }
}
