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
package illarion.graphics;

/**
 * Default class to handle colors inside the client. The colors are only limited
 * against 0. Its possible to raise the values above {@link #COLOR_MAX} what
 * will result in a increase of the color values when it comes to the display.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface SpriteColor extends Cloneable {
    /**
     * How many color points the color can change in one turn. The smaller the
     * value is the slower one sprite color approaches another. This value has
     * to be above 0. It can be set to {@link #COLOR_MAX} for ensuring that the
     * color is always set to its target right away.
     */
    int APPROACH_SPEED = 20;

    /**
     * Maximal value of the color values. Its possible to raise the color value
     * above this value to increase the values of the color share at the output
     * display.
     */
    int COLOR_MAX = ((1 << Byte.SIZE) - 1);

    /**
     * Minimal value of the color values.
     */
    int COLOR_MIN = 0;

    /**
     * Add a value to each color component of this color. The value should be
     * between 0.f and 1.f
     * 
     * @param add the value that is added to the color components
     */
    void add(float add);

    /**
     * Add a different value to each color component of this color. The values
     * should be between 0.f and 1.f
     * 
     * @param addRed the value added to the red share of the color
     * @param addGreen the value added to the green share of the color
     * @param addBlue the value added to the blue share of the color
     */
    void add(float addRed, float addGreen, float addBlue);

    /**
     * Add a value to each color component of this color. The value added should
     * be between 0 and {@link #COLOR_MAX}.
     * 
     * @param add the value that is added to the color components
     */
    void add(int add);

    /**
     * Add a different value to each color component of this color. The values
     * added should be between 0 and {@link #COLOR_MAX}.
     * 
     * @param addRed the value added to the red share of the color
     * @param addGreen the value added to the green share of the color
     * @param addBlue the value added to the blue share of the color
     */
    void add(int addRed, int addGreen, int addBlue);

    /**
     * Add the values of another color to this color. The alpha value is not
     * changed.
     * 
     * @param addColor the color that is added to this color
     */
    void add(SpriteColor addColor);

    /**
     * Add a value to the alpha value of this color. The value should be between
     * 0.f and 1.f
     * 
     * @param add the value that is added to the alpha value
     */
    void addAlpha(float add);

    /**
     * Add a value to the alpha value of this color. The value added should be
     * between 0 and {@link #COLOR_MAX}.
     * 
     * @param add the value that is added to the alpha value
     */
    void addAlpha(int add);

    /**
     * Approach the target color with this color values. So change the color
     * values of this color slowly towards the target color.
     * 
     * @param target the target color
     * @return true in case the color reached the target color, false if the
     *         approaching is not done.
     */
    boolean approach(SpriteColor target);

    /**
     * Create a clone that is a exact copy of this sprite color.
     * 
     * @return the new instance of the sprite color that holds the same values
     *         as this one
     * @throws CloneNotSupportedException in case its not possible to clone that
     *             object
     */
    SpriteColor clone() throws CloneNotSupportedException;

    /**
     * Get the alpha value of this color. The value will be between 0.f and 1.f
     * 
     * @return the alpha value of this color
     */
    float getAlphaf();

    /**
     * Get the alpha value of this color. The value will be between 0 and
     * {@link #COLOR_MAX}.
     * 
     * @return the alpha value of this color
     */
    int getAlphai();

    /**
     * Get the blue share of this color. The value will be between 0.f and 1.f
     * 
     * @return the blue share of this color
     */
    float getBluef();

    /**
     * Get the blue share of this color. The value will be between 0 and
     * {@link #COLOR_MAX}.
     * 
     * @return the blue share of this color
     */
    int getBluei();

    /**
     * Get the green share of this color. The value will be between 0.f and 1.f
     * 
     * @return the green share of this color
     */
    float getGreenf();

    /**
     * Get the green share of this color. The value will be between 0 and
     * {@link #COLOR_MAX}.
     * 
     * @return the green share of this color
     */
    int getGreeni();

    /**
     * Get the illumination of the color. The value will be between 0.f for
     * darkness and 1.f for bright light.
     * 
     * @return the calculated illumination
     */
    float getLuminationf();

    /**
     * Get the illumination of the color. The value will be between 0 for
     * darkness and {@link #COLOR_MAX} for bright light.
     * 
     * @return the calculated illumination
     */
    int getLuminationi();

    /**
     * Get the red share of this color. The value will be between 0.f and 1.f
     * 
     * @return the red share of this color
     */
    float getRedf();

    /**
     * Get the red share of this color. The value will be between 0 and
     * {@link #COLOR_MAX}.
     * 
     * @return the red share of this color
     */
    int getRedi();

    /**
     * Invert all color components of the color. The alpha value remains
     * untouched.
     */
    void invert();

    /**
     * Multiply the color shares by a modifier. All color shares are multiplied
     * with the same modifier.
     * 
     * @param mod the modifier the color shares are multiplied with
     */
    void multiply(float mod);

    /**
     * Multiply each color share with a different modifier.
     * 
     * @param modRed the modifier the red share is multiplied with
     * @param modGreen the modifier the green share is multiplied with
     * @param modBlue the modifier the blue share is multiplied with
     */
    void multiply(float modRed, float modGreen, float modBlue);

    /**
     * Multiply the Alpha value of this color with a modifier.
     * 
     * @param mod the modifier the alpha value is multiplied with
     */
    void multiplyAlpha(float mod);

    /**
     * Reset the color values back to the default values. Means black and fully
     * opaque.
     */
    void resetColor();

    /**
     * Set all color components of this color to the same value. The value
     * should be between 0.f and 1.f
     * 
     * @param value the value all color components shall get
     */
    void set(float value);

    /**
     * Set each color component to a value. The values should be between 0.f and
     * 1.f
     * 
     * @param setRed the new red value of this color
     * @param setGreen the new green value of this color
     * @param setBlue the new blue value of this color
     */
    void set(float setRed, float setGreen, float setBlue);

    /**
     * Set all color components of this color to the same value. The value
     * should be between 0 and {@link #COLOR_MAX}.
     * 
     * @param value the value all color components shall get
     */
    void set(int value);

    /**
     * Set each color component to a value. The values should be between 0 and
     * {@link #COLOR_MAX}.
     * 
     * @param setRed the new red value of this color
     * @param setGreen the new green value of this color
     * @param setBlue the new blue value of this color
     */
    void set(int setRed, int setGreen, int setBlue);

    /**
     * Set the color values of another color to this color. The alpha value is
     * not changed.
     * 
     * @param color the color that color values are used
     */
    void set(SpriteColor color);

    /**
     * Set the color as color that is used at the next render actions.
     */
    void setActiveColor();

    /**
     * Set the alpha value of this color to a value. The value should be between
     * 0.f and 1.f
     * 
     * @param newAlpha the new value for the alpha of this color
     */
    void setAlpha(float newAlpha);

    /**
     * Set the alpha value of this color to a value. The value should be between
     * 0 and {@link #COLOR_MAX}.
     * 
     * @param newAlpha the new value for the alpha of this color
     */
    void setAlpha(int newAlpha);

    /**
     * Set the blue component of this color to a value. The value should be
     * between 0.f and 1.f
     * 
     * @param newBlue the new value of the blue share of this color
     */
    void setBlue(float newBlue);

    /**
     * Set the blue component of this color to a value. The value should be
     * between 0 and {@link #COLOR_MAX}.
     * 
     * @param newBlue the new value of the blue share of this color
     */
    void setBlue(int newBlue);

    /**
     * Set the green component of this color to a value. The value should be
     * between 0.f and 1.f
     * 
     * @param newGreen the new value of the green share of this color
     */
    void setGreen(float newGreen);

    /**
     * Set the green component of this color to a value. The value should be
     * between 0 and {@link #COLOR_MAX}.
     * 
     * @param newGreen the new value of the green share of this color
     */
    void setGreen(int newGreen);

    /**
     * Set the red component of this color to a value. The value should be
     * between 0.f and 1.f
     * 
     * @param newRed the new value of the red share of this color
     */
    void setRed(float newRed);

    /**
     * Set the red component of this color to a value. The value should be
     * between 0 and {@link #COLOR_MAX}.
     * 
     * @param newRed the new value of the red share of this color
     */
    void setRed(int newRed);

    /**
     * Subtract a value to each color component of this color. The value should
     * be between 0.f and 1.f
     * 
     * @param sub the value that is subtracted to the color components
     */
    void sub(float sub);

    /**
     * Subtract a different value to each color component of this color. The
     * values should be between 0.f and 1.f
     * 
     * @param subRed the value subtracted to the red share of the color
     * @param subGreen the value subtracted to the green share of the color
     * @param subBlue the value subtracted to the blue share of the color
     */
    void sub(float subRed, float subGreen, float subBlue);

    /**
     * Subtract a value to each color component of this color. The value
     * subtracted should be between 0 and {@link #COLOR_MAX}.
     * 
     * @param sub the value that is subtracted to the color components
     */
    void sub(int sub);

    /**
     * Subtract a different value to each color component of this color. The
     * values subtracted should be between 0 and {@link #COLOR_MAX}.
     * 
     * @param subRed the value subtracted to the red share of the color
     * @param subGreen the value subtracted to the green share of the color
     * @param subBlue the value subtracted to the blue share of the color
     */
    void sub(int subRed, int subGreen, int subBlue);

    /**
     * Subtract the values of another color to this color. The alpha value is
     * not changed.
     * 
     * @param subColor the color that is subtracted to this color
     */
    void sub(SpriteColor subColor);

    /**
     * Subtract a value to the alpha value of this color. The value should be
     * between 0.f and 1.f
     * 
     * @param sub the value that is subtracted to the alpha value
     */
    void subAlpha(float sub);

    /**
     * Subtract a value to the alpha value of this color. The value subtracted
     * should be between 0 and {@link #COLOR_MAX}.
     * 
     * @param sub the value that is subtracted to the alpha value
     */
    void subAlpha(int sub);
}
