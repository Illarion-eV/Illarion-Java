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
package illarion.build.imagepacker

import org.testng.annotations.Test

import static org.testng.Assert.*;

/**
 * The test class for the {@link Space} class.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SpaceTest {
    @Test
    void parametersTest() {
        def space = new Space(1, 2, 3, 4);
        assertEquals(space.x, 1, "Applying parameter x failed")
        assertEquals(space.y, 2, "Applying parameter x failed")
        assertEquals(space.height, 3, "Applying parameter x failed")
        assertEquals(space.width, 4, "Applying parameter x failed")
    }

    @Test
    void sizeTest() {
        def space = new Space(1, 2, 3, 4);
        assertEquals(space.size, 12, "Calculation of the size failed.")
    }

    @Test
    void fitInsideTest() {
        def spaces = [new Space(0, 0, 1, 1), new Space(0, 0, 1, 2), new Space(0, 0, 2, 1), new Space(0, 0, 2, 2)]

        assertTrue(spaces[0].isFittingInside(spaces[0]))
        assertFalse(spaces[0].isFittingInside(spaces[1]))
        assertFalse(spaces[0].isFittingInside(spaces[2]))
        assertFalse(spaces[0].isFittingInside(spaces[3]))

        assertTrue(spaces[1].isFittingInside(spaces[0]))
        assertTrue(spaces[1].isFittingInside(spaces[1]))
        assertFalse(spaces[1].isFittingInside(spaces[2]))
        assertFalse(spaces[1].isFittingInside(spaces[3]))

        assertTrue(spaces[2].isFittingInside(spaces[0]))
        assertFalse(spaces[2].isFittingInside(spaces[1]))
        assertTrue(spaces[2].isFittingInside(spaces[2]))
        assertFalse(spaces[2].isFittingInside(spaces[3]))

        assertTrue(spaces[3].isFittingInside(spaces[0]))
        assertTrue(spaces[3].isFittingInside(spaces[1]))
        assertTrue(spaces[3].isFittingInside(spaces[2]))
        assertTrue(spaces[3].isFittingInside(spaces[3]))
    }
}
