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
package illarion.mapedit.gui.awt;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Panel;

/**
 * This class holds the construct for the right toolbar in the map editor view.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class RightToolbar extends Panel {
    /**
     * The serialization UID of the toolbar.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The map selector that shows all current load maps and allows to select
     * the currently edited one.
     */
    private final MapSelector map;

    /**
     * The area that is used to select the parts. Such as items and tiles.
     */
    private final PartSelector part;

    /**
     * Constructor to create to toolbar.
     */
    public RightToolbar() {
        super(new BorderLayout(5, 5) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension preferredLayoutSize(final Container target) {
                final Dimension ret = super.preferredLayoutSize(target);
                ret.width = 200;
                return ret;
            }
        });

        part = new PartSelector();
        map = new MapSelector();
        add(map, BorderLayout.NORTH);
        add(part, BorderLayout.CENTER);
    }

    /**
     * Get the map selector that is used to display and select the load maps.
     * 
     * @return the map selector
     */
    public MapSelector getMapSelector() {
        return map;
    }

    /**
     * Get the part selection area that is used to select the tiles and items.
     * 
     * @return the oarts selection area
     */
    public PartSelector getPartSelector() {
        return part;
    }
}
