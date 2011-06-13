package illarion.mapedit.tools.parts;

import illarion.mapedit.history.HistoryActionTile;
import illarion.mapedit.history.HistoryEntry;
import illarion.mapedit.map.MapStorage;
import illarion.mapedit.map.MapTile;
import illarion.mapedit.map.MapTransitions;

import illarion.common.util.Location;

/**
 * A tile mouse part descripes a tile that is bound to on mouse button and is
 * placed on the map upon clicking it.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class MousePartTile extends AbstractMousePart {
    /**
     * Constructor with default access level so the parts manager is able to
     * recycle this objects correctly.
     */
    MousePartTile() {
        // nothing to do
    }

    /**
     * Get the type of this mouse part.
     * 
     * @return always
     *         {@link illarion.mapedit.tools.parts.AbstractMousePart#TYPE_TILE}
     */
    @Override
    public int getType() {
        return TYPE_TILE;
    }

    /**
     * Place the tile at one location on the map.
     * 
     * @param loc the location where the tile shall be placed
     * @param entry the history entry the changes done by this call shall be
     *            added to
     */
    @Override
    public void placeAt(final Location loc, final HistoryEntry entry) {
        final MapTile tile = MapStorage.getInstance().getMapTile(loc);
        if (tile == null) {
            return;
        }
        final int oldId = tile.getTileId();
        tile.hideTile();
        tile.setTileId(getId());
        MapTransitions.getInstance().checkTileAndSurround(loc);
        tile.showTile();
        final int newId = tile.getTileId();
        if (oldId != newId) {
            entry.addAction(new HistoryActionTile(oldId, newId, loc));
        }
    }

}
