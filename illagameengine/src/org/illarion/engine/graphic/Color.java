/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.graphic;

import javax.annotation.Nonnull;

/**
 * This class is in general used to define a color value. It consists of four color components.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Color {
    /**
     * A fully opaque black color. This color is immutable.
     */
    public static final Color BLACK = new ImmutableColor(0.f, 0.f, 0.f);
    /**
     * The maximal valid integer value of a color component.
     */
    public static final int MAX_INT_VALUE = 255;
    /**
     * A fully opaque white color. This color is immutable.
     */
    public static final Color WHITE = new ImmutableColor(1.f, 1.f, 1.f);

    /**
     * A fully opaque red color. This color is immutable.
     */
    public static final Color RED = new ImmutableColor(1.f, 0.f, 0.f);

    /**
     * A fully opaque yellow color. This color is immutable.
     */
    public static final Color YELLOW = new ImmutableColor(1.f, 1.f, 0.f);

    /**
     * A fully opaque gray color. This color is immutable.
     */
    public static final Color GRAY = new ImmutableColor(0.5f, 0.5f, 0.5f);

    /**
     * The alpha component of the color.
     */
    private int alpha;
    /**
     * The blue component of the color.
     */
    private int blue;
    /**
     * The green component of the color.
     */
    private int green;
    /**
     * The red component of the color.
     */
    private int red;

    /**
     * Create a new color instance with the specified values.
     *
     * @param red   the red color component
     * @param green the green color component
     * @param blue  the blue color component
     * @param alpha the alpha color component
     */
    public Color(final int red, final int green, final int blue, final int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * Create a new color instance with the specified values.
     *
     * @param red   the red color component
     * @param green the green color component
     * @param blue  the blue color component
     * @param alpha the alpha color component
     */
    public Color(final float red, final float green, final float blue, final float alpha) {
        this.red = Math.round(red * MAX_INT_VALUE);
        this.green = Math.round(green * MAX_INT_VALUE);
        this.blue = Math.round(blue * MAX_INT_VALUE);
        this.alpha = Math.round(alpha * MAX_INT_VALUE);
    }

    /**
     * Create a new opaque color instance with the specified values.
     *
     * @param red   the red color component
     * @param green the green color component
     * @param blue  the blue color component
     */
    public Color(final int red, final int green, final int blue) {
        this(red, green, blue, MAX_INT_VALUE);
    }

    /**
     * Create a new opaque color instance with the specified values.
     *
     * @param red   the red color component
     * @param green the green color component
     * @param blue  the blue color component
     */
    public Color(final float red, final float green, final float blue) {
        this(red, green, blue, 1.f);
    }

    /**
     * Copy the values of another instance of the color into a new color instance.
     *
     * @param org the original color value that is the data provider
     */
    public Color(@Nonnull final Color org) {
        this(org.red, org.green, org.blue, org.alpha);
    }

    /**
     * Add the values of a color to this one.
     *
     * @param color the color that supplies the new values
     */
    public void add(@Nonnull final Color color) {
        red += color.red;
        green += color.green;
        blue += color.blue;
        alpha += color.alpha;
    }

    /**
     * Get the alpha color component.
     *
     * @return the alpha color component
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * Set the alpha component of the color.
     *
     * @param alpha the alpha component of the color
     */
    public void setAlpha(final int alpha) {
        this.alpha = alpha;
    }

    /**
     * Get the alpha color component.
     *
     * @return the alpha color component
     */
    public float getAlphaf() {
        return (float) alpha / (float) MAX_INT_VALUE;
    }

    /**
     * Set the alpha component of the color.
     *
     * @param alpha the alpha component of the color
     */
    public void setAlphaf(final float alpha) {
        this.alpha = Math.round(alpha * MAX_INT_VALUE);
    }

    /**
     * Get the blue color component.
     *
     * @return the blue color component
     */
    public int getBlue() {
        return blue;
    }

    /**
     * Set the blue component of the color.
     *
     * @param blue the blue component of the color
     */
    public void setBlue(final int blue) {
        this.blue = blue;
    }

    /**
     * Get the blue color component.
     *
     * @return the blue color component
     */
    public float getBluef() {
        return (float) blue / (float) MAX_INT_VALUE;
    }

    /**
     * Set the blue component of the color.
     *
     * @param blue the blue component of the color
     */
    public void setBluef(final float blue) {
        this.blue = Math.round(blue * MAX_INT_VALUE);
    }

    /**
     * Get the green color component.
     *
     * @return the green color component
     */
    public int getGreen() {
        return green;
    }

    /**
     * Set the green component of the color.
     *
     * @param green the green component of the color
     */
    public void setGreen(final int green) {
        this.green = green;
    }

    /**
     * Get the green color component.
     *
     * @return the green color component
     */
    public float getGreenf() {
        return (float) green / (float) MAX_INT_VALUE;
    }

    /**
     * Set the green component of the color.
     *
     * @param green the green component of the color
     */
    public void setGreenf(final float green) {
        this.green = Math.round(green * MAX_INT_VALUE);
    }

    /**
     * Get the red color component.
     *
     * @return the red color component
     */
    public int getRed() {
        return red;
    }

    /**
     * Set the red component of the color.
     *
     * @param red the red component of the color
     */
    public void setRed(final int red) {
        this.red = red;
    }

    /**
     * Get the red color component.
     *
     * @return the red color component
     */
    public float getRedf() {
        return (float) red / (float) MAX_INT_VALUE;
    }

    /**
     * Set the red component of the color.
     *
     * @param red the red component of the color
     */
    public void setRedf(final float red) {
        this.red = Math.round(red * MAX_INT_VALUE);
    }

    /**
     * Set this color to the same values as another color.
     *
     * @param org the color instance that supplies the new color values
     */
    public void setColor(@Nonnull final Color org) {
        red = org.red;
        green = org.green;
        blue = org.blue;
        alpha = org.alpha;
    }

    /**
     * Multiply the values of this colors with the values of another and store the result in this color instance.
     *
     * @param mul the color that supplies the values multiplied to this color
     */
    public void multiply(@Nonnull final Color mul) {
        red = (red * mul.red) / MAX_INT_VALUE;
        green = (green * mul.green) / MAX_INT_VALUE;
        blue = (blue * mul.blue) / MAX_INT_VALUE;
        alpha = (alpha * mul.alpha) / MAX_INT_VALUE;
    }

    /**
     * Scale all components of the color with a single value.
     *
     * @param value the value multiplied to each color component
     */
    public void multiply(final float value) {
        red *= value;
        green *= value;
        blue *= value;
        alpha *= value;
    }

    /**
     * Get the luminance level of the color.
     *
     * @return the luminance of the color
     */
    public int getLuminance() {
        return (red + green + blue) / 3;
    }

    /**
     * Get the luminance level of the color.
     *
     * @return the luminance level of the color
     */
    public float getLuminancef() {
        return (red + green + blue) / (float) MAX_INT_VALUE / 3.f;
    }
}
