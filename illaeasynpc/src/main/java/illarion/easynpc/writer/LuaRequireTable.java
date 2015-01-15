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
import java.util.HashMap;
import java.util.Map;

/**
 * This small helper class stores and assigns all the require assignments.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class LuaRequireTable {
    @Nonnull
    private final Map<String, String> requireStorage;

    public LuaRequireTable() {
        requireStorage = new HashMap<>();
    }

    public void registerDependency(@Nonnull String dependency) {
        if (!requireStorage.containsKey(dependency)) {
            String storage;
            if (dependency.startsWith("npc.base.")) {
                storage = dependency.substring("npc.base.".length()).replace('.', '_');
            } else {
                storage = dependency.replace('.', '_');
            }
            String usedStorage = storage;
            int cnt = 0;
            while (requireStorage.containsValue(usedStorage)) {
                usedStorage = storage + Integer.toString(cnt++);
            }
            requireStorage.put(dependency, usedStorage);
        }
    }

    public void writeDependencies(@Nonnull Writer writer) throws IOException {
        for (Map.Entry<String, String> entry : requireStorage.entrySet()){
            writer.write("local ");
            writer.write(entry.getValue());
            writer.write(" = require(\"");
            writer.write(entry.getKey());
            writer.write("\")");
            writer.write(LuaWriter.NL);
        }
    }

    @Nonnull
    public String getStorage(@Nonnull String dependency) {
        if (requireStorage.containsKey(dependency)) {
            return requireStorage.get(dependency);
        }
        throw new IllegalArgumentException("Dependency \"" + dependency + "\" was never registered.");
    }
}
