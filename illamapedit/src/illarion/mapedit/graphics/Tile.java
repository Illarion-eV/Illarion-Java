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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import illarion.mapedit.MapEditor;

import illarion.common.graphics.MapConstants;
import illarion.common.graphics.MapVariance;
import illarion.common.graphics.TileInfo;
import illarion.common.util.Location;

import illarion.graphics.Graphics;
import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;
import illarion.graphics.common.MapColor;

/**
 * Defines a graphical tile that can be displayed on the map.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public class Tile extends AbstractEntity {
    /**
     * The float buffer storing the required values for rendering a simplyfied
     * tile.
     */
    protected static final FloatBuffer TILE_SHAPE;

    private static final SpriteColor GRID_COLOR;

    /**
     * The path to the file data in the resources.
     */
    @SuppressWarnings("nls")
    private static final String TILE_PATH = "data/tiles/";

    static {
        TILE_SHAPE =
            ByteBuffer.allocateDirect((8 * Float.SIZE) / 8)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        TILE_SHAPE.put(-MapConstants.STEP_X).put(0.f);
        TILE_SHAPE.put(0.f).put(-MapConstants.STEP_Y);
        TILE_SHAPE.put(0.f).put(MapConstants.STEP_Y);
        TILE_SHAPE.put(MapConstants.STEP_X).put(0.f);
        TILE_SHAPE.flip();

        GRID_COLOR = Graphics.getInstance().getSpriteColor();
        GRID_COLOR.set(0.f, 0.f, 0.f);
        GRID_COLOR.setAlpha(0.7f);
    }

    /**
     * The tile informations. Construct stores all informations about this type
     * of tile.
     */
    private transient final TileInfo info;

    /**
     * The overlay mask of the tile. This is only set in case the tile uses a
     * overlay.
     */
    private Overlay overlay;

    private final String tileName;

    /**
     * A flag if this tile uses variances or not.
     */
    private final boolean variants;

    /**
     * Constructor for a tile.
     * 
     * @param id the ID of the tile
     * @param name the base name of the resource files of this tile
     * @param frames the amount of frames the sprite of this tile stores
     * @param speed the speed of the animation, in case its set to 0 the frames
     *            are used as variances and not as animations, how ever
     *            animations are not displayed in the map editor
     * @param info the common informations about this tile
     */
    protected Tile(final int id, final String name, final int frames,
        final int speed, final TileInfo info, final String title) {
        super(id, TILE_PATH, name, frames, 0, 0, 0, Sprite.HAlign.center,
            Sprite.VAlign.middle);

        this.info = info;
        tileName = title;

        if ((speed == 0) && (frames > 1)) {
            variants = true;
        } else {
            variants = false;
        }
    }

    /**
     * Short constructor of a tile that assumes one frame.
     * 
     * @param id the ID of the tile
     * @param name the base name of the resource files of this til
     * @param info the common informations about this tile
     */
    protected Tile(final int id, final String name, final TileInfo info,
        final String title) {
        this(id, name, 1, 0, info, title);
    }

    /**
     * Copy constructor to create a dublicate of the original tile.
     * 
     * @param org the original tile that needs to be copied
     */
    protected Tile(final Tile org) {
        super(org);

        info = org.info;
        variants = org.variants;
        tileName = org.tileName;
    }

    /**
     * Decode the base tile ID from a tile ID.
     * 
     * @param id the encoded tile ID
     * @return the id of the base tile
     */
    public static int baseID(final int id) {
        if ((id & MapConstants.SHAPE_MASK) > 0) {
            return id & MapConstants.BASE_MASK;
        }
        return id;
    }

    /**
     * Create a new instance of the tile, or reuse a old one and set it up for
     * display on a specified location.
     * 
     * @param id the ID of the tile, this id is decoded to get base, overlay and
     *            shape id
     * @param col the column where the tile is supposed to be created
     * @param row the row where the tile is supposed to be created
     * @return the prepared instance of a Tile
     */
    public static Tile create(final int id, final int col, final int row) {
        // split id into overlay and tile
        final int overlayShape = (id & MapConstants.SHAPE_MASK) >> 10;
        int overlayId = 0;
        int baseId = id;
        if (baseId != Short.MAX_VALUE) {
            if (overlayShape > 0) {
                overlayId = Tile.overlayID(id);
                baseId = Tile.baseID(id);
            }
        }

        // instantiate tile
        final Tile tile = TileFactory.getInstance().getCommand(baseId);
        // if it is a variants tile, set coordinates
        if (tile.variants) {
            final Location tempLoc = Location.getInstance();
            tempLoc.setMC(col, row);
            tile.setVariant(tempLoc.getScX(), tempLoc.getScY());
            tempLoc.recycle();
        }

        if (overlayId > 0) {
            tile.setOverlay(Overlay.create(overlayId, overlayShape));
        }

        return tile;
    }

    /**
     * Create a new instance of the tile, or reuse a old one and set it up for
     * display on a specified location.
     * 
     * @param id the ID of the tile, this id is decoded to get base, overlay and
     *            shape id
     * @param loc the location where the tile shall be placed, this is used to
     *            select the variance
     * @return the prepared instance of a Tile
     */
    public static Tile create(final int id, final Location loc) {
        return create(id, loc.getCol(), loc.getRow());
    }

    /**
     * Generate a ID for a tile based on a base, overlay and shape id.
     * 
     * @param baseId the tile ID of the tile that is the base of this tile
     * @param overlayId the tile ID of the tile that is the overlay of this tile
     * @param shapeId the shape ID of the overlay
     * @return the encoded tile ID
     */
    public static int generateTileId(final int baseId, final int overlayId,
        final int shapeId) {
        if ((baseId == 0) || (baseId > 31)) {
            return baseId;
        }
        int returnValue = 0;
        returnValue += (baseId & MapConstants.BASE_MASK);
        returnValue += ((overlayId << 5) & MapConstants.OVERLAY_MASK);
        returnValue += ((shapeId << 10) & MapConstants.SHAPE_MASK);
        return returnValue;
    }

    /**
     * Decode the overlay tile ID from a tile ID.
     * 
     * @param id the encoded tile ID
     * @return the id of the overlay tile
     */
    public static int overlayID(final int id) {
        if ((id & MapConstants.SHAPE_MASK) > 0) {
            return (id & MapConstants.OVERLAY_MASK) >> 5;
        }
        return 0;
    }

    /**
     * Overwritten activate method to ensure that its impossible to change the
     * ID of this tile construct.
     * 
     * @param id the new ID, that is not used in this implementation
     */
    @Override
    public void activate(final int id) {
        // no mapping possible
    }

    /**
     * Create a duplicate of this object.
     * 
     * @return the new instance of Tile that is a duplicate of this one
     */
    @Override
    public Tile clone() {
        return new Tile(this);
    }

    /**
     * Draw the object on the screen.
     * 
     * @return true in case the render operation was performed correctly
     */
    @Override
    public boolean draw() {
        if (!MapEditor.getDisplay().isInsideViewport(getBorderRectangle())) {
            return false;
        }
        final MapDisplay.TileDisplay tileSettings =
            MapEditor.getDisplay().getSettingsTile();
        if (tileSettings == MapDisplay.TileDisplay.full) {
            super.draw();
            if (overlay != null) {
                overlay.draw();
            }
            if (MapEditor.getDisplay().getSettingsGrid() == MapDisplay.GridDisplay.show) {
                Graphics.getInstance().getRenderDisplay()
                    .applyOffset(getDisplayX(), getDiplsayY());
                Graphics
                    .getInstance()
                    .getDrawer()
                    .drawQuadrangleFrame(-MapConstants.STEP_X, 0, 0,
                        -MapConstants.STEP_Y, MapConstants.STEP_X, 0, 0,
                        MapConstants.STEP_Y, GRID_COLOR);
                Graphics.getInstance().getRenderDisplay().resetOffset();
            }
        } else if (tileSettings == MapDisplay.TileDisplay.simple) {
            final SpriteColor tileColor =
                MapColor.getSpriteColor(info.getMapColor());
            Graphics.getInstance().getRenderDisplay()
                .applyOffset(getDisplayX(), getDiplsayY());
            Graphics.getInstance().getDrawer()
                .drawTriangles(TILE_SHAPE, tileColor);
            Graphics.getInstance().getRenderDisplay().resetOffset();
        }
        return true;
    }

    @Override
    public String getName() {
        return tileName;
    }

    /**
     * Check if the tile is blocking movement.
     * 
     * @return <code>true</code> if the tile is blocking movement
     */
    public boolean isBlockingTile() {
        return (info.getMovementCost() == 0);
    }

    public boolean isOpaque() {
        if (info == null) {
            return false;
        }
        return info.isOpaque();
    }

    /**
     * Put the tile back into the recycle factory.
     */
    @Override
    public void recycle() {
        TileFactory.getInstance().recycle(this);
    }

    /**
     * Cleanup the tile before its placed back on the recycle factory.
     */
    @Override
    public void reset() {
        if (overlay != null) {
            overlay.recycle();
            overlay = null;
        }
        super.reset();
    }

    /**
     * Overwritten version of the setLight method to set the color of the
     * overlay to the same value as the base tile.
     * 
     * @param light the new instance of the color that is used to color the tile
     */
    @Override
    public void setLight(final SpriteColor light) {
        super.setLight(light);
        if (overlay != null) {
            overlay.setLight(light);
        }
    }

    /**
     * Set the overlay that is displayed on the tile.
     * 
     * @param newOverlay the new instance of the overlay that is displayed
     */
    public void setOverlay(final Overlay newOverlay) {
        overlay = newOverlay;
    }

    /**
     * Set the position of the entity on the display. The display origin is at
     * the origin of the game map.
     * 
     * @param dispX the x coordinate of the location of the display
     * @param dispY the y coordinate of the location of the display
     * @param zLayer the z layer of the coordinate
     * @param typeLayer the global layer of this type of entity.
     */
    @Override
    public void setScreenPos(final int dispX, final int dispY,
        final int zLayer, final int typeLayer) {
        super.setScreenPos(dispX, dispY, zLayer, typeLayer);
        if (overlay != null) {
            overlay.setScreenPos(dispX, dispY, zLayer, typeLayer);
        }
    }

    @Override
    public void setSelectable(final Selectable newSelectable) {
        super.setSelectable(newSelectable);
        if (overlay != null) {
            overlay.setSelectable(newSelectable);
        }
    }

    /**
     * Set the variance of the tile that is displayed on the ground.
     * 
     * @param locX the x coordinate of the location
     * @param locY the y coordinate of the location
     */
    public final void setVariant(final int locX, final int locY) {
        setFrame(MapVariance.getTileFrameVariance(locX, locY, getFrames()));
    }
}
