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
package illarion.graphics.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.glsl.fixedfunc.FixedFuncUtil;

import illarion.graphics.SpriteColor;
import illarion.graphics.generic.AbstractSpriteColor;

/**
 * Default class to handle colors inside the client.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class SpriteColorJOGL extends AbstractSpriteColor {
    /**
     * Stores the color that was activated latest in order to prevent that the
     * same color is activated again and again.
     */
    private final static SpriteColorJOGL ACTIVE = new SpriteColorJOGL();

    /**
     * Constructor that creates a instance with default color. Means black and
     * fully opaque.
     */
    public SpriteColorJOGL() {
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
    SpriteColorJOGL(final float newRed, final float newGreen,
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
    SpriteColorJOGL(final float newRed, final float newGreen,
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
    SpriteColorJOGL(final int newRed, final int newGreen, final int newBlue) {
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
    SpriteColorJOGL(final int newRed, final int newGreen, final int newBlue,
        final int newAlpha) {
        super(newRed, newGreen, newBlue, newAlpha);
    }

    /**
     * Copy constructor, create a new color that is a exact copy of another
     * color.
     * 
     * @param org the original color that is copied to the new instance
     */
    SpriteColorJOGL(final SpriteColor org) {
        super((SpriteColorJOGL) org);
    }

    @Override
    public SpriteColorJOGL clone() throws CloneNotSupportedException {
        return (SpriteColorJOGL) super.clone();
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

        if (!(o instanceof SpriteColorJOGL)) {
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

        final GL gl = GLU.getCurrentGL();

        if (!gl.isGL2ES1() && !gl.hasGLSL()) {
            return;
        }

        final GL2ES1 gl2;
        if (gl.hasGLSL()) {
            gl2 = FixedFuncUtil.getFixedFuncImpl(gl);
        } else {
            gl2 = gl.getGL2ES1();
        }
        gl2.glColor4f(getRedf(), getGreenf(), getBluef(), getAlphaf());
    }
}
