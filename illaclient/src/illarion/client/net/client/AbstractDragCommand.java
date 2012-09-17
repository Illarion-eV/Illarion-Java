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
package illarion.client.net.client;

import illarion.common.types.ItemCount;

/**
 * This abstract command contains the shared code of all dragging operations.
 *
 * @author Martin Karing &lt;nitram@illlarion.org&gt;
 */
public abstract class AbstractDragCommand extends AbstractCommand {
    /**
     * The amount of items that are supposed to be moved.
     */
    private ItemCount count;

    /**
     * The constructor of a command. This is used to set the ID of the command.
     *
     * @param commId the ID of the command
     */
    protected AbstractDragCommand(final int commId) {
        super(commId);
    }

    /**
     * Get the amount of items that are supposed to be moved.
     *
     * @return the items to be moved
     */
    protected final ItemCount getCount() {
        return count;
    }

    /**
     * Set how many items are supposed to be moved.
     *
     * @param count the amount of items to move
     */
    public final void setCount(final ItemCount count) {
        this.count = count;
    }
}
