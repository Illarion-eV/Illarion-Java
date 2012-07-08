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

/**
 * Represents a single warp point, with a start point, as map coordinate and a target point as world coordinate.
 *
 * @author Tim
 */
public class Warp implements Immutable {

    /**
     * The x coordinate of the start point
     */
    private final int xStart;
    /**
     * The y coordinate of the start point.
     */
    private final int yStart;
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
     * @param xStart
     * @param yStart
     * @param xTarget
     * @param yTarget
     * @param zTarget
     */
    public Warp(final int xStart, final int yStart, final int xTarget, final int yTarget, final int zTarget) {

        this.xStart = xStart;
        this.yStart = yStart;
        this.xTarget = xTarget;
        this.yTarget = yTarget;
        this.zTarget = zTarget;
    }

    /**
     * Copies the warp object (probably useless)
     *
     * @param old
     */
    public Warp(final Warp old) {
        xStart = old.xStart;
        yStart = old.yStart;
        xTarget = old.xTarget;
        yTarget = old.yTarget;
        zTarget = old.zTarget;
    }

    /**
     * Returns the x coordinate of the start point.
     *
     * @return
     */
    public int getXStart() {
        return xStart;
    }

    /**
     * Returns the y coordinate of the start point.
     *
     * @return
     */
    public int getYStart() {
        return yStart;
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
     * Loads a warppoint from one line of a *.warps.txt file with the following format: <br/>
     * {@code [StartX];[StartY];[TargetX];[TargetY];[TargetZ]}
     *
     * @param line the string with the data
     * @return the new warp point
     */
    public static Warp fromString(final String line) {
        final String[] sections = line.split(";");
        if (sections.length != 5) {
            throw new IllegalArgumentException("Item can only hava 5 sections: " + line);
        }
        return new Warp(
                Integer.parseInt(sections[0]),
                Integer.parseInt(sections[1]),
                Integer.parseInt(sections[2]),
                Integer.parseInt(sections[3]),
                Integer.parseInt(sections[4])
        );
    }
}
