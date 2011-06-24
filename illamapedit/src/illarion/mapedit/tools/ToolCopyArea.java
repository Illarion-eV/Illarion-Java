/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.tools;

import illarion.mapedit.history.HistoryEntry;
import illarion.mapedit.input.SelectionManager;
import illarion.mapedit.map.MapItem;
import illarion.mapedit.map.MapStorage;
import illarion.mapedit.map.MapTile;
import illarion.mapedit.map.export.CopyItem;
import illarion.mapedit.map.export.CopyMap;
import illarion.mapedit.map.export.CopyStorage;
import illarion.mapedit.map.export.CopyTile;
import illarion.mapedit.tools.parts.AbstractMousePart;

import illarion.common.util.Location;

/**
 * Special tool that is used to copy a area of the map into the temporary
 * storage and prepare it to insert it again.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class ToolCopyArea extends AbstractTool {
    /**
     * Storage for the copied map data.
     */
    private CopyMap mapData;

    /**
     * Enable this tool for execution.
     */
    @Override
    public void activateTool() {
        if (!isActive()) {
            setActiveTool(this);
            SelectionManager.getInstance().setAreaSelection();
        }
    }

    /**
     * Finish the execution of the tool.
     * 
     * @param part the mouse tool that is used to execute this tool
     */
    @Override
    public void endExecution(final AbstractMousePart part) {
        CopyStorage.getInstance().setCopiedMap(mapData);
        mapData = null;
    }

    /**
     * Execute the tool on each location of the selected area. This will copy
     * the data of the map into the copied map instance.
     * 
     * @param loc the location the tool is executed on now
     * @param part the currently activated mouse part, unused in this case
     * @param history the history entry used to store changes to the map, unused
     *            in ths case, since nothing is changed
     */
    @Override
    public void executeTool(final Location loc, final AbstractMousePart part,
        final HistoryEntry history) {
        final MapTile tile = MapStorage.getInstance().getMapTile(loc);
        if (tile == null) {
            return;
        }
        mapData.addTile(new CopyTile(tile.getTileId(), loc));

        final int itemCnt = tile.getItemCount();
        for (int i = 0; i < itemCnt; i++) {
            final MapItem item = tile.getItem(i);
            mapData.addItem(new CopyItem(item.getItemId(), item.getQuality(),
                item.getData(), loc));
        }
    }

    /**
     * Overwritten checking method that ensures that the tool is executed even
     * if no mouse part is chosen.
     * 
     * @param part the mouse part to check, does not matter in this case
     * @return <code>true</code> always
     */
    @Override
    public boolean partOkay(final AbstractMousePart part) {
        return true;
    }

    /**
     * Prepare the tool to be executed properly.
     * 
     * @param part the mouse tool that is used to execute this tool
     */
    @Override
    public void startExecution(final AbstractMousePart part) {
        mapData = new CopyMap();
    }

}
