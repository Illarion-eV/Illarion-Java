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

import illarion.client.net.CommandList;
import illarion.common.annotation.NonNull;
import illarion.common.net.NetCommWriter;
import net.jcip.annotations.Immutable;

/**
 * This command is used to tell the server that the player is using a item in the inventory.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class UseInventoryCmd extends AbstractCommand {
    /**
     * The inventory slot that is used.
     */
    private final short slot;

    /**
     * Default constructor for the use command.
     *
     * @param inventorySlot the inventory slot that is used
     */
    public UseInventoryCmd(final int inventorySlot) {
        super(CommandList.CMD_USE);

        slot = (short) inventorySlot;
    }

    @Override
    public void encode(@NonNull final NetCommWriter writer) {
        writer.writeUByte((short) 3); // INVENTORY REFERENCE
        writer.writeUByte(slot);
    }

    @NonNull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Slot: " + slot);
    }
}
