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
package illarion.mapedit.graphics;

import illarion.mapedit.MapEditor;

import illarion.common.graphics.MapConstants;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * This is a special implementation of a tile that represents a tile that is on
 * the area of the map but is not set.
 * 
 * @author Martin Karing
 * @since 0.99
 */
class BlindTile extends Tile {
    /**
     * The color that is used to display the tile in case its selected.
     */
    private static final SpriteColor BLIND_SELECTED_COLOR;

    /**
     * The color that is used to display the grid for the tile in case its
     * needed.
     */
    private static final SpriteColor GRID_COLOR;

    /**
     * The color that is used to display the grid for the tile in case its
     * selected.
     */
    private static final SpriteColor SELECTED_GRID_COLOR;

    static {
        GRID_COLOR = Graphics.getInstance().getSpriteColor();
        GRID_COLOR.set(1.f);
        GRID_COLOR.setAlpha(0.7f);

        SELECTED_GRID_COLOR = Graphics.getInstance().getSpriteColor();
        SELECTED_GRID_COLOR.set(0.f, 1.f, 0.f);
        SELECTED_GRID_COLOR.setAlpha(0.9f);

        BLIND_SELECTED_COLOR = Graphics.getInstance().getSpriteColor();
        BLIND_SELECTED_COLOR.set(0.f, 1.f, 0.f);
        BLIND_SELECTED_COLOR.setAlpha(0.3f);
    }

    /**
     * Construct a default version of the blind tile.
     */
    @SuppressWarnings("nls")
    protected BlindTile() {
        super(Short.MAX_VALUE, "invisible", 1, 0, null, "Blind tile");
    }

    /**
     * Copy constuctor for a blind tile. The newly created instance copies all
     * values from this one.
     * 
     * @param org the original instance that is used as data source
     */
    private BlindTile(final BlindTile org) {
        super(org);
    }

    /**
     * Create a duplicate of this object.
     * 
     * @return the new instance of Tile that is a duplicate of this one
     */
    @Override
    public BlindTile clone() {
        return new BlindTile(this);
    }

    /**
     * Draw the frame of the blind tile or the marking on the tile to the screen
     * if needed.
     * 
     * @return true in case the render operation was performed correctly
     */
    @Override
    public boolean draw() {
        if (!MapEditor.getDisplay().isInsideViewport(getBorderRectangle())) {
            return true;
        }

        if (isSelected()) {
            CALC_COLOR.set(BLIND_SELECTED_COLOR);
            if (!isSelectable()) {
                CALC_COLOR.multiply(0.5f);
            }
            Graphics.getInstance().getRenderDisplay()
                .applyOffset(getDisplayX(), getDiplsayY());
            Graphics.getInstance().getDrawer()
                .drawTriangles(TILE_SHAPE, CALC_COLOR);
            Graphics.getInstance().getRenderDisplay().resetOffset();
        }

        if ((MapEditor.getDisplay().getSettingsGrid() == MapDisplay.GridDisplay.show)
            && isSelectable()) {
            if (isSelected()) {
                CALC_COLOR.set(SELECTED_GRID_COLOR);
            } else {
                CALC_COLOR.set(GRID_COLOR);
            }
            Graphics.getInstance().getRenderDisplay()
                .applyOffset(getDisplayX(), getDiplsayY());
            Graphics
                .getInstance()
                .getDrawer()
                .drawQuadrangleFrame(-MapConstants.STEP_X, 0, 0,
                    -MapConstants.STEP_Y, MapConstants.STEP_X, 0, 0,
                    MapConstants.STEP_Y, CALC_COLOR);
            Graphics.getInstance().getRenderDisplay().resetOffset();
        }

        return true;
    }
}
