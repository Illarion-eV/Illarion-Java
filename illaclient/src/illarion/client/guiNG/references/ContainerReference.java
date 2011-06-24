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
 * A container reference points at an item in a container of the player.
 * 
 * @author Blay09
 * @since 1.22
 */
public final class ContainerReference extends AbstractReference {
    /**
     * The container id of that container.
     */
    private byte containerID;

    /**
     * The container item id that is the source or the destination of the
     * dragging event.
     */
    private byte containerItem;

    /**
     * The target X coordinate.
     */
    private int targetX;

    /**
     * The target Y coordinate.
     */
    private int targetY;

    /**
     * Constructor to create a new instance of a container reference.
     */
    public ContainerReference() {
        super(AbstractReference.CONTAINER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encodeUse(final NetCommWriter writer) {
        writer.writeByte((byte) AbstractReference.CONTAINER);
        writer.writeByte(containerID);
        writer.writeByte(containerItem);
    }

    /**
     * Returns the container id of that reference's container.
     * 
     * @return the container id of that container
     */
    public byte getContainerID() {
        return containerID;
    }

    /**
     * Get the container item id that is the target or the destination of the
     * dragging event.
     * 
     * @return the container item id this reference refers to
     */
    public byte getReferringContainerItemID() {
        return containerItem;
    }

    /**
     * Returns the target x position.
     * 
     * @return the target x position
     */
    public int getTargetX() {
        return targetX;
    }

    /**
     * Returns the target y position.
     * 
     * @return the target y position
     */
    public int getTargetY() {
        return targetY;
    }

    /**
     * Sets the container id of that reference's container.
     * 
     * @param newContainerID the ID of the container
     */
    public void setContainerID(final byte newContainerID) {
        containerID = newContainerID;
    }

    /**
     * Set the container item id of the container that is the source or the
     * destination of the dragging event.
     * 
     * @param newContainerItemID the container item id of the item
     */
    public void setReferringContainerItemID(final byte newContainerItemID) {
        containerItem = newContainerItemID;
    }

    /**
     * Sets the target position of that reference.
     * 
     * @param x the target x coordinate
     * @param y the target y coordinate
     */
    public void setTargetPosition(final int x, final int y) {
        targetX = x;
        targetY = y;
    }

}
