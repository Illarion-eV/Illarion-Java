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
import illarion.mapedit.map.export.CopyMap;
import illarion.mapedit.map.export.CopyStorage;
import illarion.mapedit.tools.parts.AbstractMousePart;

import illarion.common.util.Location;

/**
 * A tool that allows to paste a map that was copied before into the currently
 * existing map.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class ToolPaste extends AbstractTool {
    /**
     * Prepare the tool for execution.
     */
    @Override
    public void activateTool() {
        if (!isActive()) {
            setActiveTool(this);
            SelectionManager.getInstance().setSingleSelection();
        }
    }

    /**
     * Execute the tool on one location. In this case it will copy the copied
     * map at the selected location the map.
     * 
     * @param loc the location where the copied map shall be insert
     * @param part the mouse part that is currently activated, this is not used
     *            how ever
     * @param history the history entry that stores all changes done to the map
     */
    @Override
    public void executeTool(final Location loc, final AbstractMousePart part,
        final HistoryEntry history) {
        final CopyMap map = CopyStorage.getInstance().getCopiedMap();
        if (map == null) {
            return;
        }
        map.insertAt(loc.getScX(), loc.getScY(), loc.getScZ(), history);
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

}
