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
package illarion.graphics.lwjgl;

import org.lwjgl.opengl.GL11;

import illarion.graphics.SpriteColor;
import illarion.graphics.generic.AbstractSpriteColor;

/**
 * Default class to handle colors inside the client.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class SpriteColorLWJGL extends AbstractSpriteColor {
    /**
     * Stores the color that was activated latest in order to prevent that the
     * same color is activated again and again.
     */
    private final static SpriteColorLWJGL ACTIVE = new SpriteColorLWJGL();

    /**
     * Constructor that creates a instance with default color. Means black and
     * fully opaque.
     */
    public SpriteColorLWJGL() {
        super();
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
    public SpriteColorLWJGL(final float newRed, final float newGreen,
        final float newBlue) {
        super(newRed, newGreen, newBlue);
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
    public SpriteColorLWJGL(final float newRed, final float newGreen,
        final float newBlue, final float newAlpha) {
        super(newRed, newGreen, newBlue, newAlpha);
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
    public SpriteColorLWJGL(final int newRed, final int newGreen,
        final int newBlue) {
        super(newRed, newGreen, newBlue);
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
    public SpriteColorLWJGL(final int newRed, final int newGreen,
        final int newBlue, final int newAlpha) {
        super(newRed, newGreen, newBlue, newAlpha);
    }

    /**
     * Copy constructor, create a new color that is a exact copy of another
     * color.
     * 
     * @param org the original color that is copied to the new instance
     */
    public SpriteColorLWJGL(final SpriteColor org) {
        super((SpriteColorLWJGL) org);
    }

    @Override
    public SpriteColorLWJGL clone() throws CloneNotSupportedException {
        return (SpriteColorLWJGL) super.clone();
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

        if (!(o instanceof SpriteColorLWJGL)) {
            return false;
        }

        return super.equals(o);
    }

    /**
     * Get the hash code of the color that contains all color shares of this
     * color.
     * 
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Set this color as active open GL color.
     */
    @Override
    public void setActiveColor() {
        if (isDirty()) {
            checkColor();
        }

        if (equals(ACTIVE) && (ACTIVE.getAlphai() == getAlphai())) {
            return;
        }

        ACTIVE.set(this);
        ACTIVE.setAlpha(getAlphai());

        GL11.glColor4f(getRedf(), getGreenf(), getBluef(), getAlphaf());
    }
}
