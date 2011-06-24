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
import illarion.client.util.Lang;

/**
 * Adapter implementing Interactive with no-op functions and tooltip support.
 */
public abstract class Interaction implements Interactive {

    /**
     * Text of the tool tip.
     */
    private String toolTip;

    /**
     * Key word of the tooltip. Used for the localization.
     */
    private String toolTipKey;

    /**
     * Default constructor, generating a empty tool tip.
     */
    protected Interaction() {
        toolTipKey = null;
        toolTip = null;
    }

    /**
     * Constructor with additional possibility to set the keyword of the
     * tooltip.
     * 
     * @param newToolTipKey the keyword for the tooltip that shall be used
     */
    protected Interaction(final String newToolTipKey) {
        toolTipKey = newToolTipKey;
        toolTip = null;
    }

    /**
     * Action if the user casts a spell on the component.
     * 
     * @return Reference to the object the spell was casted on
     */
    @Override
    public AbstractReference castSpellOn() {
        return null;
    }

    /**
     * Dragging action performed by the user starts.
     * 
     * @param x X-Coordinate of the screen coordinates the object was dragged
     *            from
     * @param y Y-Coordinate of the screen coordinates the object was dragged
     *            from
     * @return Reference to the object that is dragged
     */
    @Override
    public AbstractReference dragFrom(final int x, final int y) {
        return null;
    }

    /**
     * Dragging action performed by the user ends.
     * 
     * @param dragSrc Object that is dragged currently
     * @return Reference to the object that is dragged
     */
    @Override
    public AbstractReference dragTo(final AbstractReference dragSrc) {
        return null;
    }

    /**
     * Get a component at a given screen location.
     * 
     * @param x X-Coordinate on the screen where the user is pointing at
     * @param y Y-Coordinate on the screen where the user is pointing at
     * @return the component the user is pointing at or null
     */
    @Override
    public Interactive getComponentAt(final int x, final int y) {
        return null;
    }

    /**
     * Request the context menu on a component.
     * 
     * @return the generated context menu
     */
    // public ContextMenu getMenu() {
    // return null;
    // }

    /**
     * Get the text of the tooltip. Does return the localized version of the
     * tooltip
     * 
     * @return the localized tooltip text
     */
    @Override
    public final String getTooltipText() {
        if ((toolTip == null) && (toolTipKey != null)) {
            toolTip = Lang.getMsg(toolTipKey);
        }

        return toolTip;
    }

    /**
     * Check if the user is currently dragging something.
     * 
     * @param x X-Coordinate of the screen coordinates the user points at
     * @param y Y-Coordinate of the screen coordinates the user points at
     * @return true to go on dragging, false to cancel it
     */
    @Override
    public boolean isDragging(final int x, final int y) {
        return true;
    }

    /**
     * Action when the user clicks the component once (Lookat).
     * 
     * @see illarion.client.world.Interactive#lookAt()
     */
    @Override
    public void lookAt() {
    }

    /**
     * Request to open a container on a component (middle mouse key).
     */
    @Override
    public void openContainer() {
    }

    /**
     * Enable or disable a hover effect on this component.
     * 
     * @param hover true if the hover shall be enabled, false if the hover shall
     *            be disabled.
     */
    @Override
    public void setHover(final boolean hover) {
    }

    /**
     * Set the new text of the tooltip.
     * 
     * @param newToolTip new text for the tooltip
     */
    public final void setTooltip(final String newToolTip) {
        toolTip = newToolTip;
        toolTipKey = null;
    }

    /**
     * Set the keyword of the tooltip.
     * 
     * @param newToolTipKey the new keyword for the tooltip
     */
    public final void setTooltipKey(final String newToolTipKey) {
        toolTipKey = newToolTipKey;
        toolTip = null;
    }

    /**
     * Action if the user uses the component as a item.
     * 
     * @return Reference to the object that was used
     */
    @Override
    public AbstractReference useItem() {
        return null;
    }

    /**
     * Get the change of the mouse wheel.
     * 
     * @param delta The change of the mouse wheel the object is informed about
     */
    @Override
    public void wheelIncrement(final int delta) {
    }
}
