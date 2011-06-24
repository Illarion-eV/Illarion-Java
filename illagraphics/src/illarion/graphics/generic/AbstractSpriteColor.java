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
package illarion.graphics.generic;

import illarion.common.util.FastMath;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * Generic sprite color implementation that implements the parts of the sprite
 * color that is shared by all library specific implementations.
 * 
 * @author Martin Karing
 * @since 2.00
 * @version 2.00
 */
public abstract class AbstractSpriteColor implements SpriteColor {
    /**
     * Value of the alpha value of the color. 0 is fully transparent,
     * {@link #COLOR_MAX} is fully opaque.
     */
    private int alpha = COLOR_MAX;

    /**
     * Value of the blue share of the color. 0 is no blue share,
     * {@link #COLOR_MAX} is full blue share.
     */
    private int blue = 0;

    /**
     * Checks if the values got changed and need to be checked if they are still
     * within the limits or not.
     */
    private boolean dirty = false;

    /**
     * Value of the green share of the color. 0 is no green share,
     * {@link #COLOR_MAX} is full green share.
     */
    private int green = 0;

    /**
     * Value of the red share of the color. 0 is no red share,
     * {@link #COLOR_MAX} is full red share.
     */
    private int red = 0;

    /**
     * Constructor that creates a instance with default color. Means black and
     * fully opaque.
     */
    protected AbstractSpriteColor() {
        // nothing needed to do at constructing time
    }

    /**
     * Copy constructor, create a new color that is a exact copy of another
     * color.
     * 
     * @param org the original color that is copied to the new instance
     */
    protected AbstractSpriteColor(final AbstractSpriteColor org) {
        red = org.red;
        green = org.green;
        blue = org.blue;
        alpha = org.alpha;
        dirty = org.dirty;
    }

    /**
     * Constructor that create a new instance with a set color that is fully
     * opaque. The values are taken as float values and so 0.f is the lowest
     * value and 1.f is the highest value.
     * 
     * @param newRed the red share of the new color
     * @param newGreen the green share of the new color
     * @param newBlue the blue share of the new color
     */
    protected AbstractSpriteColor(final float newRed, final float newGreen,
        final float newBlue) {
        red = (int) (newRed * COLOR_MAX);
        green = (int) (newGreen * COLOR_MAX);
        blue = (int) (newBlue * COLOR_MAX);
        dirty = true;
    }

    /**
     * Constructor that create a new instance with a set color and a set
     * transparency. The values are taken as float values and so 0.f is the
     * lowest value and 1.f is the highest value.
     * 
     * @param newRed the red share of the new color
     * @param newGreen the green share of the new color
     * @param newBlue the blue share of the new color
     * @param newAlpha the alpha value of the new color
     */
    protected AbstractSpriteColor(final float newRed, final float newGreen,
        final float newBlue, final float newAlpha) {
        this(newRed, newGreen, newBlue);
        alpha = (int) (newAlpha * COLOR_MAX);
    }

    /**
     * Constructor that create a new instance with a set color that is fully
     * opaque. The values are taken as integer values and so 0 is the lowerst
     * value and {@link #COLOR_MAX} is the highest value.
     * 
     * @param newRed the red share of the new color
     * @param newGreen the green share of the new color
     * @param newBlue the blue share of the new color
     */
    protected AbstractSpriteColor(final int newRed, final int newGreen,
        final int newBlue) {
        red = newRed;
        green = newGreen;
        blue = newBlue;
        dirty = true;
    }

    /**
     * Constructor that create a new instance with a set color and a set
     * transparency. The values are taken as integer values and so 0 is the
     * lowest value and {@link #COLOR_MAX} is the highest value.
     * 
     * @param newRed the red share of the new color
     * @param newGreen the green share of the new color
     * @param newBlue the blue share of the new color
     * @param newAlpha the alpha value of the new color
     */
    protected AbstractSpriteColor(final int newRed, final int newGreen,
        final int newBlue, final int newAlpha) {
        this(newRed, newGreen, newBlue);
        alpha = newAlpha;
    }

    /**
     * Add a value to each color component of this color. The value should be
     * between 0.f and 1.f
     * 
     * @param add the value that is added to the color components
     */
    @Override
    public final void add(final float add) {
        red += add * COLOR_MAX;
        green += add * COLOR_MAX;
        blue += add * COLOR_MAX;
        dirty = true;
    }

