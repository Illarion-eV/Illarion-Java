package illarion.mapedit.data;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
        HashMap<MapPosition,MapTile> tiles = mapSelection.getTiles();

        Assert.assertNotNull(tiles, "Tiles must not be null");
        Assert.assertEquals(tiles.size(), 0, "tiles should be empty");
        Assert.assertEquals(mapSelection.getOffsetX(), Integer.MAX_VALUE, "Expected offset equal to max. integer value");
        Assert.assertEquals(mapSelection.getOffsetY(), Integer.MAX_VALUE, "Expected offset equal to max. integer value");
    }

    @Test
    public void testAddSelectedTile() {
        int horizontalPosition = 5;
        int verticalPosition = 7;
        addSelectedTileAt(horizontalPosition, verticalPosition);

        Assert.assertEquals(mapSelection.getTiles().size(), 1);
        Assert.assertEquals(mapSelection.getOffsetX(), horizontalPosition);
        Assert.assertEquals(mapSelection.getOffsetY(), verticalPosition);
    }

    @Test
    public void testSelectionVerticalOffsetAdjustment() {
        addSelectedTileAt(5, 7);
        addSelectedTileAt(6, 4);

        Assert.assertEquals(mapSelection.getOffsetX(), 5);
        Assert.assertEquals(mapSelection.getOffsetY(), 4);
        Assert.assertEquals(mapSelection.getTiles().size(), 2);
    }

    @Test
    public void testSelectionHorizontalOffsetAdjustment() {
        addSelectedTileAt(7, 5);
        addSelectedTileAt(4, 6);

        Assert.assertEquals(mapSelection.getOffsetX(), 4);
        Assert.assertEquals(mapSelection.getOffsetY(), 5);
        Assert.assertEquals(mapSelection.getTiles().size(), 2);
    }

    @Test
    public void testSelectionFullOffsetAdjustment() {
        addSelectedTileAt(7, 5);
        addSelectedTileAt(4, 6);
        addSelectedTileAt(8, 3);

        Assert.assertEquals(mapSelection.getOffsetX(), 4);
        Assert.assertEquals(mapSelection.getOffsetY(), 3);
        Assert.assertEquals(mapSelection.getTiles().size(), 3);
    }

    private MapTile createTile() {
        return new MapTile(0, 0, 0, 0, null, null);
    }

    private void addSelectedTileAt(int x, int y) {
        mapSelection.addSelectedTile(new MapPosition(x, y), tile);
    }

}
