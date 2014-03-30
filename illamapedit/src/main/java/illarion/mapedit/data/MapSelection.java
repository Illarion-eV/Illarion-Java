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
        selectedTiles = new HashMap<>();
    }

    public void addSelectedTile(@Nonnull final MapPosition mapPosition, final MapTile tile) {
        if (!selectedTiles.containsKey(mapPosition)) {
            adjustOffsets(mapPosition);
            selectedTiles.put(mapPosition, tile);
        }
    }

    private void adjustOffsets(MapPosition mapPosition) {
        adjustHorizontalOffset(mapPosition.getX());
        adjustVerticalOffset(mapPosition.getY());
    }

    private void adjustHorizontalOffset(int horizontalCoordinate) {
        minX = min(minX, horizontalCoordinate);
    }

    private void adjustVerticalOffset(int verticalCoordinate) {
        minY = min(minY, verticalCoordinate);
    }

    private int min(int currentMinimum, int candidateMinimum) {
        return Math.min(currentMinimum, candidateMinimum);
    }

    public int getOffsetX() {
        return minX;
    }

    public int getOffsetY() {
        return minY;
    }

    @Nonnull
    public HashMap<MapPosition, MapTile> getTiles() {
        return selectedTiles;
    }
}
