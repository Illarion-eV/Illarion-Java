/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import illarion.common.graphics.MapVariance;
import illarion.common.graphics.TileInfo;
import illarion.common.util.Location;

import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;

/**
 * Created: 20.08.2005 17:39:11
 */
public class Tile extends AbstractEntity {
    private static final int BASE_MASK = 0x001F;
    private static final int OVERLAY_MASK = 0x03E0;
    private static final int SHAPE_MASK = 0xFC00;

    @SuppressWarnings("nls")
    private static final String TILE_PATH = "data/tiles/";

    private transient final FrameAnimation ani;

    private transient final TileInfo info;

    private Overlay overlay;

    private final boolean variants;

    /**
     * Create tile with animation or variants
     */
    protected Tile(final int id, final String name, final int frames,
        final int speed, final TileInfo info) {
        super(id, TILE_PATH, name, frames, 0, 0, 0, 0, Sprite.HAlign.center,
            Sprite.VAlign.middle, false, false, null);

        this.info = info;

        // an animated tile
        if (speed > 0) {
            // start animation right away. All tiles of this type will share it
            ani = new FrameAnimation(null);
            ani.setup(frames, 0, speed, FrameAnimation.LOOPED);
            variants = false;
        } else if (frames > 1) { // a tile with variants
            variants = true;
            ani = null;
        } else {
            ani = null;
            variants = false;
        }
        reset();
    }

    /**
     * Create static tile
     * 
     * @param id
     * @param name
     */
    protected Tile(final int id, final String name, final TileInfo info) {
        this(id, name, 1, 0, info);
    }

    /**
     * Copy constructor for duplicates
     * 
     * @param org
     */
    private Tile(final Tile org) {
        super(org);

        // copy the animation, too, so that all tiles share the same cycle
        ani = org.ani;
        info = org.info;
        variants = org.variants;
        reset();
    }

    /**
     * Return the base id of a tile
     * 
     * @param id
     * @return
     */
    public static int baseID(final int id) {
        if ((id & SHAPE_MASK) > 0) {
            return id & BASE_MASK;
        }
        return id;
    }

    public static Tile create(final int id, final Location loc) {
        return create(id, loc.getScX(), loc.getScY());
    }

    public static int overlayID(final int id) {
        if ((id & SHAPE_MASK) > 0) {
            return (id & OVERLAY_MASK) >> 5;
        }
        return 0;
    }

    /**
     * Create a tile and its overlay. Assign variant.
     * 
     * @param id complex id
     * @param x
     * @param y
     * @return
     */
    @SuppressWarnings("nls")
    private static Tile create(int id, final int x, final int y) {
        Tile tile;

        // split id into overlay and tile
        final int overlayShape = (id & SHAPE_MASK) >> 10;
        int overlayId = 0;
        if (overlayShape > 0) {
            overlayId = Tile.overlayID(id);
            id = Tile.baseID(id);
        }

        // instantiate tile
        tile = TileFactory.getInstance().getCommand(id);
        // if it is a variants tile, set coordinates
        if (tile.variants) {
            tile.setVariant(x, y);
        }

        if (overlayId > 0) {
            tile.setOverlay(Overlay.create(overlayId, overlayShape));
        }

        return tile;
    }

    @Override
    public void activate(final int id) {
        // no mapping possible
        // this.id = id;
    }

    @Override
    public Tile clone() {
        return new Tile(this);
    }

    /**
     * Draw tile and its overlay
     * 
     * @param x
     * @param y
     * @param corridor
     * @return
     */
    @Override
    public boolean draw() {
        super.draw();

        if (overlay != null) {
            overlay.draw();
        }
        return true;
    }

    public int getMapColor() {
        return info.getMapColor();
    }

    public int getMovementCost() {
        return info.getMovementCost();
    }

    public boolean isObstacle() {
        return info.getMovementCost() == 0;
    }

    public boolean isOpapue() {
        return info.isOpaque() && !isTransparent();
    }

    @Override
    public void recycle() {
        hide();
        if (ani != null) {
            ani.removeTarget(this);
        }
        if (overlay != null) {
            overlay.recycle();
            overlay = null;
        }
        TileFactory.getInstance().recycle(this);
    }

    // @Override
    // public void reduceFrames() {
    // super.reduceFrames();
    // if (ani != null) {
    // // restart animation with new parameters
    // ani.start(sprite.getFrames(), 0, (int) ani.getDuration()
    // / Animation.ANIMATION_FRAME, FrameAnimation.LOOPED);
    // ani.stop();
    // }
    // }

    @Override
    public void setLight(final SpriteColor light) {
        super.setLight(light);
        if (overlay != null) {
            overlay.setLight(light);
        }
    }

    /**
     * Assign overlay to tile
     * 
     * @param overlay
     */
    public void setOverlay(final Overlay overlay) {
        this.overlay = overlay;
    }

    /**
     * Set position of tile and its overlays
     * 
     * @param x
     * @param y
     * @param z
     * @param layer
     */
    @Override
    public void setScreenPos(final int x, final int y, final int z,
        final int layer) {
        super.setScreenPos(x, y, z, layer);
        if (overlay != null) {
            overlay.setScreenPos(x, y, 0, 0);
        }
    }

    /**
     * Add tile to display list
     */
    @Override
    public void show() {
        super.show();
        if (ani != null) {
            ani.addTarget(this, true);
        }
    }

    @Override
    public void update(final int delta) {
        if (overlay == null) {
            super.update(delta);
        } else {
            setAlpha(SpriteColor.COLOR_MAX);
            overlay.setAlpha(SpriteColor.COLOR_MAX);
        }
    }

    /**
     * Determine the graphical variant from the x/y coordinates
     * 
     * @param x
     * @param y
     */
    protected final void setVariant(final int x, final int y) {
        setFrame(MapVariance.getTileFrameVariance(x, y, getFrames()));
    }
}
