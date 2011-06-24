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

/**
 * Interface for a object that can be rendered on the screen.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.95
 */
public interface DisplayItem {
    /**
     * Draw the object on the screen.
     * 
     * @return true in case the render operation was performed correctly
     */
    boolean draw();

    /**
     * Get the z order of the item. This order is used to sort the display items
     * in a proper order to be rendered.
     * 
     * @return the z layer coordinate of the display item
     */
    int getZOrder();

    /**
     * Remove object from display list.
     */
    void hide();

    /**
     * Show object by adding it to the display list.
     */
    void show();

    /**
     * Update the alpha value of this component. This is done by considering the
     * size and the location of the component and regarding the alpha target.
     * 
     * @param delta the time in milliseconds since the last update
     */
    void update(int delta);
}
