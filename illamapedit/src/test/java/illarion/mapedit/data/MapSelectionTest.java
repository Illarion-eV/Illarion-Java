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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author Volker Jung
 */
public class MapSelectionTest {

    private MapSelection mapSelection;
    private MapTile tile;

    @BeforeMethod
    public void before() {
        mapSelection = new MapSelection();
        tile = createTile();
    }

    @Test
    public void testInitialState() {
        Collection<MapPosition> selectedPositions = mapSelection.getSelectedPositions();

        Assert.assertNotNull(selectedPositions, "Collection of selected positions must not be null");
        Assert.assertEquals(selectedPositions.size(), 0, "No position should be selected");
        Assert.assertEquals(mapSelection.getOffsetX(), Integer.MAX_VALUE, "Expected offset equal to max. integer value");
        Assert.assertEquals(mapSelection.getOffsetY(), Integer.MAX_VALUE, "Expected offset equal to max. integer value");
    }

    @Test
    public void testAddSelectedTile() {
        int horizontalPosition = 5;
        int verticalPosition = 7;
        addSelectedTileAt(horizontalPosition, verticalPosition);

        Assert.assertEquals(mapSelection.getSelectedPositions().size(), 1);
        Assert.assertEquals(mapSelection.getOffsetX(), horizontalPosition);
        Assert.assertEquals(mapSelection.getOffsetY(), verticalPosition);
    }

    @Test
    public void testSelectionVerticalOffsetAdjustment() {
        addSelectedTileAt(5, 7);
        addSelectedTileAt(6, 4);

        Assert.assertEquals(mapSelection.getOffsetX(), 5);
        Assert.assertEquals(mapSelection.getOffsetY(), 4);
        Assert.assertEquals(mapSelection.getSelectedPositions().size(), 2);
    }

    @Test
    public void testSelectionHorizontalOffsetAdjustment() {
        addSelectedTileAt(7, 5);
        addSelectedTileAt(4, 6);

        Assert.assertEquals(mapSelection.getOffsetX(), 4);
        Assert.assertEquals(mapSelection.getOffsetY(), 5);
        Assert.assertEquals(mapSelection.getSelectedPositions().size(), 2);
    }

    @Test
    public void testSelectionFullOffsetAdjustment() {
        addSelectedTileAt(7, 5);
        addSelectedTileAt(4, 6);
        addSelectedTileAt(8, 3);

        Assert.assertEquals(mapSelection.getOffsetX(), 4);
        Assert.assertEquals(mapSelection.getOffsetY(), 3);
        Assert.assertEquals(mapSelection.getSelectedPositions().size(), 3);
    }

    private MapTile createTile() {
        return new MapTile(0, 0, 0, 0, null, null);
    }

    private void addSelectedTileAt(int x, int y) {
        mapSelection.addSelectedTile(new MapPosition(x, y), tile);
    }

}
