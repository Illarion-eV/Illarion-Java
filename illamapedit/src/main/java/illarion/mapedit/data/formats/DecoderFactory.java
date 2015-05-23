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
package illarion.mapedit.data.formats;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * The decoder factory is able to provide the decoder implementations according to a version value.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DecoderFactory {
    @Nonnull
    public Decoder getDecoder(int version, @Nonnull String mapName, @Nonnull Path mapPath) {
        switch (version) {
            case 2:
                return new Version2Decoder(mapName, mapPath);
            default:
                throw new IllegalArgumentException("Illegal version: " + version);
        }
    }
}
