package illarion.mapedit.data;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This is a special iterator implementation that allows to iterate over all tiles of a map in a swift way.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MapIterator implements Iterator<MapTile> {
    private int currentIndex = -1;
    private final int tileCount;
    private final Map parentMap;

    MapIterator(@Nonnull final Map map, final int tileCount) {
        parentMap = map;
        this.tileCount = tileCount;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < (tileCount - 1);
    }

    @Override
    public MapTile next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return parentMap.getTileAtIndex(++currentIndex);
    }

    public int getCurrentX() {
        if (currentIndex == -1) {
            throw new IllegalStateException("Can't request coordinate while next() was never called.");
        }
        return parentMap.indexToMapX(currentIndex);
    }

    public int getCurrentY() {
        if (currentIndex == -1) {
            throw new IllegalStateException("Can't request coordinate while next() was never called.");
        }
        return parentMap.indexToMapY(currentIndex);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Removing tiles is not supported by this iterator");
    }
}
