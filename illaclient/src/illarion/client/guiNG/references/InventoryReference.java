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
package illarion.client.guiNG.references;

import illarion.client.net.NetCommWriter;

/**
 * A inventory reference points at a item in the inventory of the player
 * character.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class InventoryReference extends AbstractReference {
    /**
     * The inventory slot that is the source or the destination of the dragging
     * event.
     */
    private int slot;

    /**
     * Constructor to create a new instance of a inventory reference.
     */
    public InventoryReference() {
        super(AbstractReference.INVENTORY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encodeUse(final NetCommWriter writer) {
        writer.writeByte((byte) AbstractReference.INVENTORY);
        writer.writeByte((byte) slot);
    }

    /**
     * Get the slot that is the target or the destination of the dragging event.
     * 
     * @return the inventory slot this reference refers to
     */
    public int getReferringSlot() {
        return slot;
    }

    /**
     * Set the slot of the inventory that is the source or the destination of
     * the dragging event.
     * 
     * @param newSlot the slot of the inventory
     */
    public void setReferringSlot(final int newSlot) {
        slot = newSlot;
    }
}
