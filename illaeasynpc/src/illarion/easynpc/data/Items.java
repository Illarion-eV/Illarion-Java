/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.data;

import java.util.ArrayList;

import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

/**
 * This class loads and stores all items defined in the client database along
 * with all the values required for the script parser to work properly.
 * 
 * @author Martin Karing
 * @since 1.00
 */
@SuppressWarnings("nls")
public final class Items {
    /**
     * The table index of the item id in the definition table.
     */
    protected static final int TB_ID = 0;

    /**
     * The list of items that were load.
     */
    private static final Items[] itemsList;

    static {
        final ArrayList<Items> itemList = new ArrayList<Items>();

        new TableLoader("Items", new TableLoaderSink() {
            @Override
            public boolean processRecord(final int line,
                final TableLoader loader) {
                final int itemId = loader.getInt(TB_ID);
                itemList.add(new Items(itemId));
                return true;
            }
        });

        itemsList = itemList.toArray(new Items[itemList.size()]);
    }

    /**
     * The ID of the item.
     */
    private final int itemId;

    /**
     * Protected constructor to store the item ID.
     * 
     * @param id the ID of the item
     */
    protected Items(final int id) {
        itemId = id;
    }

    /**
     * Get the values stored in this list. This creates a new list filled with
     * the references to the item object. The internal list remains untouched.
     * 
     * @return the array of references to the item objects
     */
    public static Items[] values() {
        final Items[] tempList = new Items[itemsList.length];
        System.arraycopy(itemsList, 0, tempList, 0, itemsList.length);
        return tempList;
    }

    /**
     * Get the ID of the item.
     * 
     * @return the ID of the item.
     */
    public int getItemId() {
        return itemId;
    }
}
