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

import org.apache.log4j.Logger;

import illarion.common.util.RecycleObject;

import illarion.graphics.Sprite;

/**
 * Created: 20.08.2005 17:39:11
 */
public class Overlay extends AbstractEntity implements RecycleObject {
    public static final String TILE_PATH = "data/tiles/";

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Overlay.class);

    /**
     * Create tile with animation or variants
     */
    public Overlay(final int id, final String name) {
        super(id, TILE_PATH, name, 28, 0, 0, 0, 0, Sprite.HAlign.center,
            Sprite.VAlign.middle, false);
        reset();
    }

    /**
     * Copy constructor for duplicates
     * 
     * @param org
     */
    public Overlay(final Overlay org) {
        super(org);
        reset();
    }

    /**
     * Create a new instance of a overlay.
     * 
     * @param id
     * @param shape
     * @return
     */
    public static Overlay create(final int id, final int shape) {
        try {
            final Overlay overlay =
                OverlayFactory.getInstance().getCommand(id);
            overlay.setFrame(shape - 1);
            return overlay;
        } catch (final IndexOutOfBoundsException ex) {
            LOGGER.error("Failed to create overlay with ID "
                + Integer.toString(id));
            return null;
        }
    }

    @Override
    public void activate(final int id) {
        // no mapping possible
        // this.id = id;
    }

    @Override
    public Overlay clone() {
        return new Overlay(this);
    }

    @Override
    public void recycle() {
        OverlayFactory.getInstance().recycle(this);
    }
}