    /**
     * Add a different value to each color component of this color. The values
     * should be between 0.f and 1.f
     * 
     * @param addRed the value added to the red share of the color
     * @param addGreen the value added to the green share of the color
     * @param addBlue the value added to the blue share of the color
     */
    @Override
    public final void add(final float addRed, final float addGreen,
        final float addBlue) {
        red += addRed * COLOR_MAX;
        green += addGreen * COLOR_MAX;
        blue += addBlue * COLOR_MAX;
        dirty = true;
    }

    /**
     * Add a value to each color component of this color. The value added should
     * be between 0 and {@link #COLOR_MAX}.
     * 
     * @param add the value that is added to the color components
     */
    @Override
    public final void add(final int add) {
        red += add;
        green += add;
        blue += add;
        dirty = true;
    }

    /**
     * Add a different value to each color component of this color. The values
     * added should be between 0 and {@link #COLOR_MAX}.
     * 
     * @param addRed the value added to the red share of the color
     * @param addGreen the value added to the green share of the color
     * @param addBlue the value added to the blue share of the color
     */
    @Override
    public final void add(final int addRed, final int addGreen,
        final int addBlue) {
        red += addRed;
        green += addGreen;
        blue += addBlue;
        dirty = true;
    }

    /**
     * Add the values of another color to this color. The alpha value is not
     * changed.
     * 
     * @param addColor the color that is added to this color
     */
    @Override
    public final void add(final SpriteColor addColor) {
        red += addColor.getRedi();
        green += addColor.getGreeni();
        blue += addColor.getBluei();
        dirty = true;
    }

    /**
     * Add a value to the alpha value of this color. The value should be between
     * 0.f and 1.f
     * 
     * @param add the value that is added to the alpha value
     */
    @Override
    public final void addAlpha(final float add) {
        alpha += add * COLOR_MAX;
        dirty = true;
    }

    /**
     * Add a value to the alpha value of this color. The value added should be
     * between 0 and {@link #COLOR_MAX}.
     * 
     * @param add the value that is added to the alpha value
     */
    @Override
    public final void addAlpha(final int add) {
        alpha += add;
        dirty = true;
    }

    /**
     * Approach a second color so change the values of this color slowly towards
     * the target color.
     * 
     * @param target the target color of the approach
     * @return true in case the approach is done
     */
    @Override
    public final boolean approach(final SpriteColor target) {
        final int quality = Graphics.getInstance().getQuality();

        // no approaching for now quality
        if (quality <= Graphics.QUALITY_LOW) {
            red = target.getRedi();
            green = target.getGreeni();
            blue = target.getBluei();
            alpha = target.getAlphai();
            return true;
        }
        final int diffRed = red - target.getRedi();
        final int diffGreen = green - target.getGreeni();
        final int diffBlue = blue - target.getBluei();
        final int diffAlpha = alpha - target.getAlphai();

        // Approach does not need to do anything
        if ((diffRed == 0) && (diffGreen == 0) && (diffBlue == 0)
            && (diffAlpha == 0)) {
            return true;
        }

        if (quality == Graphics.QUALITY_NORMAL) {
            red -= Math.min(diffRed, APPROACH_SPEED);
            green -= Math.min(diffGreen, APPROACH_SPEED);
            blue -= Math.min(diffBlue, APPROACH_SPEED);
            alpha -= Math.min(diffAlpha, APPROACH_SPEED);
        } else {
            final int maxDiff =
                Math.max(
                    Math.abs(diffRed),
                    Math.max(Math.abs(diffGreen),
                        Math.max(Math.abs(diffBlue), Math.abs(diffAlpha))));

            final int mod = Math.min(maxDiff, APPROACH_SPEED);

            final float diffMod = (float) mod / (float) maxDiff;
            red -= diffRed * diffMod;
            green -= diffGreen * diffMod;
            blue -= diffBlue * diffMod;
            alpha -= diffAlpha * diffMod;
        }
        return ((red == target.getRedi()) && (green == target.getGreeni())
            && (blue == target.getBluei()) && (alpha == target.getAlphai()));
    }

    @Override
    public AbstractSpriteColor clone() throws CloneNotSupportedException {
        return (AbstractSpriteColor) super.clone();
    }

