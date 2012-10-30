/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.server.events;

/**
 * This event is send once the server sends a crafting update dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class DialogCraftingUpdateReceivedEvent {
    /**
     * This enumerator holds the possible update values for the crafting dialog.
     */
    public enum UpdateType {
        /**
         * This update type means that the players now starts crafting now.
         */
        Start,

        /**
         * This update type means that the player completed the crafting.
         */
        Completed,

        /**
         * This update type means that the crafting was aborted.
         */
        Aborted
    }

    /**
     * The ID of the dialog.
     */
    private final int requestId;

    /**
     * This is the type of the update.
     */
    private final UpdateType type;

    /**
     * The time in 1s/10 required to finish the operation.
     */
    private final int requiredTime;

    /**
     * Create a new instance of this event.
     *
     * @param requestId    the ID of the dialog that is updated
     * @param type         the type of the update
     * @param requiredTime the time required to finish the operation started with this update
     * @throws IllegalArgumentException in case the {@code requiredTime} is less then {@code 0} or the
     *                                  {@code requiredTime} is larger then zero while the {@code type} is not
     *                                  {@link UpdateType#Start}.
     */
    public DialogCraftingUpdateReceivedEvent(final int requestId, final UpdateType type, final int requiredTime) {
        if (requiredTime < 0) {
            throw new IllegalArgumentException("The time must not be less then zero");
        }

        if ((type != UpdateType.Start) && (requiredTime > 0)) {
            throw new IllegalArgumentException("Only the start events is allowed to contain tiles.");
        }

        this.requestId = requestId;
        this.type = type;
        this.requiredTime = requiredTime;
    }

    /**
     * Create a new instance of this event.
     *
     * @param requestId the ID of the dialog that is updated
     * @param type      the type of the update
     */
    public DialogCraftingUpdateReceivedEvent(final int requestId, final UpdateType type) {
        this(requestId, type, 0);
    }

    /**
     * Get the ID of this dialog.
     *
     * @return the ID of the dialog
     */
    public int getId() {
        return requestId;
    }

    /**
     * Get the type of this update event.
     *
     * @return the type of this event
     */
    public UpdateType getType() {
        return type;
    }

    /**
     * Get the required time of this event.
     *
     * @return the time required for this event
     * @throws IllegalStateException in case the {@link #getType()} is not {@link UpdateType#Start}
     */
    public int getRequiredTime() {
        if (type == UpdateType.Start) {
            return requiredTime;
        }
        throw new IllegalStateException("The required time is only valid for the START event.");
    }
}
