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
package illarion.client.world;

import illarion.client.guiNG.references.AbstractReference;

/**
 * Base class for interactive components.
 */
public interface Interactive {
    /**
     * Cast magic at object under cursor.
     * 
     * @return Reference to the object casted on
     */
    AbstractReference castSpellOn();

    /**
     * Signal that the user wants to start dragging from the item.
     * 
     * @param x X-Coordinate of the item to be dragged
     * @param y Y-Coordinate of the item to be dragged
     * @return reference to the item to be dragged or null if it is not allowed
     */
    AbstractReference dragFrom(int x, int y);

    /**
     * Request acceptance of the drag from the drag destination.
     * 
     * @param dragSrc object that is currently dragged
     * @return a target reference or null to refuse the drag
     */
    AbstractReference dragTo(AbstractReference dragSrc);

    /**
     * Get graphics component at the specified location.
     * 
     * @param x X-Coordinate on the game screen
     * @param y Y-Coordinate on the game screen
     * @return the component on the specified position or null
     */
    Interactive getComponentAt(int x, int y);

    /**
     * Retrieves the context menu for this component.
     * 
     * @return the generated context menu.
     */
    // ContextMenu getMenu();

    /**
     * Gets the tooltip text for the component.
     * 
     * @return the text of the tooltip
     */
    String getTooltipText();

    /**
     * Inform the drag source that the user is still dragging. Dragging may be
     * cancelled by returning false.
     * 
     * @param x X-Coordinate of the current cursor position
     * @param y Y-Coordinate of the current cursor position
     * @return true to continue the drag, false to cancel it
     */
    boolean isDragging(int x, int y);

    /**
     * Lookat action on the interactive object.
     */
    void lookAt();

    /**
     * Try to open a container.
     */
    void openContainer();

    /**
     * Notifies a component that the mouse is hovering over it.
     * 
     * @param hover true for enableing hover, false for disabling
     */
    void setHover(boolean hover);

    /**
     * Add this item to the list of used items.
     * 
     * @return Reference to the used object
     */
    AbstractReference useItem();

    /**
     * Change this component with the mouse wheel.
     * 
     * @param delta new position of the mouse
     */
    void wheelIncrement(int delta);
}
