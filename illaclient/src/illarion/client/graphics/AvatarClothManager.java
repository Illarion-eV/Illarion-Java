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
package illarion.client.graphics;

/**
 * The Cloth manager stores all known clothes and their locations for each known
 * avatar. And allows accessing the cloth definitions.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class AvatarClothManager {
    /**
     * The index of the group of clothes that contains the beards types this
     * avatar could wear.
     */
    public static final int GROUP_BEARD; // NO_UCD

    /**
     * The index of the groups of all chest parts, such as shirts, armors and so
     * on this avatar can wear.
     */
    public static final int GROUP_CHEST; // NO_UCD

    /**
     * The index of the groups of all coats a avatar can wear. Such as coats,
     * capes, dresses.
     */
    public static final int GROUP_COAT; // NO_UCD

    /**
     * The amount of groups that exist to sort the paper dolling objects in.
     */
    public static final int GROUP_COUNT; // NO_UCD

    /**
     * The index of the group of items the avatar can wear in the right hand.
     * Tools weapons and similar things could be listed there.
     */
    public static final int GROUP_FIRST_HAND; // NO_UCD

    /**
     * The index of the group of clothes that contains the hair types this
     * avatar could wear.
     */
    public static final int GROUP_HAIR; // NO_UCD

    /**
     * The index of the groups of all hats this avatar can wear.
     */
    public static final int GROUP_HAT; // NO_UCD

    /**
     * The index of the group of items the avatar can wear in the left hand.
     * Tools weapons and similar things could be listed there.
     */
    public static final int GROUP_SECOND_HAND; // NO_UCD

    /**
     * The index of the group of shoes and boots a avatar can wear.
     */
    public static final int GROUP_SHOES; // NO_UCD

    /**
     * The index of the group of trousers a avatar can wear.
     */
    public static final int GROUP_TROUSERS; // NO_UCD

    static {
        int cnt = 0;
        GROUP_HAIR = cnt++;
        GROUP_BEARD = cnt++;
        GROUP_HAT = cnt++;
        GROUP_CHEST = cnt++;
        GROUP_COAT = cnt++;
        GROUP_FIRST_HAND = cnt++;
        GROUP_SECOND_HAND = cnt++;
        GROUP_TROUSERS = cnt++;
        GROUP_SHOES = cnt++;

        GROUP_COUNT = cnt;
    }

    /**
     * The storage for the known clothes of each group.
     */
    private final AvatarClothFactory[] exsistingClothes;

    /**
     * Constructor for this class. Each avatar needs one instance of this class
     * to get the paper dolling working.
     */
    public AvatarClothManager() {
        exsistingClothes = new AvatarClothFactory[GROUP_COUNT];
    }

    /**
     * Add a item to the storage. It has to be sorted into a group here. Its
     * only possible to define a itemID once per group. But a single item can be
     * defined in multiple groups.
     * 
     * @param group the group the item shall be assigned to
     * @param cloth the definition of the cloth itself
     */
    @SuppressWarnings("nls")
    public void addCloth(final int group, final AvatarCloth cloth) {
        if ((group < 0) || (group >= GROUP_COUNT)) {
            throw new IllegalArgumentException(
                "Group needs to be between 0 and " + GROUP_COUNT);
        }
        if (exsistingClothes[group] == null) {
            exsistingClothes[group] = new AvatarClothFactory();
        }
        exsistingClothes[group].registerCloth(cloth);
    }

    /**
     * Check if a cloth item is defined on a specified location.
     * 
     * @param group the location of the cloth
     * @param itemID the item id of the item that shall be checked
     * @return <code>true</code> in case the cloth item is defined for that
     *         location
     */
    @SuppressWarnings("nls")
    public boolean clothExists(final int group, final int itemID) {
        if ((group < 0) || (group >= GROUP_COUNT)) {
            throw new IllegalArgumentException(
                "Group needs to be between 0 and " + GROUP_COUNT);
        }
        if (exsistingClothes[group] == null) {
            return false;
        }
        int refID;
        Item refItem;

        if ((group == GROUP_HAIR) || (group == GROUP_BEARD)) {
            refID = itemID;
            refItem = null;
        } else {
            refItem = ItemFactory.getInstance().getPrototype(itemID);
            refID = refItem.getPaperdollingId();
        }
        return exsistingClothes[group].prototypeExists(refID);
    }

    /**
     * Cleanup all internal data for the actual client runtime. This method is
     * called after the loading is done to optimize the internal storages.
     */
    public void finish() {
        for (final AvatarClothFactory factory : exsistingClothes) {
            if (factory != null) {
                factory.finish();
            }
        }
    }

    /**
     * Get a item from the storage.
     * 
     * @param group the group the requested item is assigned to
     * @param itemID the item id of the requested item
     * @return the item that was found regarding the parameters or
     *         <code>null</code> in case the requested item was not defined in
     *         this storage
     */
    @SuppressWarnings("nls")
    public AvatarCloth getCloth(final int group, final int itemID) {
        if ((group < 0) || (group >= GROUP_COUNT)) {
            throw new IllegalArgumentException(
                "Group needs to be between 0 and " + GROUP_COUNT);
        }
        if (exsistingClothes[group] == null) {
            return null;
        }
        int refID;
        Item refItem;

        if ((group == GROUP_HAIR) || (group == GROUP_BEARD)) {
            refID = itemID;
            refItem = null;
        } else {
            refItem = ItemFactory.getInstance().getPrototype(itemID);
            refID = refItem.getPaperdollingId();
        }
        final AvatarCloth ret = exsistingClothes[group].getCommand(refID);
        if (ret.getId() == 0) {
            ret.recycle();
            return null;
        }
        if (refItem != null) {
            ret.setBaseColor(refItem.getPaperdollingColor());
        }
        return ret;
    }
}
