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
package illarion.client.graphics;

import illarion.common.types.DisplayCoordinate;
import org.illarion.engine.graphic.Sprite;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This fading corridor is used to fade out the objects that would block the
 * view on the player character.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
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
    @Nonnull
    public static FadingCorridor getInstance() {
        return INSTANCE;
    }

    /**
     * Test given screen coordinates for intersection with transparent corridor.
     *
     * @param testLocX the x location of the area that needs to be checked for
     * intersection
     * @param testLocY the y location of the area that needs to be checked for
     * intersection
     * @param testLayer the layer of the area that needs to be checked, that
     * generally is the Z order and it ensures that objects that are
     * "below" the avatar are not faded out
     * @param width the width of the area that needs to be checked for
     * intersection
     * @param height the height of the area that needs to be checked for
     * intersection
     * @return {@code true} in case the area intersects and is "above" the
     * avatar image (spoken in z order), so returning {@code true}
     * means that the tested object needs to be faded out
     */
    boolean isInCorridor(
            int testLocX, int testLocY, int testLayer, int width, int height) {
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
     * corridor
     */
    void setCorridor(@Nonnull AbstractEntity<?> character) {
        Sprite characterSprite = character.getTemplate().getSprite();
        fading.setBounds(character.getDisplayCoordinate().getX() + TOLERANCE,
                (character.getDisplayCoordinate().getY() + TOLERANCE) - characterSprite.getHeight(),
                         characterSprite.getWidth(), characterSprite.getHeight());
        back = character.getOrder() - (DisplayCoordinate.ROW_DISTANCE / 2);
    }
}