    /**
     * Compare the sprite color to another object to see if it contains the same
     * values. This only returns true in case the object is a instance of
     * SpriteColor and all color shares contain the same values. The alpha value
     * is not checked.
     * 
     * @param o the object the sprite color shall be compared with
     * @return true in case the object is a sprite color with the same color as
     *         the current object.
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof AbstractSpriteColor)) {
            return false;
        }

        final AbstractSpriteColor tempColor = (AbstractSpriteColor) o;

        if (dirty) {
            checkColor();
        }
        if (tempColor.dirty) {
            tempColor.checkColor();
        }

        return (red == tempColor.red) && (green == tempColor.green)
            && (blue == tempColor.blue);
    }

    /**
     * Get the alpha value of this color. The value will be between 0.f and 1.f
     * 
     * @return the alpha value of this color
     */
    @Override
    public final float getAlphaf() {
        if (dirty) {
            checkColor();
        }

        return (float) alpha / COLOR_MAX;
    }

    /**
     * Get the alpha value of this color. The value will be between 0 and
     * {@link #COLOR_MAX}.
     * 
     * @return the alpha value of this color
     */
    @Override
    public final int getAlphai() {
        if (dirty) {
            checkColor();
        }

        return alpha;
    }

    /**
     * Get the blue share of this color. The value will be between 0.f and 1.f
     * 
     * @return the blue share of this color
     */
    @Override
    public final float getBluef() {
        if (dirty) {
            checkColor();
        }

        return (float) blue / COLOR_MAX;
    }

    /**
     * Get the blue share of this color. The value will be between 0 and
     * {@link #COLOR_MAX}.
     * 
     * @return the blue share of this color
     */
    @Override
    public final int getBluei() {
        if (dirty) {
            checkColor();
        }

        return blue;
    }

    /**
     * Get the green share of this color. The value will be between 0.f and 1.f
     * 
     * @return the green share of this color
     */
    @Override
    public final float getGreenf() {
        if (dirty) {
            checkColor();
        }

        return (float) green / COLOR_MAX;
    }

    /**
     * Get the green share of this color. The value will be between 0 and
     * {@link #COLOR_MAX}.
     * 
     * @return the green share of this color
     */
    @Override
    public final int getGreeni() {
        if (dirty) {
            checkColor();
        }

        return green;
    }

    /**
     * Get the illumination of the color calculated using a fast computation
     * formula to get the illumination value. The value will be between 0.f for
     * darkness and 1.f for bright light.
     * 
     * @return the calculated illumination
     */
    @Override
    public final float getLuminationf() {
        return (float) getLuminationi() / COLOR_MAX;
    }

    /**
     * Get the illumination of the color calculated using a fast computation
     * illumination to get the illumination value. The value will be between 0
     * for darkness and {@link #COLOR_MAX} for bright light.
     * 
     * @return the calculated illumination
     */
    @Override
    public final int getLuminationi() {
        if (dirty) {
            checkColor();
        }

        // faster computation, (2*R + 5*G + B) / 8
        int tmp = red << 1; // 2 * red
        tmp += (green << 2) + green; // 5 * green
        tmp += blue; // 1 * blue
        tmp = tmp >> 2 >> 1; // divide by 8
        return tmp;
    }

    /**
     * Get the red share of this color. The value will be between 0.f and 1.f
     * 
     * @return the red share of this color
     */
    @Override
    public final float getRedf() {
        if (dirty) {
            checkColor();
        }

        return (float) red / COLOR_MAX;
    }

    /**
     * Get the red share of this color. The value will be between 0 and
     * {@link #COLOR_MAX}.
     * 
     * @return the red share of this color
     */
    @Override
    public final int getRedi() {
        if (dirty) {
            checkColor();
        }

        return red;
    }

    /**
     * Get the hash code of the color that contains all color shares of this
     * color.
     * 
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return ((red & COLOR_MAX) << Byte.SIZE)
            + ((green & COLOR_MAX) << Byte.SIZE) + (blue & COLOR_MAX);
    }

    /**
     * Invert this color.
     */
    @Override
    public final void invert() {
        if (dirty) {
            checkColor();
        }
        red = SpriteColor.COLOR_MAX - red;
        green = SpriteColor.COLOR_MAX - green;
        blue = SpriteColor.COLOR_MAX - blue;
    }

    /**
     * Multiply the color shares by a modificator. All color shares are
     * multiplied with the same modificator.
     * 
     * @param mod the modificator the color shares are multiplicated with
     */
    @Override
    public final void multiply(final float mod) {
        red *= mod;
        green *= mod;
        blue *= mod;
        dirty = true;
    }

