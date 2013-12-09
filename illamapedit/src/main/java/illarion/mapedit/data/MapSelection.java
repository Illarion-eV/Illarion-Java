package illarion.mapedit.data;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * This class represents the selected tile in a map
 *
 * @author Fredrik K
 */
public class MapSelection {

    @Nonnull
    private final HashMap<MapPosition, MapTile> selectedTiles;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;

    public MapSelection() {
        selectedTiles = new HashMap<MapPosition, MapTile>();
    }

    public void addSelectedTile(@Nonnull final MapPosition mapPosition, final MapTile tile) {
        if (!selectedTiles.containsKey(mapPosition)) {
            minX = Math.min(minX, mapPosition.getX());
            minY = Math.min(minY, mapPosition.getY());
            selectedTiles.put(mapPosition, tile);
        }
    }

    public int getOffsetX() {
        return minX;
    }

    public int getOffsetY() {
        return minY;
    }

    @Nonnull
    public  HashMap<MapPosition, MapTile> getTiles() {
        return selectedTiles;
    }
}
