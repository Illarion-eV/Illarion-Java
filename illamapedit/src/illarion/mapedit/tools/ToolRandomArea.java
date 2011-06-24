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
import illarion.mapedit.tools.parts.AbstractMousePart;

import illarion.common.util.FastMath;
import illarion.common.util.Location;

/**
 * The single tool is used to place items randomly on a area.
 * 
 * @author Martin Karing
 * @since 0.99
 * @version 1.01
 */
public final class ToolRandomArea extends AbstractTool {
    /**
     * The chance that a part is set on the map.
     */
    private float chance = 0.3f;

    /**
     * Activate the tool for proper execution.
     */
    @Override
    public void activateTool() {
        if (!isActive()) {
            setActiveTool(this);
            SelectionManager.getInstance().setAreaSelection();
        }
    }

    /**
     * Execute the tool on a specified location.
     * 
     * @param loc the location the tool is executed at
     * @param part the part that is executed on this location
     * @param entry the history entry the changes done by this tool shall be
     *            added to
     */
    @Override
    public void executeTool(final Location loc, final AbstractMousePart part,
        final HistoryEntry entry) {
        if (FastMath.nextRandomFloat() <= chance) {
            part.placeAt(loc, entry);
        }
    }

    /**
     * Set the chance value that is used by this random area tool.
     * 
     * @param newChance the new chance value, valid values between 0.f and 1.f
     */
    public void setProbability(final float newChance) {
        if ((newChance >= 0.f) && (newChance <= 1.f)) {
            chance = newChance;
        }
    }
}
