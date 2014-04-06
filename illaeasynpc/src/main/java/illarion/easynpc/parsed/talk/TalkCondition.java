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
package illarion.easynpc.parsed.talk;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;

/**
 * This interface is the common talking condition interface used to store the conditions of a talking line.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface TalkCondition {
    /**
     * The base module of all condition.
     */
    String BASE_LUA_MODULE = "npc.base.condition.";

    /**
     * Get the LUA module needed for this condition.
     *
     * @return the LUA module needed for this condition
     */
    @Nullable
    String getLuaModule();

    /**
     * Write the data of this talking condition to a LUA script.
     *
     * @param target the writer that takes the data
     * @throws IOException thrown in case the writing operations fail
     */
    void writeLua(Writer target) throws IOException;
}
