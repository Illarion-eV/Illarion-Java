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
package illarion.compile.impl;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractCompile implements Compile {
    private Path targetDir;

    @Override
    public void setTargetDir(@Nonnull Path directory) {
        targetDir = directory;
    }

    protected Path getTargetDir() {
        return targetDir;
    }

    protected void ensureTargetDir() throws IOException {
        if (Files.isDirectory(targetDir)) {
            return;
        }
        if (Files.exists(targetDir)) {
            throw new IOException("Target directory points to a file: " + targetDir);
        }
        Files.createDirectories(targetDir);
    }
}
