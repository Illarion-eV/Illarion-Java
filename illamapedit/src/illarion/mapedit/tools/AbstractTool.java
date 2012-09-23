/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.tools;

/**
 * @author Tim
 */
public abstract class AbstractTool {

    private ToolManager manager;

    /**
     * X and Y are tile coordinates.
     *
     * @param x
     * @param y
     */
    public abstract void clickedAt(int x, int y);

    /**
     * WARNING x1, x2, y1, y2 are pixel coordinates relative to the panel!
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public abstract void dragged(int x1, int y1, int x2, int y2);

    public void registerManager(final ToolManager toolManager) {
        manager = toolManager;
    }


    protected ToolManager getManager() {
        return manager;
    }
}