    /**
     * Multiply each color share with a different modificator.
     * 
     * @param modRed the modificator the red share is multiplicated with
     * @param modGreen the modificator the green share is multiplicated with
     * @param modBlue the modificator the blue share is multiplicated with
     */
    @Override
    public final void multiply(final float modRed, final float modGreen,
        final float modBlue) {
        red *= modRed;
        green *= modGreen;
        blue *= modBlue;
        dirty = true;
    }

    /**
     * Multiply the Alpha value of this color with a modificator.
     * 
     * @param mod the modificator the alpha value is multiplied with
     */
    @Override
    public final void multiplyAlpha(final float mod) {
        alpha *= mod;
        dirty = true;
    }

    /**
     * Reset the color values back to the default values. Means black and fully
     * opaque.
     */
    @Override
    public final void resetColor() {
        red = 0;
        green = 0;
        blue = 0;
        alpha = 0;
        dirty = false;
    }

    /**
     * Set all color components of this color to the same value. The value
     * should be between 0.f and 1.f
     * 
     * @param value the value all color components shall get
     */
    @Override
    public final void set(final float value) {
        red = (int) (value * COLOR_MAX);
        green = (int) (value * COLOR_MAX);
        blue = (int) (value * COLOR_MAX);
        dirty = true;
    }

    /**
     * Set each color component to a value. The values should be between 0.f and
     * 1.f
     * 
     * @param setRed the new red value of this color
     * @param setGreen the new green value of this color
     * @param setBlue the new blue value of this color
     */
    @Override
    public final void set(final float setRed, final float setGreen,
        final float setBlue) {
        red = (int) (setRed * COLOR_MAX);
        green = (int) (setGreen * COLOR_MAX);
        blue = (int) (setBlue * COLOR_MAX);
        dirty = true;
    }

    /**
     * Set all color components of this color to the same value. The value
     * should be between 0 and {@link #COLOR_MAX}.
     * 
     * @param value the value all color components shall get
     */
    @Override
    public final void set(final int value) {
        red = value;
        green = value;
        blue = value;
        dirty = true;
    }

    /**
     * Set each color component to a value. The values should be between 0 and
     * {@link #COLOR_MAX}.
     * 
     * @param setRed the new red value of this color
     * @param setGreen the new green value of this color
     * @param setBlue the new blue value of this color
     */
    @Override
    public final void set(final int setRed, final int setGreen,
        final int setBlue) {
        red = setRed;
        green = setGreen;
        blue = setBlue;
        dirty = true;
    }

    /**
     * Set the color values of another color to this color. The alpha value is
     * not changed.
     * 
     * @param color the color that color values are used
     */
    @SuppressWarnings("nls")
    @Override
    public final void set(final SpriteColor color) {
        if (color == null) {
            throw new IllegalArgumentException(
                "New color value must not be NULL");
        }

        if (color instanceof AbstractSpriteColor) {
            final AbstractSpriteColor tempColor = (AbstractSpriteColor) color;
            red = tempColor.red;
            green = tempColor.green;
            blue = tempColor.blue;
            dirty = tempColor.dirty;
        } else {
            throw new IllegalArgumentException(
                "Invalid sprite color implementation: " + color.toString());
        }
    }

    /**
     * Set the alpha value of this color to a value. The value should be between
     * 0.f and 1.f
     * 
     * @param newAlpha the new value for the alpha of this color
     */
    @Override
    public final void setAlpha(final float newAlpha) {
        alpha = (int) (newAlpha * COLOR_MAX);
        dirty = true;
    }

    /**
     * Set the alpha value of this color to a value. The value should be between
     * 0 and {@link #COLOR_MAX}.
     * 
     * @param newAlpha the new value for the alpha of this color
     */
    @Override
    public final void setAlpha(final int newAlpha) {
        alpha = newAlpha;
        dirty = true;
    }

    /**
     * Set the blue component of this color to a value. The value should be
     * between 0.f and 1.f
     * 
     * @param newBlue the new value of the blue share of this color
     */
    @Override
    public final void setBlue(final float newBlue) {
        blue = (int) (newBlue * COLOR_MAX);
        dirty = true;
    }

    /**
     * Set the blue component of this color to a value. The value should be
     * between 0 and {@link #COLOR_MAX}.
     * 
     * @param newBlue the new value of the blue share of this color
     */
    @Override
    public final void setBlue(final int newBlue) {
        blue = newBlue;
        dirty = true;
    }

