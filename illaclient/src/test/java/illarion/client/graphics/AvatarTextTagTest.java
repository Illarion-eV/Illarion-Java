/*
 * This file is part of the Illarion project.
 *
 * Copyright 2026 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */
package illarion.client.graphics;

import illarion.common.types.DisplayCoordinate;
import illarion.common.types.Rectangle;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Font;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import java.lang.reflect.Proxy;

import static org.testng.Assert.assertEquals;

/** Tests label measurement and positioning without a graphics engine or a server. */
public class AvatarTextTagTest {
    private static final int FRAME_TIME = 16;
    private static final int CHARACTER_WIDTH = 7;
    private static final int LINE_HEIGHT = 12;
    private static final GameContainer CONTAINER = (GameContainer) Proxy.newProxyInstance(
            GameContainer.class.getClassLoader(), new Class<?>[]{GameContainer.class}, (proxy, method, arguments) -> {
                throw new AssertionError("Label positioning must not access the game container: " + method.getName());
            });

    private RecordingFont font;
    private AvatarTextTag tag;

    @BeforeMethod
    public void setUp() {
        font = new RecordingFont();
        tag = new AvatarTextTag(font);
        tag.setDisplayLocation(new DisplayCoordinate(100, 200, 0));
        tag.setAvatarHeight(50);
    }

    @Test
    public void unchangedLabelsAreMeasuredOnlyOnce() {
        tag.setCharacterName("Ada");
        tag.setHealthState("Hurt");
        update();

        for (int frame = 0; frame < 10; frame++) {
            update();
        }

        assertMeasurements(2);
        assertBounds(86, 121, 28, 24);
    }

    @Test
    public void unchangedTextSettersPreserveCachedDimensions() {
        tag.setCharacterName("Ada");
        tag.setHealthState("Hurt");
        update();

        tag.setCharacterName(new String("Ada"));
        tag.setHealthState(new String("Hurt"));
        update();

        assertMeasurements(2);
        assertBounds(86, 121, 28, 24);
    }

    @Test
    public void changingNameRecalculatesDimensionsAndCentresTheLabel() {
        tag.setCharacterName("Ada");
        tag.setHealthState("Hurt");
        update();

        tag.setCharacterName("Beatrice");
        update();
        assertBounds(72, 121, 56, 24);
        update();

        assertMeasurements(4);
    }

    @Test
    public void addingChangingAndRemovingHealthUpdatesDimensions() {
        tag.setCharacterName("Ada");
        update();
        assertBounds(90, 133, 21, 12);

        tag.setHealthState("Hurt");
        update();
        assertBounds(86, 121, 28, 24);

        tag.setHealthState("Injured");
        update();
        assertBounds(76, 121, 49, 24);

        tag.setHealthState(null);
        update();
        assertBounds(90, 133, 21, 12);
        tag.setHealthState(null);
        update();

        assertMeasurements(6);
    }

    @Test
    public void movingAnUnchangedLabelUpdatesItsBoundsWithoutMeasuring() {
        tag.setCharacterName("Ada");
        tag.setHealthState("Hurt");
        update();

        tag.setDisplayLocation(new DisplayCoordinate(-40, 80, 0));
        update();

        assertMeasurements(2);
        assertBounds(-54, 1, 28, 24);
    }

    @Test
    public void changingAvatarHeightUpdatesBoundsWithoutMeasuring() {
        tag.setCharacterName("Ada");
        tag.setHealthState("Hurt");
        update();

        tag.setAvatarHeight(75);
        update();

        assertMeasurements(2);
        assertBounds(86, 96, 28, 24);
    }

    @Test
    public void resumingUpdatesAfterHidingUsesTheCurrentPositionAndHeight() {
        tag.setCharacterName("Ada");
        update();

        // Avatar skips label updates while the name is hidden, then supplies its latest position.
        tag.setAvatarHeight(70);
        tag.setDisplayLocation(new DisplayCoordinate(300, 400, 0));
        update();

        assertMeasurements(1);
        assertBounds(290, 313, 21, 12);
    }

    @Test
    public void textChangesWhileHiddenAreMeasuredOnTheNextUpdate() {
        tag.setCharacterName("Ada");
        update();

        tag.setCharacterName("Beatrice");
        tag.setHealthState("Hurt");
        tag.setHealthState("Injured");
        tag.setDisplayLocation(new DisplayCoordinate(300, 400, 0));
        update();
        update();

        assertMeasurements(3);
        assertBounds(272, 321, 56, 24);
    }

    @Test
    public void missingPositionDefersMeasurementUntilTheFirstPositionArrives() {
        tag = new AvatarTextTag(font);
        tag.setCharacterName("Ada");
        update();
        assertMeasurements(0);

        tag.setDisplayLocation(new DisplayCoordinate(100, 200, 0));
        update();
        update();

        assertMeasurements(1);
        assertBounds(90, 183, 21, 12);
    }

    @Test
    public void healthOnlyLabelCanBecomeEmpty() {
        tag.setHealthState("Hurt");
        update();
        assertBounds(86, 133, 28, 12);

        tag.setHealthState(null);
        update();
        update();

        assertMeasurements(1);
        assertBounds(100, 145, 0, 0);
    }

    @Test
    public void emptyNameRetainsItsLineHeightWithoutRepeatedMeasurement() {
        tag.setCharacterName("");
        update();
        update();

        assertMeasurements(1);
        assertBounds(100, 133, 0, 12);
    }

    private void update() {
        tag.update(CONTAINER, FRAME_TIME);
    }

    private void assertMeasurements(int expected) {
        assertEquals(font.widthMeasurements, expected, "Text width measurements");
        assertEquals(font.heightMeasurements, expected, "Line height measurements");
    }

    private void assertBounds(int x, int y, int width, int height) {
        Rectangle bounds = tag.getDisplayRect();
        assertEquals(bounds.getX(), x, "Label x position");
        assertEquals(bounds.getY(), y, "Label y position");
        assertEquals(bounds.getWidth(), width, "Rectangle width");
        assertEquals(bounds.getHeight(), height, "Rectangle height");
        assertEquals(tag.getWidth(), width, "Label width");
        assertEquals(tag.getHeight(), height, "Label height");
    }

    private static final class RecordingFont implements Font {
        private int widthMeasurements;
        private int heightMeasurements;

        @Override
        public int getWidth(@Nonnull CharSequence text) {
            widthMeasurements++;
            return text.length() * CHARACTER_WIDTH;
        }

        @Override
        public int getLineHeight() {
            heightMeasurements++;
            return LINE_HEIGHT;
        }

        @Override
        public int getAdvance(char current, char next) {
            throw new AssertionError("Label measurement must use the font's complete text width");
        }

        @Override
        public void dispose() {
            throw new AssertionError("The label does not own the shared font");
        }
    }
}
