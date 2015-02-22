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

import illarion.common.net.NetCommReader;
import illarion.common.util.FastMath;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

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
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     * @param alpha the alpha color component
     */
    public Color(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * Create a new color instance with the specified values.
     *
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     * @param alpha the alpha color component
     */
    public Color(float red, float green, float blue, float alpha) {
        this(Math.round(red * MAX_INT_VALUE), Math.round(green * MAX_INT_VALUE), Math.round(blue * MAX_INT_VALUE),
             Math.round(alpha * MAX_INT_VALUE));
    }

    /**
     * Create a new opaque color instance with the specified values.
     *
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     */
    public Color(int red, int green, int blue) {
        this(red, green, blue, MAX_INT_VALUE);
    }

    /**
     * Create a new opaque color instance with the specified values.
     *
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     */
    public Color(float red, float green, float blue) {
        this(red, green, blue, 1.f);
    }

    /**
     * Copy the values of another instance of the color into a new color instance.
     *
     * @param org the original color value that is the data provider
     */
    public Color(@Nonnull Color org) {
        this(org.red, org.green, org.blue, org.alpha);
    }

    /**
     * Create a color instance from the communication interface.
     *
     * @param reader the reader used to fetch the color values
     * @throws IOException in case reading fails
     */
    public Color(@Nonnull NetCommReader reader) throws IOException {
        this(reader.readUByte(), reader.readUByte(), reader.readUByte(), reader.readUByte());
    }

    /**
     * Add the values of a color to this one.
     *
     * @param color the color that supplies the new values
     */
    public void add(@Nonnull Color color) {
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
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    /**
     * Get the alpha color component.
     *
     * @return the alpha color component
     */
    public float getAlphaf() {
        return (float) alpha / MAX_INT_VALUE;
    }

    /**
     * Set the alpha component of the color.
     *
     * @param fAlpha the alpha component of the color
     */
    public void setAlphaf(float fAlpha) {
        setAlpha(Math.round(fAlpha * MAX_INT_VALUE));
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
    public void setBlue(int blue) {
        this.blue = blue;
    }

    /**
     * Get the blue color component.
     *
     * @return the blue color component
     */
    public float getBluef() {
        return (float) blue / MAX_INT_VALUE;
    }

    /**
     * Set the blue component of the color.
     *
     * @param fBlue the blue component of the color
     */
    public void setBluef(float fBlue) {
        setBlue(Math.round(fBlue * MAX_INT_VALUE));
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
    public void setGreen(int green) {
        this.green = green;
    }

    /**
     * Get the green color component.
     *
     * @return the green color component
     */
    public float getGreenf() {
        return (float) green / MAX_INT_VALUE;
    }

    /**
     * Set the green component of the color.
     *
     * @param fGreen the green component of the color
     */
    public void setGreenf(float fGreen) {
        setGreen(Math.round(fGreen * MAX_INT_VALUE));
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
    public void setRed(int red) {
        this.red = red;
    }

    /**
     * Get the red color component.
     *
     * @return the red color component
     */
    public float getRedf() {
        return (float) red / MAX_INT_VALUE;
    }

    /**
     * Set the red component of the color.
     *
     * @param fRed the red component of the color
     */
    public void setRedf(float fRed) {
        setRed(Math.round(fRed * MAX_INT_VALUE));
    }

    /**
     * Set this color to the same values as another color.
     *
     * @param org the color instance that supplies the new color values
     */
    public void setColor(@Nonnull Color org) {
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
    public void multiply(@Nonnull Color mul) {
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
    public void multiply(float value) {
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
        return (red + green + blue) / MAX_INT_VALUE / 3.f;
    }

    /**
     * Limit the color components to its legal values.
     */
    public void clamp() {
        red = FastMath.clamp(red, 0, MAX_INT_VALUE);
        green = FastMath.clamp(green, 0, MAX_INT_VALUE);
        blue = FastMath.clamp(blue, 0, MAX_INT_VALUE);
        alpha = FastMath.clamp(alpha, 0, MAX_INT_VALUE);
    }

    @Override
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof Color) && equals((Color) obj);
    }

    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Color color) {
        return (color != null) && (red == color.red) && (green == color.green) && (blue == color.blue) &&
                (alpha == color.alpha);
    }

    @Override
    @Contract(pure = true)
    public int hashCode() {
        int result = alpha;
        result = (31 * result) + blue;
        result = (31 * result) + green;
        result = (31 * result) + red;
        return result;
    }

    @Override
    @Nonnull
    @Contract(pure = true)
    public String toString() {
        return "Color(r:" + red + " g:" + green + " b:" + blue + " a:" + alpha + ')';
    }
}
