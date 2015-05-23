/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.mapedit.events.menu;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * @author Fredrik K
 */
public class SetFolderEvent {
    @Nonnull
    private final Path file;

    public SetFolderEvent(@Nonnull Path file) {
        this.file = file;
    }

    @Nonnull
    public Path getFile() {
        return file;
    }
}
