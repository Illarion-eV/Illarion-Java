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
     * @param red   the red color component
     * @param green the green color component
     * @param blue  the blue color component
     * @param alpha the alpha color component
     */
    public ImmutableColor(final int red, final int green, final int blue, final int alpha) {
        super(red, green, blue, alpha);
    }

    /**
     * Create a new color instance with the specified values.
     *
     * @param red   the red color component
     * @param green the green color component
     * @param blue  the blue color component
     * @param alpha the alpha color component
     */
    public ImmutableColor(final float red, final float green, final float blue, final float alpha) {
        super(red, green, blue, alpha);
    }

    /**
     * Create a new opaque color instance with the specified values.
     *
     * @param red   the red color component
     * @param green the green color component
     * @param blue  the blue color component
     */
    public ImmutableColor(final int red, final int green, final int blue) {
        super(red, green, blue);
    }

    /**
     * Create a new opaque color instance with the specified values.
     *
     * @param red   the red color component
     * @param green the green color component
     * @param blue  the blue color component
     */
    public ImmutableColor(final float red, final float green, final float blue) {
        super(red, green, blue);
    }

    /**
     * Copy the values of another instance of the color into a new color instance.
     *
     * @param org the original color value that is the data provider
     */
    public ImmutableColor(@Nonnull final Color org) {
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
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setAlpha(final int alpha) {
        throwImmutableException();
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setAlphaf(final float alpha) {
        throwImmutableException();
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setBlue(final int blue) {
        throwImmutableException();
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setBluef(final float blue) {
        throwImmutableException();
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setGreen(final int green) {
        throwImmutableException();
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setGreenf(final float green) {
        throwImmutableException();
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setRed(final int red) {
        throwImmutableException();
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setRedf(final float red) {
        throwImmutableException();
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void setColor(@Nonnull final Color org) {
        throwImmutableException();
    }


    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException in any case because changes are not allowed on this immutable class
     */
    @Override
    public void multiply(@Nonnull final Color mul) {
        throwImmutableException();
    }
}
