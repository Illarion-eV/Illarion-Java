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
package illarion.client.net.server;

import java.io.IOException;
import java.util.Arrays;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;

/**
 * Servermessage: Menu with items (
 * {@link illarion.client.net.CommandList#MSG_MENU}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class MenuMsg extends AbstractReply {
    /**
     * Default size of the array that stores the item IDs of the menus. The size
     * is increased automatically in case its needed.
     */
    private static final int DEFAULT_SIZE = 24;

    /**
     * IDs of the items in this menu.
     */
    private int[] itemId = new int[DEFAULT_SIZE];

    /**
     * Amount of items visible in the menu.
     */
    private short size;

    /**
     * Default constructor for the menu message.
     */
    public MenuMsg() {
        super(CommandList.MSG_MENU);
    }

    /**
     * Create a new instance of the menu message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public MenuMsg clone() {
        return new MenuMsg();
    }

    /**
     * Decode the menu data the receiver got and prepare it for the execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        size = reader.readUByte();

        // check if the array is large enough and increase the size if needed.
        if (size > itemId.length) {
            itemId = new int[size];
        }

        for (int i = 0; i < size; i++) {
            itemId[i] = reader.readUShort();
        }
    }

    /**
     * Execute the menu message and send the decoded data to the rest of the
     * client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        // close existing menus
        // Gui.getInstance().getManager().closeMenus();

        // update menu and open it
        // Gui.getInstance().getContainers()
        // .updateContainer(Containers.MENU, size, itemId, null);
        return true;
    }

    /**
     * Get the data of this menu message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("size: " + size + " Content: "
            + Arrays.toString(itemId));
    }
}
