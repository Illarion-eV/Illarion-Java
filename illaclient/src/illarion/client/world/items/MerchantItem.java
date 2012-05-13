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
package illarion.client.world.items;

/**
 * This class is able to store a single item that is sold by a NPC merchant.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class MerchantItem {
    /**
     * The index of the item inside the list of items as it was transferred.
     */
    private final int index;

    /**
     * The ID of the item.
     */
    private final int itemId;

    /**
     * The name that is supposed to be displayed along with the item.
     */
    private final String name;

    /**
     * The price of the item in copper coins.
     */
    private final long price;

    /**
     * Create a new instance of that merchant item.
     *
     * @param itemIndex the index of the item in the list of items that was send by the server
     * @param id        the ID of the item
     * @param itemName  the name of the item
     * @param itemPrice the price of the item in copper coins
     */
    public MerchantItem(final int itemIndex, final int id, final String itemName, final long itemPrice) {
        index = itemIndex;
        itemId = id;
        name = itemName;
        price = itemPrice;
    }
}
