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
package illarion.common.graphics;

import illarion.common.types.DisplayCoordinate;
import org.jetbrains.annotations.Contract;

/**
 * Utility class to store the constants that mark the graphical layers of the different elements. This layer values
 * influence the order of the items they are rendered in. The lower the layer number the sooner the object is
 * rendered. A object with a low layer number will be below a object with a high layer number.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum Layer {
    Chars((DisplayCoordinate.ROW_DISTANCE * 6) + 1),
    Effects((DisplayCoordinate.ROW_DISTANCE * 6) + 2),
    Overlays((DisplayCoordinate.ROW_DISTANCE * 6) + 3),
    Items(DisplayCoordinate.ROW_DISTANCE * 6),
    Tiles(0);

    private final int layerOffset;

    Layer(int offset) {
        layerOffset = offset;
    }

    @Contract(pure = true)
    public int getLayerOffset() {
        return layerOffset;
    }
}
