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

import illarion.common.util.FastMath;
import org.illarion.engine.graphic.Color;

import javax.annotation.Nonnull;

/**
 * This is a utility class that provides a few static functions that are handy
 * when handling animations.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class AnimationUtility {
    /**
     * The time the delta time is divided by. This result of the division is
     * multiplied with the difference of the values in order to slow the
     * animation down or speed it up based on the time since the last render
     * action.
     */
    public static final int DELTA_DIV = 50;

    /**
     * The default value for the factor of the approaching functions.
     *
     * @see #approach(int, int, int, int, int)
     */
    private static final int DEFAULT_APPROACH = 4;

    /**
     * The minimal difference value that is allowed for the the float
     * translation.
     *
     * @see #translate(float, float, float, int)
     * @see #translate(float, float, float, float, float, int)
     */
    private static final float MIN_FLOAT_DIFF = 0.01f;
    /**
     * The minimal value of the difference that is still not compensated by the
     * delta time value at the approach and the translate functions. If the
     * difference between the value and the target value is larger the delta
     * value is used to reduce or increase the approaching speed.
     *
     * @see #approach(int, int, int, int, int)
     * @see #approach(int, int, int, int, int, int)
     * @see #translateAlpha(int, int, int, int)
     * @see #translate(int, int, int, int, int, int)
     * @see #translate(float, float, float, int)
     * @see #translate(float, float, float, float, float, int)
     */
    private static final int MIN_INT_DIFF = 3;

    /**
     * Private constructor so no instance of this object is created.
     */
    private AnimationUtility() {
        // nothing to do
    }

    /**
     * Approach two color values smoothly.
     *
     * @param workingColor the color that is changed
     * @param targetColor the target color value
     * @param delta the time since the last update
     * @return {@code true} in case the colors got changed
     */
    public static boolean approach(
            @Nonnull Color workingColor,
            @Nonnull Color targetColor,
            int delta) {
        if (workingColor.equals(targetColor)) {
            return false;
        }
        workingColor.setRed(approach(workingColor.getRed(), targetColor.getRed(), 0, Color.MAX_INT_VALUE, delta));
        workingColor.setGreen(approach(workingColor.getGreen(), targetColor.getGreen(), 0, Color.MAX_INT_VALUE, delta));
        workingColor.setBlue(approach(workingColor.getBlue(), targetColor.getBlue(), 0, Color.MAX_INT_VALUE, delta));
        workingColor.setAlpha(approach(workingColor.getAlpha(), targetColor.getAlpha(), 0, Color.MAX_INT_VALUE, delta));

        return true;
    }

    /**
     * Let a value quickly approach a target value. This function ensures that
     * the value stays within the limits.
     *
     * @param value the current value that shall approach the target value
     * @param target the target of the approaching operation
     * @param min the lower border of the approaching operation, the returned
     * value won't be smaller then this value
     * @param max the upper border of the approaching operation, the returned
     * value won't be larger then this value
     * @param delta the delta time in milliseconds since the last update of the
     * value. This value is used to compensate the approaching speed
     * regarding different update speeds. In case this value is 0 the
     * value will change by the smallest value possible
     * @return the new value and so the next step of the approaching operation.
     * Call the function again and again with the new value until is
     * reaches the target value to get the full approach calculation
     */
    public static int approach(int value, int target, int min, int max, int delta) {
        return approach(value, target, DEFAULT_APPROACH, min, max, delta);
    }

    /**
     * Let a value quickly approach a target value. This function ensures that
     * the value stays within the limits.
     *
     * @param value the current value that shall approach the target value
     * @param target the target of the approaching operation
     * @param factor the factor the difference between the target and the
     * current value is divided by. To slow down the approaching
     * speed, make this value larger. This value must not be 0 or
     * smaller
     * @param min the lower border of the approaching operation, the returned
     * value won't be smaller then this value
     * @param max the upper border of the approaching operation, the returned
     * value won't be larger then this value
     * @param delta the delta time in milliseconds since the last update of the
     * value. This value is used to compensate the approaching speed
     * regarding different update speeds. In case this value is 0 the
     * value will change by the smallest value possible
     * @return the new value and so the next step of the approaching operation.
     * Call the function again and again with the new value until is
     * reaches the target value to get the full approach calculation
     */
    public static int approach(
            int value,
            int target,
            int factor,
            int min,
            int max,
            int delta) {
        int diff = target - value;
        if (diff == 0) {
            return value;
        }

        int dir = FastMath.sign(diff);
        int absDiff = FastMath.abs(diff) / factor;

        if (absDiff > MIN_INT_DIFF) {
            absDiff = FastMath.clamp((absDiff * delta) / DELTA_DIV, 1, FastMath.abs(diff));
        } else {
            absDiff = FastMath.clamp(absDiff, 1, MIN_INT_DIFF);
        }

        int newValue = value + (absDiff * dir);
        return FastMath.clamp(newValue, min, max);
    }

    /**
     * Translate a float value linear towards a target value.
     *
     * @param value the current value that shall be translated
     * @param target the target value the current value shall be translated
     * towards
     * @param step the minimal step of the translation, so the maximal value the
     * value is changed by at one step
     * @param min the bottom border of the value, the value won't be smaller
     * then this value
     * @param max the top border of the value, the value won't be larger then
     * this value
     * @param delta the time in milliseconds since the last call of this
     * function. This value is used to compensate the change of the
     * value to ensure that the translation looks the same no matter
     * of the update speed
     * @return the new value that is one step closer to the translation target
     */
    public static float translate(
            float value,
            float target,
            float step,
            float min,
            float max,
            int delta) {
        float diff = (target - value);
        if (diff != 0) {
            int dir = (int) (diff / FastMath.abs(diff));
            diff = FastMath.abs(diff);
            if (diff <= step) {
                return target;
            }

            diff = step;
            if (diff > MIN_FLOAT_DIFF) {
                diff = (diff * delta) / DELTA_DIV;
                if (diff < MIN_FLOAT_DIFF) {
                    diff = MIN_FLOAT_DIFF;
                }
            }
            float newValue = value + (diff * dir);

            // clamp value against limits
            if (newValue > max) {
                return max;
            } else if (newValue < min) {
                return min;
            } else {
                return newValue;
            }
        }
        return value;
    }

    /**
     * Translate a float value linear between 0 and 1. The result value will be
     * always between 0 and 1.
     *
     * @param value the current value that shall be translated
     * @param target the target value the current value shall be translated
     * towards
     * @param step the minimal step of the translation, so the maximal value the
     * value is changed by at one step
     * @param delta the time in milliseconds since the last call of this
     * function. This value is used to compensate the change of the
     * value to ensure that the translation looks the same no matter
     * of the update speed
     * @return the new value that is one step closer to the translation target
     */
    public static float translate(float value, float target, float step, int delta) {
        return translate(value, target, step, 0, 1, delta);
    }

    /**
     * Translate a value linear towards a target value. This function ensures
     * also that the value stays within the border values.
     *
     * @param value the current value that shall be translated towards the
     * target value
     * @param target the target of the translation
     * @param step the step value that says by how many points the current value
     * shall change each step
     * @param min the bottom border of the value. The value won't be lower then
     * this border value
     * @param max the upper border of the value. The value won't be larger then
     * this border value
     * @param delta the time in milliseconds since the last call of this
     * function. This value is used to compensate the change of the
     * value to ensure that the translation looks the same no matter
     * of the update speed
     * @return the new value that is one step closer to the translation target
     */
    public static int translate(
            int value,
            int target,
            int step,
            int min,
            int max,
            int delta) {
        int diff = (target - value);
        if (diff != 0) {
            int dir = FastMath.sign(diff);
            diff = FastMath.abs(diff);
            if (diff < step) {
                return target;
            }

            diff = step;
            if (diff > MIN_INT_DIFF) {
                diff = (diff * delta) / DELTA_DIV;
                if (diff == 0) {
                    diff = 1;
                }
            }
            int newValue = value + (diff * dir);

            // clamp value against limits
            if ((dir == 1) && (newValue > target)) {
                return target;
            } else if ((dir == -1) && (newValue < target)) {
                return target;
            } else if (newValue > max) {
                return max;
            } else if (newValue < min) {
                return min;
            } else {
                return newValue;
            }
        }
        return value;
    }

    /**
     * Translate a value linear optimized for alpha values and colors. The
     * borders of this animation are the minimal and maximal integer values for
     * colors set by sprite color.
     *
     * @param value the current value that shall be translated towards the
     * target value
     * @param target the target of the translation
     * @param step the step value that says by how many points the current value
     * shall change each step
     * @param delta the time in milliseconds since the last call of this
     * function. This value is used to compensate the change of the
     * value to ensure that the translation looks the same no matter
     * of the update speed
     * @return the new value that is one step closer to the translation target
     */
    public static int translateAlpha(int value, int target, int step, int delta) {
        return translate(value, target, step, 0, 255, delta);
    }
}
