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
import illarion.mapedit.tools.parts.AbstractMousePart;

import illarion.common.util.Location;

/**
 * Abstract definition of a tool that is used place something on the map.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public abstract class AbstractTool {
    /**
     * The tool that is currently activated.
     */
    private static AbstractTool activatedTool;

    /**
     * Get the currently activated tool.
     * 
     * @return the activated tool
     */
    public static AbstractTool getActiveTool() {
        return activatedTool;
    }

    /**
     * Set the tool that is activated.
     * 
     * @param tool the tool to activate
     */
    protected static void setActiveTool(final AbstractTool tool) {
        activatedTool = tool;
    }

    /**
     * Activate the tool and set up everything so the tool works correctly.
     */
    public abstract void activateTool();

    /**
     * This function is called after the
     * {@link #executeTool(Location, AbstractMousePart, HistoryEntry)} calls.
     * Can be used to clean up the tool after the execution.
     * 
     * @param part the mouse tool that is used to execute this tool
     */
    public void endExecution(final AbstractMousePart part) {
        if (part != null) {
            part.finishExecution();
        }
    }

    /**
     * Execute the tool on a specified location.
     * 
     * @param loc the location the tool is executed on
     * @param part the mouse tool that is used to execute this tool
     * @param history the history entry the changes caused by this tool shall be
     *            added to
     */
    public abstract void executeTool(Location loc, AbstractMousePart part,
        HistoryEntry history);

    /**
     * Check if the mouse part is fine for this tool to be used.
     * 
     * @param part the part to check
     * @return <code>true</code> in case the tool can be executed with that
     *         mouse part
     */
    public boolean partOkay(final AbstractMousePart part) {
        return (part != null);
    }

    /**
     * This function is called before the
     * {@link #executeTool(Location, AbstractMousePart, HistoryEntry)} calls.
     * Can be used to prepare the tool in case its needed.
     * 
     * @param part the mouse tool that is used to execute this tool
     */
    public void startExecution(final AbstractMousePart part) {
        if (part != null) {
            part.startExecution();
        }
    }

    /**
     * Check if this tool is the activated one.
     * 
     * @return <code>true</code> in case this tool is activated
     */
    protected final boolean isActive() {
        return equals(activatedTool);
    }
}
