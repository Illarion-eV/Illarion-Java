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
 * A reference allows to point to some specified place or on some object the
 * game world or on the GUI in a way that it can be encoded for the network
 * interface.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public abstract class AbstractReference {
    /**
     * The ID of a character reference. Using this its possible to point on a
     * character.
     */
    public static final int CHARACTER = 5;

    /**
     * The ID of a container reference. This one allows to point on an item in a
     * container.
     */
    public static final int CONTAINER = 2;

    /**
     * The ID of a inventory reference. This one allows to point on an item in
     * the inventory of the player.
     */
    public static final int INVENTORY = 3;

    /**
     * The ID of a magic reference. As in a casted spell. It will encode the
     * player selected runes.
     */
    public static final int MAGIC = 6;

    /**
     * The ID of a map reference. This one allows to point in all ways possible
     * to the map.
     */
    public static final int MAP = 1;

    /**
     * A reference to a item displayed in a menu. The implementation of menus
     * sucks.
     * 
     * @deprecated not to be used anymore
     */
    @Deprecated
    public static final int MENU = 4;

    /**
     * The type ID of this reference.
     * 
     * @see #MAP
     * @see #CONTAINER
     * @see #INVENTORY
     * @see #MENU
     * @see #CHARACTER
     * @see #MAGIC
     */
    private final byte refID;

    /**
     * Create a instance of this reference and save the ID of it.
     * 
     * @param id the type ID of this reference instance
     * @see #MAP
     * @see #CONTAINER
     * @see #INVENTORY
     * @see #MENU
     * @see #CHARACTER
     * @see #MAGIC
     */
    protected AbstractReference(final int id) {
        refID = (byte) id;
    }

    /**
     * Encode the needed data to perform a use on this reference.
     * 
     * @param writer the interface that allows writing on the network connection
     */
    public abstract void encodeUse(NetCommWriter writer);

    /**
     * Get the reference type ID of this reference.
     * 
     * @return the reference type ID
     */
    public final int getId() {
        return refID;
    }

    /**
     * Encode the ID of the reference and write it to the network interface.
     * 
     * @param writer the interface that allows writing on the network connection
     */
    protected final void encodeID(final NetCommWriter writer) {
        writer.writeByte(refID);
    }
}
