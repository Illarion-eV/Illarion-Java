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
package illarion.mapedit.input;

import illarion.mapedit.MapEditor;
import illarion.mapedit.history.HistoryEntry;
import illarion.mapedit.map.MapStorage;
import illarion.mapedit.tools.AbstractTool;
import illarion.mapedit.tools.parts.AbstractMousePart;
import illarion.mapedit.tools.parts.MousePartsManager;

import illarion.common.util.Location;

import illarion.graphics.Graphics;

/**
 * The selection manager stores the currently selected area and takes care for
 * for selecting and deselecting tiles properly.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class SelectionManager {
    /**
     * The singleton instance of the selection manager.
     */
    private static final SelectionManager INSTANCE = new SelectionManager();

    /**
     * The location the mouse cursor currently points at.
     */
    private final Location currentLocation;

    /**
     * This flag stores if the SelectionManager selects only one tile or a area
     * of tiles.
     */
    private boolean multiSelection;

    /**
     * Flag that causes all {@link #isSelected(Location)} calls to return false.
     * Should be set in case the mouse cursor is outside the render area.
     */
    private boolean noSelection;

    /**
     * This flag is set true in case there is currently a area selection going
     * on.
     */
    private boolean selectingArea;

    /**
     * The location where the selection of a area starts.
     */
    private final Location startArea;

    /**
     * The private default constructor used to make sure that there are no
     * further instances but the singleton instance created.
     */
    private SelectionManager() {
        startArea = Location.getInstance();
        currentLocation = Location.getInstance();
        multiSelection = false;
        noSelection = false;
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of the SelectionManager
     */
    public static SelectionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Stop selecting a area.
     */
    public void endAreaSelect() {
        if (!selectingArea) {
            return;
        }
        selectingArea = false;
        Graphics.getInstance().getRenderDisplay().getRenderArea().repaint();
    }

    /**
     * Execute the currently used tool on the current selection.
     * 
     * @param key the key that triggered this execution
     */
    public void executeToolOnSelection(final int key) {
        final AbstractTool tool = AbstractTool.getActiveTool();
        final AbstractMousePart part =
            MousePartsManager.getInstance().getActivePart(key);

        if ((tool == null) || !tool.partOkay(part)) {
            return;
        }
        int minX = currentLocation.getScX();
        int minY = currentLocation.getScY();
        final int level = currentLocation.getScZ();
        int maxX = minX;
        int maxY = minY;
        if (selectingArea) {
            minX = Math.min(minX, startArea.getScX());
            minY = Math.min(minY, startArea.getScY());
            maxX = Math.max(maxX, startArea.getScX());
            maxY = Math.max(maxY, startArea.getScY());
        }

        final HistoryEntry histEntry = new HistoryEntry();
        final Location loc = Location.getInstance();
        tool.startExecution(part);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                loc.setSC(x, y, level);
                tool.executeTool(loc, part, histEntry);
            }
        }
        tool.endExecution(part);
        if (histEntry.containsActions()) {
            MapStorage.getInstance().getSelectedMap().getHistory()
                .addEntry(histEntry);
        }
        loc.recycle();
    }

    /**
     * Check if a location is currently selected or not.
     * 
     * @param loc the location to check
     * @return <code>true</code> in case the location is currently selected
     */
    public boolean isSelected(final Location loc) {
        if (noSelection) {
            return false;
        }
        if (currentLocation.getScZ() != loc.getScZ()) {
            return false;
        }
        if (selectingArea) {
            return isInside(startArea.getScX(), currentLocation.getScX(),
                loc.getScX())
                && isInside(startArea.getScY(), currentLocation.getScY(),
                    loc.getScY());
        }
        return currentLocation.equals(loc);
    }

    /**
     * Set the selection manager to select a area of tiles in case its requested
     * by the player.
     */
    public void setAreaSelection() {
        multiSelection = true;
    }

    /**
     * Update the location the cursor currently points at.
     * 
     * @param loc the location to point at
     */
    public void setCurrentLocation(final Location loc) {
        if (!currentLocation.equals(loc)) {
            currentLocation.set(loc);
            noSelection = false;
            Graphics.getInstance().getRenderDisplay().getRenderArea()
                .repaint();
            //MapEditor.getMainFrame().getToolbar().setCurrentLocation(loc);
        }
    }

    /**
     * Set that the mouse cursor is currently outside of the render area. This
     * flag is removed again when the next current location is set.
     */
    public void setOutside() {
        noSelection = true;
    }

    /**
     * Set the selection manager to select only one tile at time.
     */
    public void setSingleSelection() {
        multiSelection = false;
        endAreaSelect();
    }

    /**
     * Start the selection of a area.
     */
    public void startAreaSelect() {
        if (multiSelection) {
            selectingArea = true;
            startArea.set(currentLocation);
        }
    }

    /**
     * Check if a value is within two borders (including).
     * 
     * @param border1 the first border value
     * @param border2 the second border value
     * @param value the value to check
     * @return <code>true<code> if the value equals one of the borders or if the value is between both borders
     */
    private boolean isInside(final int border1, final int border2,
        final int value) {
        if ((border1 == value) || (border2 == value)) {
            return true;
        }
        if (border1 > border2) {
            return ((value > border2) && (value < border1));
        }
        if (border1 < border2) {
            return ((value < border2) && (value > border1));
        }
        return false;
    }
}
