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
import illarion.client.graphics.AnimationUtility;
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
 * This is the implementation of the pointers. This class is the image that is displayed on the GUI in order to
 * show the arrow on the mini map. It takes care for rendering it with the proper rotation applied.
 */
final class MiniMapArrowPointer implements IgeRenderImage, Pointer {
    /**
     * The sprite that contains the arrow. This is displayed in case the point is outside the area of the mini map.
     */
    @Nonnull
    private final Sprite arrowSprite;

    /**
     * The sprite that contains the point. This is displayed in case the target location is on the area of the
     * mini map.
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
     * The current angle of the arrow in (1/10)°
     */
    private int currentAngle;

    /**
     * The target angle of the arrow in (1/10)°
     */
    private int targetAngle;

    /**
     * The current delta value of the X coordinates.
     */
    private int currentDeltaX;

    /**
     * The current delta value of the Y coordinates.
     */
    private int currentDeltaY;

    /**
     * Change color depending on value
     */
    private boolean isCurrentQuest;

    /**
     * Create a new arrow pointer.
     */
    MiniMapArrowPointer(@Nonnull Element parentElement) {
        arrowSprite = MiscImageFactory.getInstance().getTemplate(MiscImageFactory.MINI_MAP_ARROW).getSprite();
        pointSprite = MiscImageFactory.getInstance().getTemplate(MiscImageFactory.MINI_MAP_POINT).getSprite();
        this.parentElement = parentElement;
    }

    @Override
    public void setCurrentQuest(boolean currentQuest) {
        isCurrentQuest = currentQuest;
    }

    @Override
    public int getWidth() {
        return arrowSprite.getWidth();
    }

    @Override
    public int getHeight() {
        return arrowSprite.getHeight();
    }

    @Override
    public void dispose() {
        // nothing to do
    }

    private float getAngle() {
        double angle = Math.toDegrees(Math.atan2(currentDeltaY, currentDeltaX));

        return (float) angle + 45.f;
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
        renderImage(g, x, y, width, height, 0, 0, arrowSprite.getWidth(), arrowSprite.getHeight(), color, imageScale,
                    arrowSprite.getWidth() / 2, arrowSprite.getHeight() / 2);
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

        int fixedX = x + Math.round((w - scaledWidth) * ((float) centerX / (float) w));
        int fixedY = y + Math.round((h - scaledHeight) * ((float) centerY / (float) h));

        Color pointerColor = POINTER_COLOR;
        if (isCurrentQuest) {
            pointerColor = ACTIVEPOINTER_COLOR;
        }

        if (isOnMapArea()) {
            int offsetX = (FastMath.sqrt(FastMath.sqr(currentDeltaX) / 2) * -FastMath.sign(currentDeltaX)) +
                    (FastMath.sqrt(FastMath.sqr(currentDeltaY) / 2) * -FastMath.sign(currentDeltaY));
            int offsetY = (FastMath.sqrt(FastMath.sqr(currentDeltaX) / 2) * FastMath.sign(currentDeltaX)) +
                    (FastMath.sqrt(FastMath.sqr(currentDeltaY) / 2) * -FastMath.sign(currentDeltaY));

            g.drawTexture(pointSprite.getFrame(0), fixedX - offsetX, fixedY - offsetY, scaledWidth, scaledHeight, srcX,
                          srcY, srcW, srcH, centerX - fixedX, centerY - fixedY, 0.f, pointerColor);
        } else {
            float angle = (float) currentAngle / 10.f;

            int spriteOffsetX = arrowSprite.getOffsetX();
            int spriteOffsetY = arrowSprite.getOffsetY();

            float radianAngle = FastMath.toRadians(angle);
            float sinAngle = FastMath.sin(radianAngle);
            float cosAngle = FastMath.cos(radianAngle);

            int rotationOffsetX = Math.round((spriteOffsetX * cosAngle) + (spriteOffsetY * sinAngle));
            int rotationOffsetY = Math.round((spriteOffsetX * sinAngle) + (spriteOffsetY * cosAngle));

            g.drawTexture(arrowSprite.getFrame(0), fixedX + rotationOffsetX, fixedY - rotationOffsetY, scaledWidth,
                          scaledHeight, srcX, srcY, srcW, srcH, centerX - fixedX, centerY - fixedY, angle,
                          pointerColor);
        }
    }

    /**
     * This functions returns if this pointer is on the mini map right now or outside of it.
     *
     * @return {@code true} in case the pointer is on the map
     */
    private boolean isOnMapArea() {
        return FastMath.sqrt(FastMath.sqr(currentDeltaX) + FastMath.sqr(currentDeltaY)) < 71;
    }

    /**
     * Update the angle of arrow.
     *
     * @param delta the time since the last update
     * @param playerLocation the current location of the player
     */
    void update(int delta, @Nonnull ServerCoordinate playerLocation) {
        if (targetLocation == null) {
            throw new IllegalStateException("This pointer has no set target and is not valid to be updated.");
        }
        int dX = targetLocation.getX() - playerLocation.getX();
        int dY = targetLocation.getY() - playerLocation.getY();

        if ((currentDeltaX != dX) || (currentDeltaY != dY)) {
            currentDeltaX = dX;
            currentDeltaY = dY;

            targetAngle = ((Math.round(getAngle() * 10.f) % 3600) + 3600) % 3600;
        }

        animateAngle(delta);
    }

    /**
     * Perform the animation of this arrow.
     *
     * @param delta the time since the last update
     */
    private void animateAngle(int delta) {
        if (targetAngle == currentAngle) {
            return;
        }

        int angleDiff = targetAngle - currentAngle;
        if (Math.abs(angleDiff) <= 1800) {
            currentAngle += AnimationUtility.approach(0, angleDiff, -1800, 1800, delta);
        } else if (angleDiff > 0) {
            currentAngle += AnimationUtility.approach(0, angleDiff - 3600, -3600, 0, delta);
        } else {
            currentAngle += AnimationUtility.approach(0, angleDiff + 3600, 0, 3600, delta);
        }
        currentAngle = ((currentAngle % 3600) + 3600) % 3600;
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
