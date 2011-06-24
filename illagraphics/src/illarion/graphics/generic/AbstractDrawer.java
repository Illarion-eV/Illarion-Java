/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.generic;

import illarion.graphics.Drawer;

/**
 * The common implementation of the drawer interface that is used for the shared
 * parts of the drawer implementation that is used by all library specific
 * implementations.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public abstract class AbstractDrawer implements Drawer {
    /**
     * The size of the border that is added in case a rectangle with rounded
     * edges shall be drawn. This helper value stores the smaller triangle edge
     * value.
     */
    protected static final int ROUNDED_BORDER_WIDTH_1 =
        (ROUNDED_BORDER_WIDTH * 5) / 6;

    /**
     * The size of the border that is added in case a rectangle with rounded
     * edges shall be drawn. This helper value stores the larger triangle edge
     * value.
     */
    protected static final int ROUNDED_BORDER_WIDTH_2 =
        (ROUNDED_BORDER_WIDTH * 1) / 2;
}
