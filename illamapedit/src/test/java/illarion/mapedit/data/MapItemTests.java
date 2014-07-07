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
package illarion.mapedit.data;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Fredrik K
 */
public class MapItemTests {

    @Test
    public void newInstanceTest() {
        MapItem testItem = new MapItem(1, null, 333);
        MapItem sut = new MapItem(testItem);
        Assert.assertNull(sut.getItemData());
        Assert.assertEquals(1, sut.getId());
        Assert.assertEquals(333, sut.getQualityDurability());
    }

    @Test
    public void testQualityDurability() {
        MapItem sut = new MapItem(1, null, 123);

        Assert.assertEquals(1, sut.getQuality());
        Assert.assertEquals(23, sut.getDurability());
    }

    @Test
    public void testSettingDurability() {
        MapItem sut = new MapItem(1, null, 111);

        sut.setDurability(33);

        Assert.assertEquals(133, sut.getQualityDurability());
    }

    @Test
    public void testSettingQuality() {
        MapItem sut = new MapItem(1, null, 111);

        sut.setQuality(3);

        Assert.assertEquals(311, sut.getQualityDurability());
    }
}
