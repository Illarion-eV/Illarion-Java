/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import java.io.Serializable;

import javolution.context.ObjectFactory;

/**
 * Unlike the java implementation this vector is a mathematical 2D vector. It
 * can be used to calculate proper positions on the screen.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Vector implements Reusable, Serializable {
    /**
     * This is the factory class that is used to buffer and reuse the vectors
     * that are created.
     * 
     * @author Martin Karing
     * @since 1.22
     * @version 1.22
     */
    private static final class VectorFactory extends ObjectFactory<Vector> {
        /**
         * Public constructor to allow the parent class to create a proper
         * instance.
         */
        public VectorFactory() {
            super();
        }

        /**
         * Create a new instance of the vector class.
         * 
         * @return the new vector instance
         */
        @Override
        protected Vector create() {
            return new Vector();
        }
    }

    /**
     * The factory used to buffer and reuse the class instances.
     */
    private static final ObjectFactory<Vector> FACTORY = new VectorFactory();

    /**
     * The serialization UID of this vector.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The x coordinate of the origin of this vector.
     */
    private int originX;

    /**
     * The y coordinate of the origin of this vector.
     */
    private int originY;

    /**
     * The x coordinate of the target of this vector.
     */
    private int targetX;

    /**
     * The y coordinate of the target of this vector.
     */
    private int targetY;

    /**
     * Private constructor to ensure only one vector to be created.
     */
    Vector() {
        reset();
    }

    /**
     * Get a instance from this vector class that is currently not in use.
     * 
     * @return the unused vector instance
     */
    public static Vector getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the x coordinate of the origin of this vector.
     * 
     * @return the x coordinate of the origin
     */
    public int getOriginX() {
        return originX;
    }

    /**
     * Get the y coordinate of the origin of this vector.
     * 
     * @return the y coordinate of the origin
     */
    public int getOriginY() {
        return originY;
    }

    /**
     * Get the x coordinate of the target of this vector.
     * 
     * @return the x coordinate of the target
     */
    public int getTargetX() {
        return targetX;
    }

    /**
     * Get the y coordinate of the target of this vector.
     * 
     * @return the y coordinate of the target
     */
    public int getTargetY() {
        return targetY;
    }

    /**
     * Get the x coordinate on the vector for a specified y value.
     * 
     * @param y the y coordinate the fitting x coordinate is needed for
     * @return the x coordinate on the vector
     */
    public int getXOnVector(final int y) {
        return FastMath
            .round(((float) (y - originY) / (float) (targetY - originY))
                * (targetX - originX))
            + originX;
    }

    /**
     * Get the y coordinate on the vector for a specified x value.
     * 
     * @param x the x coordinate the fitting y coordinate is needed for
     * @return the y coordinate on the vector
     */
    public int getYOnVector(final int x) {
        return FastMath
            .round(((float) (x - originX) / (float) (targetX - originX))
                * (targetY - originY))
            + originY;
    }

    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    @Override
    public void reset() {
        originX = 0;
        originY = 0;
        targetX = 0;
        targetY = 0;
    }

    /**
     * Set the origin coordinates for this vector.
     * 
     * @param x the x coordinate of the origin of the vector
     * @param y the y coordinate of the origin of the vector
     */
    public void setOrigin(final int x, final int y) {
        originX = x;
        originY = y;
    }

    /**
     * Set the target coordinates of this vector.
     * 
     * @param x the x coordinate of the target of the vector
     * @param y the y coordinate of the target of the vector
     */
    public void setTarget(final int x, final int y) {
        targetX = x;
        targetY = y;
    }
}
