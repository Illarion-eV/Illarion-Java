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
package illarion.client.graphics;

import illarion.common.types.Rectangle;

import javax.annotation.Nonnull;

/**
 * This helper class is used to hide out all parts of the game map that are not needed to be viewed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Camera {
    /**
     * The singleton instance of this class.
     */
    private static final Camera INSTANCE = new Camera();

    /**
     * This rectangle stores the viewport of the camera. Anything outside of this viewport does not need to be drawn.
     */
    @Nonnull
    private final Rectangle viewport;

    /**
     * Private constructor to avoid any instances being created but the singleton instance.
     */
    private Camera() {
        viewport = new Rectangle();
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static Camera getInstance() {
        return INSTANCE;
    }

    /**
     * Get the height of the viewport.
     *
     * @return the height of the viewport
     */
    public int getViewportHeight() {
        return viewport.getHeight();
    }

    /**
     * Get the x offset applied to the viewport.
     *
     * @return the x offset
     */
    public int getViewportOffsetX() {
        return viewport.getX();
    }

    /**
     * Get the y offset applied to the viewport.
     *
     * @return the y offset
     */
    public int getViewportOffsetY() {
        return viewport.getY();
    }

    /**
     * Get the width of the viewport.
     *
     * @return the width of the viewport
     */
    public int getViewportWidth() {
        return viewport.getWidth();
    }

    /**
     * Check if a area is at least partly within a area that needs to be updated at this render run.
     *
     * @param testLocX the x location of the area that needs to be checked for intersection with the clipping area
     * @param testLocY the y location of the area that needs to be checked for intersection with the clipping area
     * @param width the width of the area that needs to be checked
     * @param height the height of the area that needs to be checked
     * @return {@code true} in case there is no clipping area set or the tested area is at least partly within the
     * clipping area, if its fully outside {@code false} is returned
     */
    public boolean requiresUpdate(final int testLocX, final int testLocY, final int width, final int height) {
        final Rectangle testRect = new Rectangle();
        testRect.set(testLocX, testLocY, width, height);

        return requiresUpdate(testRect);
    }

    /**
     * Check if a area is at least partly within a area that needs to be updated at this render run.
     *
     * @param rect the rectangle to test
     * @return {@code true} in case there is no clipping area set or the tested area is at least partly within the
     * clipping area, if its fully outside {@code false} is returned
     */
    public boolean requiresUpdate(@Nonnull final Rectangle rect) {
        return !(!viewport.intersects(rect) || rect.isEmpty());
    }

    /**
     * Set the viewport of the camera. That is needed to check if objects are are inside the camera view or not.
     *
     * @param x the x coordinate of the origin of the viewport
     * @param y the y coordinate of the origin of the viewport
     * @param width the width of the viewport
     * @param height the height of the viewport
     */
    public void setViewport(final int x, final int y, final int width, final int height) {
        viewport.set(x, y, width, height);
    }

    @Nonnull
    public Rectangle getViewport() {
        return viewport;
    }
}
