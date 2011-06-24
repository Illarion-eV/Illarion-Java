/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import java.awt.Rectangle;

import illarion.common.graphics.Layers;

/**
 * This fading corridor is used to fade out the objects that would block the
 * view on the player character.
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.95
 */
public final class FadingCorridor {
    /**
     * The singleton instance of this class.
     */
    private static final FadingCorridor INSTANCE = new FadingCorridor();

    /**
     * The rectangle that is used for some temporary calculation actions.
     */
    private static final Rectangle TEMP_RECT = new Rectangle();

    /**
     * The tolerance around the corridor, so the width the minimal needed area
     * is increased by.
     */
    private static final int TOLERANCE = -2;

    /**
     * The Z layer position of the fading rectangle. Object behind this area do
     * not need to fade out, since the object that shall be be hidden is in
     * front of them anyway.
     */
    private int back;

    /**
     * The rectangle that marks the area where object need to fade out.
     */
    private final Rectangle fading = new Rectangle();

    /**
     * The private constructor is used to ensure that no instances but the
     * singleton instance is created from this class.
     */
    private FadingCorridor() {
        // nothing to do
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static FadingCorridor getInstance() {
        return INSTANCE;
    }

    /**
     * Test given screen coordinates for intersection with transparent corridor.
     * 
     * @param testLocX the x location of the area that needs to be checked for
     *            intersection
     * @param testLocY the y location of the area that needs to be checked for
     *            intersection
     * @param testLayer the layer of the area that needs to be checked, that
     *            generally is the Z order and it ensures that objects that are
     *            "below" the avatar are not faded out
     * @param width the width of the area that needs to be checked for
     *            intersection
     * @param height the height of the area that needs to be checked for
     *            intersection
     * @return <code>true</code> in case the area intersects and is "above" the
     *         avatar image (spoken in z order), so returning <code>true</code>
     *         means that the tested object needs to be faded out
     */
    protected boolean isInCorridor(final int testLocX, final int testLocY,
        final int testLayer, final int width, final int height) {
        TEMP_RECT.setBounds(testLocX, testLocY, width, height);

        return (testLayer < back) && fading.intersects(TEMP_RECT);
    }

    /**
     * Set corridor for the player avatar. After calling this function, this
     * fading corridor will handle the isInCorridor function correctly regarding
     * the current player avatar and its position.
     * <p>
     * Do not forget updating this function if the avatar or the player location
     * changes.
     * </p>
     * 
     * @param character the avatar that delivers the size for the fading
     *            corridor
     */
    protected void setCorridor(final AbstractEntity character) {
        fading.setBounds(character.getDisplayX() + TOLERANCE,
            character.getDisplayY() + TOLERANCE, character.getWidth(),
            character.getHeight());
        back = character.getZOrder() - (Layers.DISTANCE / 2);
    }
}
