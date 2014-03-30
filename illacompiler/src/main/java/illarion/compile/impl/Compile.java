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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Compile {
    void setTargetDir(@Nonnull Path directory);

    int compileFile(@Nonnull Path file);

    int compileStream(@Nonnull InputStream in, @Nonnull OutputStream out);
}
