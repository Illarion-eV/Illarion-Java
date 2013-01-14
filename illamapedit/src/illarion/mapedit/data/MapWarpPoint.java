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
package illarion.mapedit.data;

import javolution.lang.Immutable;
import javolution.text.TextBuilder;

import javax.annotation.Nonnull;

/**
 * Represents a single warp point, with a start point, as map coordinate and a target point as world coordinate.
 *
 * @author Tim
 */
public class MapWarpPoint implements Immutable {

    /**
     * The x coordinate of the target point.
     */
    private final int xTarget;
    /**
     * The y coordinate of the target point.
     */
    private final int yTarget;
    /**
     * The level/z coordinate of the target point.
     */
    private final int zTarget;

    /**
     * Creates a new Warp object with all necessary data.
     *
     * @param xTarget
     * @param yTarget
     * @param zTarget
     */
    public MapWarpPoint(final int xTarget, final int yTarget, final int zTarget) {
        this.xTarget = xTarget;
        this.yTarget = yTarget;
        this.zTarget = zTarget;
    }

    /**
     * Copies the warp object (probably useless)
     *
     * @param old
     */
    public MapWarpPoint(@Nonnull final MapWarpPoint old) {
        xTarget = old.xTarget;
        yTarget = old.yTarget;
        zTarget = old.zTarget;
    }


    /**
     * Returns the x coordinate of the target point.
     *
     * @return
     */
    public int getXTarget() {
        return xTarget;
    }

    /**
     * Returns the y coordinate of the target point.
     *
     * @return
     */
    public int getYTarget() {
        return yTarget;
    }

    /**
     * Returns the z coordinate of the target point.
     *
     * @return
     */
    public int getZTarget() {
        return zTarget;
    }


    /**
     * Serializes the current warp point to a string in the following format: <br>
     * {@code <tx>;<ty>;<tz>}
     *
     * @return
     */
    @Nonnull
    @Override
    public String toString() {
        TextBuilder builder = TextBuilder.newInstance();

        builder.append(xTarget).append(';');
        builder.append(yTarget).append(';');
        builder.append(zTarget);

        try {
            return builder.toString();
        } finally {
            TextBuilder.recycle(builder);
        }
    }
}
