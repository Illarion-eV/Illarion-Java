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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.elements.Element;
import illarion.client.gui.MiniMapGui.Pointer;
import illarion.client.resources.MiscImageFactory;
import illarion.common.types.ServerCoordinate;
import illarion.common.util.FastMath;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.Sprite;
import org.illarion.engine.nifty.IgeRenderImage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the implementation of a mini map pointer that marks the starting location of a quest.
 */
final class MiniMapStartPointer implements IgeRenderImage, Pointer {
    /**
     * The sprite that contains the point, that is displayed on the map.
     */
    @Nonnull
    private final Sprite pointSprite;

    /**
     * The location the arrow is supposed to point to.
     */
    @Nullable
    private ServerCoordinate targetLocation;

    /**
     * The Nifty-GUI element this pointer is assigned to.
     */
    @Nonnull
    private final Element parentElement;

    /**
     * The current delta value of the X coordinates.
     */
    private int currentDeltaX;

    /**
     * The current delta value of the Y coordinates.
     */
    private int currentDeltaY;

    /**
     * This is {@code} true if the quest is available right now.
     */
    private boolean available;

    /**
     * Create a new arrow pointer.
     */
    MiniMapStartPointer(@Nonnull Element parentElement) {
        pointSprite = MiscImageFactory.getInstance().getTemplate(MiscImageFactory.MINI_MAP_EXCLAMATION).getSprite();
        this.parentElement = parentElement;
    }

    @Override
    public int getWidth() {
        return pointSprite.getWidth();
    }

    @Override
    public int getHeight() {
        return pointSprite.getHeight();
    }

    @Override
    public void dispose() {
        // nothing to do
    }

    @Override
    public void renderImage(
            @Nonnull Graphics g,
            int x,
            int y,
            int width,
            int height,
            @Nonnull Color color,
            float imageScale) {
        renderImage(g, x, y, width, height, 0, 0, pointSprite.getWidth(), pointSprite.getHeight(), color, imageScale,
                    pointSprite.getWidth() / 2, pointSprite.getHeight() / 2);
    }

    @Override
    public void renderImage(
            @Nonnull Graphics g,
            int x,
            int y,
            int w,
            int h,
            int srcX,
            int srcY,
            int srcW,
            int srcH,
            @Nonnull Color color,
            float scale,
            int centerX,
            int centerY) {
        int scaledWidth = Math.round(w * scale);
        int scaledHeight = Math.round(h * scale);

        int fixedX = x + Math.round((w - scaledWidth) * (centerX / (float) w));
        int fixedY = y + Math.round((h - scaledHeight) * (centerY / (float) h));

        if (isOnMapArea()) {
            int offsetX = (FastMath.sqrt(FastMath.sqr(currentDeltaX) / 2) * -FastMath.sign(currentDeltaX)) +
                    (FastMath.sqrt(FastMath.sqr(currentDeltaY) / 2) * -FastMath.sign(currentDeltaY));
            int offsetY = (FastMath.sqrt(FastMath.sqr(currentDeltaX) / 2) * FastMath.sign(currentDeltaX)) +
                    (FastMath.sqrt(FastMath.sqr(currentDeltaY) / 2) * -FastMath.sign(currentDeltaY));

            Color renderColor;
            if (available) {
                renderColor = POINTER_COLOR;
            } else {
                renderColor = color;
            }

            g.drawTexture(pointSprite.getFrame(0), fixedX - offsetX, fixedY - offsetY, scaledWidth, scaledHeight, srcX,
                          srcY, srcW, srcH, centerX - fixedX, centerY - fixedY, 0.f, renderColor);
        }
    }

    @Override
    public void setCurrentQuest(boolean currentQuest) {

    }

    /**
     * This functions returns if this pointer is on the mini map right now or outside of it.
     *
     * @return {@code true} in case the pointer is on the map
     */
    private boolean isOnMapArea() {
        return FastMath.sqrt(FastMath.sqr(currentDeltaX) + FastMath.sqr(currentDeltaY)) < 71;
    }

    void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Update the angle of arrow.
     *
     * @param playerLocation the current location of the player
     */
    void update(@Nonnull ServerCoordinate playerLocation) {
        if (targetLocation == null) {
            throw new IllegalStateException("The target location of the pointer is not set. Updating it is illegal.");
        }
        currentDeltaX = targetLocation.getX() - playerLocation.getX();
        currentDeltaY = targetLocation.getY() - playerLocation.getY();
    }

    @Override
    public void setTarget(@Nonnull ServerCoordinate coordinate) {
        targetLocation = coordinate;
    }

    /**
     * Get the parent Nifty-GUI element.
     *
     * @return the parent element
     */
    @Nonnull
    public Element getParentElement() {
        return parentElement;
    }
}
