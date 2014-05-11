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

import org.illarion.engine.graphic.Color;

import javax.annotation.Nonnull;

/**
 * This is a special implementation of a color value that approaches another value.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AnimatedColor {
    /**
     * The color that is the target of the animation.
     */
    @Nonnull
    private final Color targetColor;

    /**
     * The current color value.
     */
    @Nonnull
    private final Color currentColor;

    /**
     * Create a new animated color and set the instance of the target color.
     *
     * @param targetColor the target color. This instance is stored and its possible to alter it cause the class
     *                    approach another color. This class will never alter this instance of the color by itself.
     */
    public AnimatedColor(@Nonnull Color targetColor) {
        this.targetColor = targetColor;
        currentColor = new Color(targetColor);
    }

    /**
     * This sets the current color and the target color to the same value without any animation.
     */
    public void setCurrentColorToTarget() {
        currentColor.setColor(targetColor);
    }

    /**
     * Update the current color by approaching the target color.
     *
     * @param delta the time in milliseconds since the last update
     */
    public void update(int delta) {
        AnimationUtility.approach(currentColor, targetColor, delta);
    }

    @Nonnull
    public Color getCurrentColor() {
        return currentColor;
    }

    @Nonnull
    public Color getTargetColor() {
        return targetColor;
    }
}
