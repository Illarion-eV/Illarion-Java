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
    public static final ImmutableColor BLACK = new ImmutableColor(0.f, 0.f, 0.f);
    /**
     * The maximal valid integer value of a color component.
     */
    public static final int MAX_INT_VALUE = 255;
    /**
     * A fully opaque white color. This color is immutable.
     */
    public static final ImmutableColor WHITE = new ImmutableColor(1.f, 1.f, 1.f);

    /**
     * The default color of text entries.
     */
    public static final ImmutableColor clouds = new ImmutableColor(0.93f, 0.94f, 0.95f);

    /**
     * The color of shouted messages
     */
    public static final ImmutableColor pomegranate = new ImmutableColor(0.75f, 0.22f, 0.17f);

    /**
     * The color of whispered text, OOC text, dead status bar
     */
    public static final ImmutableColor asbestos = new ImmutableColor(0.5f, 0.55f, 0.55f);

    /**
     * The color of emotes, the item number of stacked items, player character names
     */
    public static final ImmutableColor sunflower = new ImmutableColor(0.95f, 0.77f, 0.06f);

    /**
     * The color of inform.
     */
    public static final ImmutableColor peterRiver = new ImmutableColor(0.2f, 0.6f, 0.86f);

    /**
     * The color of high inform.
     */
    public static final ImmutableColor pumpkin = new ImmutableColor(0.83f, 0.33f, 0.0f);

    /**
     * Color for (talkto = !tt) admin messages.
     */
    public static final ImmutableColor greenSea = new ImmutableColor(0.09f, 0.63f, 0.52f);

    /**
     * Color for (!bc) broadcasts.
     */
    public static final ImmutableColor amethyst = new ImmutableColor(0.61f, 0.35f, 0.71f);

    /**
     * Other colours in our palette
     */
    public static final ImmutableColor turquoise = new ImmutableColor(26, 188, 156);
    public static final ImmutableColor emerald = new ImmutableColor(46, 204, 113);
    public static final ImmutableColor nephritis = new ImmutableColor(39, 174, 96);
    public static final ImmutableColor belizeHole = new ImmutableColor(41, 128, 185);
    public static final ImmutableColor wisteria = new ImmutableColor(142, 68, 173);
    public static final ImmutableColor wetAsphalt = new ImmutableColor(52, 73, 94);
    public static final ImmutableColor midnightBlue = new ImmutableColor(44, 62, 80);
    public static final ImmutableColor orange = new ImmutableColor(243, 156, 18);
    public static final ImmutableColor carrot = new ImmutableColor(230, 126, 34);
    public static final ImmutableColor alizarin = new ImmutableColor(231, 76, 60);
    public static final ImmutableColor silver = new ImmutableColor(189, 195, 199);
    public static final ImmutableColor concrete = new ImmutableColor(149, 165, 166);


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
     * Get this color as a immutable copy.
     * <p>
     * The returned instance is not a wrapper that changes if this color is changed. It is a actual copy of the current
     * value of the color.
     *
     * @return a immutable copy of this color.
     */
    @Nonnull
    public ImmutableColor getImmutableCopy() {
        return new ImmutableColor(this);
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
    @Contract(value = "null->false", pure = true)
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof Color) && equals((Color) obj);
    }

    @Override
    @Nonnull
    @Contract(pure = true)
    public String toString() {
        return "Color(r:" + red + " g:" + green + " b:" + blue + " a:" + alpha + ')';
    }
}
