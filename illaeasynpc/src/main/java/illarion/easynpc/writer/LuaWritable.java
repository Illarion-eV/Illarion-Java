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
package illarion.easynpc.writer;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

/**
 * This implements needs to be implemented in all objects that are able to
 * supply data to the {@link illarion.easynpc.writer.LuaWriter}. This interface
 * is used to fetch the data that needs to be written into the script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface LuaWritable {
    /**
     * Build the SQL query.
     *
     * @param builder the builder that is used to construct the SQL query
     */
    void buildSQL(@Nonnull SQLBuilder builder);

    /**
     * Check if this LUA writable has any effect on a selected stage.
     *
     * @param stage the selected stage
     * @return <code>true<code> in case this LUA writable effects the stage
     */
    boolean effectsLuaWritingStage(@Nonnull LuaWriter.WritingStage stage);

    /**
     * Get the list of modules required to have the code written by this
     * LuaWritable to work.
     *
     * @return the list of required LUA modules
     */
    @Nonnull
    Collection<String> getRequiredModules();

    /**
     * Write the LUA Code fitting into the currently selected writing stage.
     *
     * @param target the writer that is supposed to receive the script data
     * @param requires the storage of registered requires.
     * @param stage the stage that is currently written
     * @throws IOException thrown in case a writing error occurs
     */
    void writeLua(@Nonnull Writer target, @Nonnull LuaRequireTable requires, @Nonnull LuaWriter.WritingStage stage)
            throws IOException;
}
