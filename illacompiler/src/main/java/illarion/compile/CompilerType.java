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
package illarion.compile;

import illarion.compile.impl.Compile;
import illarion.compile.impl.EasyNpcCompile;
import illarion.compile.impl.EasyQuestCompile;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * This enumeration contains all the compiler types the compiler knows.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum CompilerType {
    /**
     * Constant for easyNPC files.
     */
    easyNPC(new EasyNpcCompile(), ".npc"),

    /**
     * Constant for easyQuest files.
     */
    easyQuest(new EasyQuestCompile(), ".quest");

    @Nonnull
    private final Compile compiler;
    @Nonnull
    private final String[] extensions;

    CompilerType(@Nonnull Compile compiler, @Nonnull String... extensions) {
        this.compiler = compiler;
        this.extensions = Arrays.copyOf(extensions, extensions.length);
    }

    /**
     * Get the compiler.
     *
     * @return the compiler
     */
    public Compile getCompiler() {
        try {
            return compiler;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if the file is valid for this compiler.
     *
     * @param file the file to check
     * @return {@code true} in case this file is valid for this compiler
     */
    public boolean isValidFile(@Nonnull Path file) {
        if (!Files.isReadable(file)) {
            return false;
        }

        for (String extension : extensions) {
            if (file.toFile().getName().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
