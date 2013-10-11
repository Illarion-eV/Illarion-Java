/*
 * This file is part of the build.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The build is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The build is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the build.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build.imagepacker

import org.junit.Assert
import org.junit.Test

/**
 * The test class for the {@link Space} class.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SpaceTest {
    @Test
    void parametersTest() {
        def space = new Space(1, 2, 3, 4);
        Assert.assertEquals("Applying parameter x failed", space.x, 1)
        Assert.assertEquals("Applying parameter x failed", space.y, 2)
        Assert.assertEquals("Applying parameter x failed", space.height, 3)
        Assert.assertEquals("Applying parameter x failed", space.width, 4)
    }

    @Test
    void sizeTest() {
        def space = new Space(1, 2, 3, 4);
        Assert.assertEquals("Calculation of the size failed.", space.size, 12)
    }

    @Test
    void fitInsideTest() {
        def spaces = [new Space(0, 0, 1, 1), new Space(0, 0, 1, 2), new Space(0, 0, 2, 1), new Space(0, 0, 2, 2)]

        Assert.assertTrue(spaces[0].isFittingInside(spaces[0]))
        Assert.assertFalse(spaces[0].isFittingInside(spaces[1]))
        Assert.assertFalse(spaces[0].isFittingInside(spaces[2]))
        Assert.assertFalse(spaces[0].isFittingInside(spaces[3]))

        Assert.assertTrue(spaces[1].isFittingInside(spaces[0]))
        Assert.assertTrue(spaces[1].isFittingInside(spaces[1]))
        Assert.assertFalse(spaces[1].isFittingInside(spaces[2]))
        Assert.assertFalse(spaces[1].isFittingInside(spaces[3]))

        Assert.assertTrue(spaces[2].isFittingInside(spaces[0]))
        Assert.assertFalse(spaces[2].isFittingInside(spaces[1]))
        Assert.assertTrue(spaces[2].isFittingInside(spaces[2]))
        Assert.assertFalse(spaces[2].isFittingInside(spaces[3]))

        Assert.assertTrue(spaces[3].isFittingInside(spaces[0]))
        Assert.assertTrue(spaces[3].isFittingInside(spaces[1]))
        Assert.assertTrue(spaces[3].isFittingInside(spaces[2]))
        Assert.assertTrue(spaces[3].isFittingInside(spaces[3]))
    }
}
