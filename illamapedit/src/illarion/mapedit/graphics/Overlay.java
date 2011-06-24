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

import illarion.graphics.Sprite;

/**
 * This defines a graphical overlay that is displayed over a tile.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class Overlay extends AbstractEntity {
    /**
     * The path to the tile graphic resources.
     */
    @SuppressWarnings("nls")
    public static final String TILE_PATH = "data/tiles/";

    /**
     * The overlay layer.
     */
    private final int overlayLayer;

    /**
     * Create a overlay with a specified ID.
     * 
     * @param id the ID of the overlay
     * @param name the name of the graphic of this overlay
     * @param layer the overlay layer of this overlay
     */
    public Overlay(final int id, final String name, final int layer) {
        super(id, TILE_PATH, name, 28, 0, 0, 0, Sprite.HAlign.center,
            Sprite.VAlign.middle);
        overlayLayer = layer;
    }

    /**
     * Copy constructor for duplicates
     * 
     * @param org the overlay to copy
     */
    public Overlay(final Overlay org) {
        super(org);
        overlayLayer = org.overlayLayer;
    }

    /**
     * Get a new instance of a overlay with a specified graphic ID and a shape.
     * 
     * @param id the ID of the overlay
     * @param shape the shape of the overlay
     * @return the instance of overlay with the requested specifications
     */
    public static Overlay create(final int id, final int shape) {
        final Overlay overlay = OverlayFactory.getInstance().getCommand(id);
        overlay.setFrame(shape - 1);
        return overlay;
    }

    /**
     * Overwritten activate method to avoid that the ID is changed at
     * activation. This will disable all mapping possibilities.
     * 
     * @param id the new ID, unused in this case
     */
    @Override
    public void activate(final int id) {
        // no mapping possible
        // this.id = id;
    }

    /**
     * Create a duplicate of this object.
     * 
     * @return the duplicate of this object
     */
    @Override
    public Overlay clone() {
        return new Overlay(this);
    }

    /**
     * Get the overlay layer of this overlay. This is used to determine what
     * overlay is displayed.
     * 
     * @return the value of the layer
     */
    public int getLayer() {
        return overlayLayer;
    }

    @Override
    public String getName() {
        return null;
    }

    /**
     * Put the overlay back into the factory, so its reused later.
     */
    @Override
    public void recycle() {
        OverlayFactory.getInstance().recycle(this);
    }
}
