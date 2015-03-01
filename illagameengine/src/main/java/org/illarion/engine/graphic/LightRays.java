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
package org.illarion.engine.graphic;

import illarion.common.util.Bresenham;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class stores a set of light rays that originate from a root location.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class LightRays {
    /**
     * The root node.
     */
    @Nonnull
    private final RayNode root;

    /**
     * The length of the rays
     */
    private final int size;

    /**
     * The caches for the rays.
     */
    @Nonnull
    private static final Map<Integer, LightRays> RAY_CACHE = new HashMap<>();

    @Nonnull
    public static LightRays getRays(int size) {
        LightRays rays = RAY_CACHE.get(size);
        if (rays == null) {
            synchronized (RAY_CACHE) {
                rays = RAY_CACHE.get(size);
                if (rays == null) {
                    rays = new LightRays(size);
                    RAY_CACHE.put(size, rays);
                }
            }
        }
        return rays;
    }

    /**
     * Create a new say of rays.
     *
     * @param targetSize the length of the light rays
     */
    public LightRays(int targetSize) {
        size = targetSize;
        root = new RayNode(targetSize);

        Bresenham bresenham = new Bresenham();

        for (int i = -targetSize; i < targetSize; ++i) {
            createRay(i, -targetSize, bresenham);
            createRay(i + 1, targetSize, bresenham);
            createRay(targetSize, i, bresenham);
            createRay(-targetSize, i + 1, bresenham);
        }
    }

    /**
     * Apply a light source to the root node. This causes that the root node is set to the location of the
     * light source and the rays are used for this light source. With knowing the real location there are the checks
     * down with the {@link LightingMap} of the light ray gets over the tiles correctly and with the results the
     * light rays are  modified to the shadow by the objects on the map applies correctly.
     *
     * @param light the source of the light that shall be mapped with the pre
     */
    public void apply(@Nonnull LightSource light) {
        root.apply(light, 1.0f);
    }

    /**
     * Prepare a single light ray and add it to the root node
     *
     * @param x the x coordinate of the target location of the ray
     * @param y the y coordinate of the target location of the ray
     */
    private void createRay(int x, int y, @Nonnull Bresenham bresenham) {
        bresenham.calculate(0, 0, x, y);
        bresenham.adjustStart(0, 0);

        root.addRay(bresenham.getX(), bresenham.getY(), bresenham.getLength(), 1, size);
    }
}
