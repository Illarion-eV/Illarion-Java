/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.util;

import illarion.common.types.Location;

/**
 * @author Tim
 */
public class SwingLocation {

    private static final Location DUMMY = new Location();

    private SwingLocation() {

    }

    public static int mapCoordinateX(final int x, final int y, final int transX, final int transY,
                                     final float zoom) {
        final float xr = (x - transX) / zoom;
        final float yr = (y - transY) / zoom;
        DUMMY.reset();
        DUMMY.setDC((int) xr, (int) yr);
        return DUMMY.getScX();
    }

    public static int mapCoordinateY(final int x, final int y, final int transX, final int transY,
                                     final float zoom) {
        final float xr = (x - transX) / zoom;
        final float yr = (y - transY) / zoom;
        DUMMY.reset();
        DUMMY.setDC((int) xr, (int) yr);
        return DUMMY.getScY() - 1;
    }

    public static int displayCoordinateX(final int x, final int y, final int z) {
        return Location.displayCoordinateX(x, y, z);
    }

    public static int displayCoordinateY(final int x, final int y, final int z) {
        return -Location.displayCoordinateY(x, y, z);
    }
}
