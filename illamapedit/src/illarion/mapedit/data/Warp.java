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
 * @author Tim
 */
public class Warp implements Immutable {


    private final int xStart;
    private final int yStart;
    private final int xTarget;
    private final int yTarget;
    private final int zTarget;
    public Warp(final int xStart, final int yStart, final int xTarget, final int yTarget, final int zTarget) {

        this.xStart = xStart;
        this.yStart = yStart;
        this.xTarget = xTarget;
        this.yTarget = yTarget;
        this.zTarget = zTarget;
    }

    public Warp(final Warp old) {
        xStart = old.xStart;
        yStart = old.yStart;
        xTarget = old.xTarget;
        yTarget = old.yTarget;
        zTarget = old.zTarget;
    }

    public int getXStart() {
        return xStart;
    }

    public int getYStart() {
        return yStart;
    }

    public int getXTarget() {
        return xTarget;
    }

    public int getYTarget() {
        return yTarget;
    }

    public int getZTarget() {
        return zTarget;
    }

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
