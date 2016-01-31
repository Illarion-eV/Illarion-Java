/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * This is a special version of the color class that is immutable.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public class ImmutableColor extends Color {
    /**
     * Create a new color instance with the specified values.
     *
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     * @param alpha the alpha color component
     */
    public ImmutableColor(int red, int green, int blue, int alpha) {
        super(red, green, blue, alpha);
    }

    /**
     * Create a new color instance with the specified values.
     *
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     * @param alpha the alpha color component
     */
    public ImmutableColor(float red, float green, float blue, float alpha) {
        super(red, green, blue, alpha);
    }

    /**
     * Create a new opaque color instance with the specified values.
     *
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     */
    public ImmutableColor(int red, int green, int blue) {
        super(red, green, blue);
    }

    /**
     * Create a new opaque color instance with the specified values.
     *
     * @param red the red color component
     * @param green the green color component
     * @param blue the blue color component
     */
    public ImmutableColor(float red, float green, float blue) {
        super(red, green, blue);
    }

    /**
     * Copy the values of another instance of the color into a new color instance.
     *
     * @param org the original color value that is the data provider
     */
    public ImmutableColor(@Nonnull Color org) {
        super(org);
    }

    /**
     * This function does nothing but throwing the required exception that informs the user about a illegal access to
     * the setter function.
     *
     * @throws UnsupportedOperationException in any case because its the purpose of this function
     */
    private static void throwImmutableException() {
        throw new UnsupportedOperationException("This color instance is immutable. Changes are not legal.");
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void add(@Nonnull Color color) {
        throwImmutableException();
    }

    @Nonnull
    @Override
    public ImmutableColor getImmutableCopy() {
        return this;
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setAlpha(int alpha) {
        throwImmutableException();
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setAlphaf(float fAlpha) {
        throwImmutableException();
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setBlue(int blue) {
        throwImmutableException();
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setBluef(float fBlue) {
        throwImmutableException();
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setGreen(int green) {
        throwImmutableException();
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setGreenf(float fGreen) {
        throwImmutableException();
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setRed(int red) {
        throwImmutableException();
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setRedf(float fRed) {
        throwImmutableException();
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setColor(@Nonnull Color org) {
        throwImmutableException();
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void multiply(@Nonnull Color mul) {
        throwImmutableException();
    }

    /**
     *
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void multiply(float value) {
        throwImmutableException();
    }
}
