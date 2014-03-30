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

import illarion.easynpc.writer.EasyNpcWriter;
import illarion.easynpc.writer.LuaWriter;
import illarion.easynpc.writer.SQLBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;

/**
 * This parsed data storage is able to store the hair or the beard ID of the NPC.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedHair implements ParsedData {
    /**
     * This enum contains the possible values for the type of the hair.
     */
    public enum HairType {
        /**
         * In case this type is set the ID applies to the beard.
         */
        Beard,

        /**
         * In case this ID is set the ID applied to the main hair.
         */
        Hair
    }

    /**
     * The ID of the hair.
     */
    private final int hairId;

    /**
     * The type of the hair.
     */
    private final ParsedHair.HairType hairType;

    /**
     * The constructor for this parsed hair data.
     *
     * @param type the type of the hair
     * @param id the ID of the hair
     */
    public ParsedHair(final ParsedHair.HairType type, final int id) {
        hairType = type;
        hairId = id;
    }

    /**
     * Add the information about the hair and the beard to the SQL query.
     */
    @Override
    public void buildSQL(@Nonnull final SQLBuilder builder) {
        switch (hairType) {
            case Beard:
                builder.setNpcBeard(hairId);
                break;
            case Hair:
                builder.setNpcHair(hairId);
                break;
        }
    }

    /**
     * Check if the selected stage is effected by the data stored in this. This is only <code>true</code> for the
     * "hair" stage.
     */
    @Override
    public boolean effectsEasyNpcStage(@Nonnull final EasyNpcWriter.WritingStage stage) {
        return stage == EasyNpcWriter.WritingStage.hair;
    }

    /**
     * Hair information are not written into the LUA script. No stage is effected.
     */
    @Override
    public boolean effectsLuaWritingStage(@Nonnull final LuaWriter.WritingStage stage) {
        return false;
    }

    /**
     * No additional modules needed for this.
     */
    @Nullable
    @Override
    public String[] getRequiredModules() {
        return null;
    }

    /**
     * Write the data stored in this line to the easyNPC script.
     */
    @SuppressWarnings("nls")
    @Override
    public void writeEasyNpc(
            @Nonnull final Writer target, @Nonnull final EasyNpcWriter.WritingStage stage) throws IOException {
        if (!effectsEasyNpcStage(stage)) {
            return;
        }

        switch (hairType) {
            case Beard:
                target.write("beardID = ");
                break;
            case Hair:
                target.write("hairID = ");
                break;
        }

        target.write(Integer.toString(hairId));
        target.write(EasyNpcWriter.NL);
    }

    /**
     * The LUA script is not effected by this data. This function does nothing at all in this case.
     */
    @Override
    public void writeLua(
            @Nonnull final Writer target, @Nonnull final LuaWriter.WritingStage stage) throws IOException {
        // nothing
    }
}
