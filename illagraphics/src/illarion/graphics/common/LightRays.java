/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.common;

import illarion.common.util.Bresenham;

/**
 * This class stores the root node of a set of light rays for a given size. It
 * precalculates all rays when its created and stores them for later usage.
 * 
 * @author Nop
 * @author Martin Karing
 * @version 1.21
 * @since 2.00
 */
public final class LightRays {
    /**
     * The root node. This node is placed exactly on the light source with the
     * size created in this class. From this ray node all other precalculated
     * nodes are accessible.
     */
    private final RayNode root;

    /**
     * The length of the rays that were precalculated with this instance of
     * LightRays.
     */
    private final int size;

    /**
     * Constructor, triggers the precalculation of all light rays up to the size
     * set as parameter in this constructor call.
     * 
     * @param targetSize the length of the light rays
     */
    public LightRays(final int targetSize) {
        size = targetSize;
        root = new RayNode(targetSize);

        for (int i = -targetSize; i < targetSize; ++i) {
            createRay(i, -targetSize);
            createRay(i + 1, targetSize);
            createRay(targetSize, i);
            createRay(-targetSize, i + 1);
        }
    }

    /**
     * Apply a light source to the root node. This causes that the root node is
     * set to the location of the lightsource and the rays are used for this
     * light source. With knowing the real location there are the checks down
     * with the {@link illarion.graphics.common.LightingMap} of the lightray
     * gets over the tiles correctly and with the results the light rays are
     * modified to the shadow by the objects on the map applys correctly.
     * 
     * @param light the lightsource that shall be mapped with the pre
     */
    public void apply(final LightSource light) {
        root.apply(light, 1.0f);
    }

    /**
     * Precalculate a light ray and attach it to the root node. The center of
     * the ray, so the ray node is assumed to be at 0, 0.
     * 
     * @param x the x coordinate of the target location of the ray
     * @param y the y coordinate of the target location of the ray
     */
    private void createRay(final int x, final int y) {
        final Bresenham bres = Bresenham.getInstance();

        bres.calculate(0, 0, x, y);
        bres.adjustStart(0, 0);

        root.addRay(bres.getX(), bres.getY(), bres.getLength(), 1, size);
    }
}