    /**
     * Set the green component of this color to a value. The value should be
     * between 0.f and 1.f
     * 
     * @param newGreen the new value of the green share of this color
     */
    @Override
    public final void setGreen(final float newGreen) {
        green = (int) (newGreen * COLOR_MAX);
        dirty = true;
    }

    /**
     * Set the green component of this color to a value. The value should be
     * between 0 and {@link #COLOR_MAX}.
     * 
     * @param newGreen the new value of the green share of this color
     */
    @Override
    public final void setGreen(final int newGreen) {
        green = newGreen;
        dirty = true;
    }

    /**
     * Set the red component of this color to a value. The value should be
     * between 0.f and 1.f
     * 
     * @param newRed the new value of the red share of this color
     */
    @Override
    public final void setRed(final float newRed) {
        red = (int) (newRed * COLOR_MAX);
        dirty = true;
    }

    /**
     * Set the red component of this color to a value. The value should be
     * between 0 and {@link #COLOR_MAX}.
     * 
     * @param newRed the new value of the red share of this color
     */
    @Override
    public final void setRed(final int newRed) {
        red = newRed;
        dirty = true;
    }

    /**
     * Subtract a value to each color component of this color. The value should
     * be between 0.f and 1.f
     * 
     * @param sub the value that is subtracted to the color components
     */
    @Override
    public final void sub(final float sub) {
        red -= sub * COLOR_MAX;
        green -= sub * COLOR_MAX;
        blue -= sub * COLOR_MAX;
        dirty = true;
    }

    /**
     * Subtract a different value to each color component of this color. The
     * values should be between 0.f and 1.f
     * 
     * @param subRed the value subtracted to the red share of the color
     * @param subGreen the value subtracted to the green share of the color
     * @param subBlue the value subtracted to the blue share of the color
     */
    @Override
    public final void sub(final float subRed, final float subGreen,
        final float subBlue) {
        red -= subRed * COLOR_MAX;
        green -= subGreen * COLOR_MAX;
        blue -= subBlue * COLOR_MAX;
        dirty = true;
    }

    /**
     * Subtract a value to each color component of this color. The value added
     * should be between 0 and {@link #COLOR_MAX}.
     * 
     * @param sub the value that is subtracted to the color components
     */
    @Override
    public final void sub(final int sub) {
        red -= sub;
        green -= sub;
        blue -= sub;
        dirty = true;
    }

    /**
     * Subtract a different value to each color component of this color. The
     * values subtracted should be between 0 and {@link #COLOR_MAX}.
     * 
     * @param subRed the value subtracted to the red share of the color
     * @param subGreen the value subtracted to the green share of the color
     * @param subBlue the value subtracted to the blue share of the color
     */
    @Override
    public final void sub(final int subRed, final int subGreen,
        final int subBlue) {
        red -= subRed;
        green -= subGreen;
        blue -= subBlue;
        dirty = true;
    }

    /**
     * Subtract the values of another color to this color. The alpha value is
     * not changed.
     * 
     * @param subColor the color that is subtracted to this color
     */
    @Override
    public final void sub(final SpriteColor subColor) {
        red -= subColor.getRedi();
        green -= subColor.getGreeni();
        blue -= subColor.getBluei();
        dirty = true;
    }

    /**
     * Subtract a value to the alpha value of this color. The value should be
     * between 0.f and 1.f
     * 
     * @param sub the value that is subtracted to the alpha value
     */
    @Override
    public final void subAlpha(final float sub) {
        alpha -= sub * COLOR_MAX;
        dirty = true;
    }

    /**
     * Subtract a value to the alpha value of this color. The value subtracted
     * should be between 0 and {@link #COLOR_MAX}.
     * 
     * @param sub the value that is subtracted to the alpha value
     */
    @Override
    public final void subAlpha(final int sub) {
        alpha -= sub;
        dirty = true;
    }

    /**
     * Check and fix the color if needed. Limit the color values to the given
     * borders.
     */
    protected final void checkColor() {
        red = FastMath.clamp(red, COLOR_MIN, COLOR_MAX);
        green = FastMath.clamp(green, COLOR_MIN, COLOR_MAX);
        blue = FastMath.clamp(blue, COLOR_MIN, COLOR_MAX);
        alpha = FastMath.clamp(alpha, COLOR_MIN, COLOR_MAX);
        dirty = false;
    }

    /**
     * Check if the color is currently dirty and needs to be cleaned up.
     * 
     * @return <code>true</code> in case the color is dirty
     */
    protected final boolean isDirty() {
        return dirty;
    }
}
