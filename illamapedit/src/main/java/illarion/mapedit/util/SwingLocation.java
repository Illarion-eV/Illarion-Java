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
package illarion.mapedit.util;

import illarion.common.types.DisplayCoordinate;
import illarion.common.types.ServerCoordinate;

/**
 * @author Tim
 */
public final class SwingLocation {

    private SwingLocation() {

    }

    public static int mapCoordinateX(
            int x, int y, int transX, int transY, float zoom) {
        float xr = (x - transX) / zoom;
        float yr = (y - transY) / zoom;
        return DisplayCoordinate.toServerX((int) xr, (int) yr);
    }

    public static int mapCoordinateY(
            int x, int y, int transX, int transY, float zoom) {
        float xr = (x - transX) / zoom;
        float yr = (y - transY) / zoom;
        return DisplayCoordinate.toServerY((int) xr, (int) yr) - 1;
    }

    public static int displayCoordinateX(int x, int y, int z) {
        return ServerCoordinate.toDisplayX(x, y);
    }

    public static int displayCoordinateY(int x, int y, int z) {
        return ServerCoordinate.toDisplayY(x, y, z);
    }
}
