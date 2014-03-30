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
package illarion.mapedit.events.menu;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * @author Tim
 */
public class MapOpenEvent {

    @Nonnull
    private final Path path;
    private final String name;

    public MapOpenEvent(@Nonnull final Path path, final String name) {
        this.path = path;
        this.name = name;
    }

    @Nonnull
    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
